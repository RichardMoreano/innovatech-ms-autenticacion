package cl.duoc.innovatech.authentication.service;

import cl.duoc.innovatech.authentication.dto.LoginRequest;
import cl.duoc.innovatech.authentication.dto.AuthResponse;
import cl.duoc.innovatech.authentication.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public AuthResponse authenticate(LoginRequest request) {
        // Mock temporal para asegurar flujo frontend-backend antes de JWT real
        if ("admin@innovatech.cl".equals(request.getEmail()) && "password123".equals(request.getPassword())) {
            return new AuthResponse("mock-jwt-token-v2", request.getEmail(), "ROLE_ADMIN");
        }
        throw new RuntimeException("Credenciales inválidas (Simulado)");
    }
}