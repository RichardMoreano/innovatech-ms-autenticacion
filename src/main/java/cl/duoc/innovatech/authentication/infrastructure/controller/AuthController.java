package cl.duoc.innovatech.authentication.infrastructure.controller;

import cl.duoc.innovatech.authentication.config.JwtUtil;
import cl.duoc.innovatech.authentication.dto.AuthResponse;
import cl.duoc.innovatech.authentication.dto.LoginRequest;
import cl.duoc.innovatech.authentication.domain.Usuario;
import cl.duoc.innovatech.authentication.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Buscamos el usuario en la base de datos para validar su contraseña antes de soltar el token
        Optional<Usuario> maybeUser = userRepository.findByUsername(request.getUsername());

        if (maybeUser.isEmpty()) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        Usuario user = maybeUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        AuthResponse response = new AuthResponse(token, user.getUsername());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        // Verificamos si ya existe el usuario
        Optional<Usuario> existing = userRepository.findByUsername(request.getUsername());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está tomado");
        }

        // Ciframos la contraseña recibida
        String encoded = passwordEncoder.encode(request.getPassword());

        Usuario user = new Usuario();
        user.setUsername(request.getUsername());
        user.setPassword(encoded);
        user.setRole("ROLE_USER");

        userRepository.save(user);

        return ResponseEntity.status(201).build();
    }
}
