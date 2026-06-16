package cl.duoc.innovatech.authentication.infrastructure.controller;

import cl.duoc.innovatech.authentication.config.JwtUtil;
import cl.duoc.innovatech.authentication.domain.Usuario;
import cl.duoc.innovatech.authentication.dto.LoginRequest;
import cl.duoc.innovatech.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.is;

// Test del AuthController usando MockMvc en modo standalone para evitar dependencias de anotaciones
class AuthControllerTest {

    private MockMvc mockMvc;

    private UserRepository userRepository;

    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        // Creamos los mocks manualmente para no depender de @WebMvcTest y @MockitoBean
        // Creamos un UserRepository dinámico por proxy para evitar dependencia de Mockito/ByteBuddy
        userRepository = (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class[]{UserRepository.class},
                new InvocationHandler() {
                    private Optional<Usuario> configured = Optional.empty();
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String name = method.getName();
                        if ("findByUsername".equals(name) && args != null && args.length == 1) {
                            return configured;
                        }
                        if ("save".equals(name) && args != null && args.length == 1) {
                            return args[0];
                        }
                        // Métodos no usados en tests: lanzar excepción para detectar usos inesperados
                        throw new UnsupportedOperationException("Method not implemented in test proxy: " + name);
                    }
                }
        );

        // JwtUtil real con secret de prueba
        jwtUtil = new JwtUtil();
        org.springframework.test.util.ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-which-is-long-enough-0123456789abcd");
        org.springframework.test.util.ReflectionTestUtils.setField(jwtUtil, "expirationMillis", 3600000L);

        // Montamos el controller con MockMvc standalone
        AuthController controller = new AuthController(userRepository, jwtUtil);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void login_withValidCredentials_returns200AndToken() throws Exception {
        // Mockeamos el repositorio para simular que el usuario sí existe en la BD
        Usuario user = new Usuario();
        user.setUsername("admin");
        user.setPassword(encoder.encode("123456"));
        user.setRole("ADMIN");

    // reemplazamos el proxy por uno que devuelve el usuario para este test
    UserRepository repoWithUser = (UserRepository) Proxy.newProxyInstance(
        UserRepository.class.getClassLoader(),
        new Class[]{UserRepository.class},
        (proxy, method, args) -> {
            if ("findByUsername".equals(method.getName())) return Optional.of(user);
            if ("save".equals(method.getName())) return args[0];
            throw new UnsupportedOperationException(method.getName());
        }
    );
    // Re-configuramos el controller under test with the repo that has the user
    AuthController controller = new AuthController(repoWithUser, jwtUtil);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        String body = "{\"username\":\"admin\",\"password\":\"123456\"}";

    // Ejecutamos y capturamos la respuesta
    String response = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    // Extraemos el token JSON y validamos su payload
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response);
    String token = root.get("token").asText();
    assertNotNull(token);
    // Decodificamos la parte central del JWT para verificar el subject
    String[] parts = token.split("\\.");
    assertTrue(parts.length >= 2);
    byte[] decoded = java.util.Base64.getUrlDecoder().decode(parts[1]);
    String payloadJson = new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
    com.fasterxml.jackson.databind.JsonNode payload = mapper.readTree(payloadJson);
    org.junit.jupiter.api.Assertions.assertEquals("admin", payload.get("sub").asText());
    }

    @Test
    void login_withInvalidCredentials_returns401() throws Exception {
        // Simulamos que el usuario no existe
    UserRepository repoEmpty = (UserRepository) Proxy.newProxyInstance(
        UserRepository.class.getClassLoader(),
        new Class[]{UserRepository.class},
        (proxy, method, args) -> {
            if ("findByUsername".equals(method.getName())) return Optional.empty();
            if ("save".equals(method.getName())) return args[0];
            throw new UnsupportedOperationException(method.getName());
        }
    );
    AuthController controllerEmpty = new AuthController(repoEmpty, jwtUtil);
    mockMvc = MockMvcBuilders.standaloneSetup(controllerEmpty).build();

        String body = "{\"username\":\"baduser\",\"password\":\"wrong\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_withNewUser_returns201() throws Exception {
    UserRepository repoForRegister = (UserRepository) Proxy.newProxyInstance(
        UserRepository.class.getClassLoader(),
        new Class[]{UserRepository.class},
        (proxy, method, args) -> {
            if ("findByUsername".equals(method.getName())) return Optional.empty();
            if ("save".equals(method.getName())) return args[0];
            throw new UnsupportedOperationException(method.getName());
        }
    );
    AuthController controllerForRegister = new AuthController(repoForRegister, jwtUtil);
    mockMvc = MockMvcBuilders.standaloneSetup(controllerForRegister).build();
        String body = "{\"username\":\"nuevaUser\",\"password\":\"papitafrita2\"}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void register_withExistingUser_returns400() throws Exception {
    Usuario existing = new Usuario();
    existing.setUsername("existente");
    existing.setPassword(encoder.encode("claveSegura123"));
    existing.setRole("ROLE_USER");

    UserRepository repoWithExisting = (UserRepository) Proxy.newProxyInstance(
        UserRepository.class.getClassLoader(),
        new Class[]{UserRepository.class},
        (proxy, method, args) -> {
            if ("findByUsername".equals(method.getName())) return Optional.of(existing);
            if ("save".equals(method.getName())) return args[0];
            throw new UnsupportedOperationException(method.getName());
        }
    );
    AuthController controllerWithExisting = new AuthController(repoWithExisting, jwtUtil);
    mockMvc = MockMvcBuilders.standaloneSetup(controllerWithExisting).build();

    String body = "{\"username\":\"existente\",\"password\":\"papitafrita2\"}";

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("El nombre de usuario ya está tomado"));
    }
}
