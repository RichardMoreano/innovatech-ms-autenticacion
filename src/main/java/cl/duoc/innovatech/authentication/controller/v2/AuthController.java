package cl.duoc.innovatech.authentication.controller.v2;

import cl.duoc.innovatech.authentication.dto.LoginRequest;
import cl.duoc.innovatech.authentication.dto.RegisterRequest;
import cl.duoc.innovatech.authentication.dto.RegisterResponse;
import cl.duoc.innovatech.authentication.model.Usuario;
import cl.duoc.innovatech.authentication.repository.UsuarioRepository;
import cl.duoc.innovatech.authentication.dto.AuthResponse;
import cl.duoc.innovatech.authentication.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegisterRequest request) { // <-- Usa RegisterRequest
        // 1. Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Error: El correo ya está registrado.");
        }

        // 2. Mapear DTO a Entidad
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setRol(request.getRol());

        // 3. Guardar en Base de Datos
        usuarioRepository.save(usuario);

        // 4. Retornar DTO de Respuesta
        RegisterResponse response = new RegisterResponse("Usuario registrado exitosamente", usuario.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}