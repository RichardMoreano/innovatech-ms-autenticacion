package cl.duoc.innovatech.authentication.infrastructure.controller;

import cl.duoc.innovatech.authentication.config.JwtUtil;
import cl.duoc.innovatech.authentication.domain.Usuario;
import cl.duoc.innovatech.authentication.dto.LoginRequest;
import cl.duoc.innovatech.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

// Test del AuthController usando MockMvc. Cubrimos login válido (200) y inválido (401)
@WebMvcTest(controllers = cl.duoc.innovatech.authentication.infrastructure.controller.AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        // Nothing for now
    }

    @Test
    void login_withValidCredentials_returns200AndToken() throws Exception {
        // Mockeamos el repositorio para simular que el usuario sí existe en la BD
        Usuario user = new Usuario();
        user.setUsername("admin");
        user.setPassword(encoder.encode("123456"));
        user.setRole("ADMIN");

        Mockito.when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        Mockito.when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mocked-jwt-token");

        String body = "{\"username\":\"admin\",\"password\":\"123456\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("mocked-jwt-token")));
    }

    @Test
    void login_withInvalidCredentials_returns401() throws Exception {
        // Simulamos que el usuario no existe
        Mockito.when(userRepository.findByUsername("baduser")).thenReturn(Optional.empty());

        String body = "{\"username\":\"baduser\",\"password\":\"wrong\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
