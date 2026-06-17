package cl.duoc.innovatech.autenticacion.controller.v2;

import cl.duoc.innovatech.autenticacion.dto.LoginRequest;
import cl.duoc.innovatech.autenticacion.dto.AuthResponse;
import cl.duoc.innovatech.autenticacion.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}