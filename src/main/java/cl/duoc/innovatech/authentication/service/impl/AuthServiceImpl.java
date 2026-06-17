package cl.duoc.innovatech.authentication.service.impl;

import cl.duoc.innovatech.authentication.dto.LoginRequest;
import cl.duoc.innovatech.authentication.model.Usuario;
import cl.duoc.innovatech.authentication.dto.AuthResponse;
import cl.duoc.innovatech.authentication.repository.UsuarioRepository;
import cl.duoc.innovatech.authentication.service.AuthService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Inyecta ambos componentes requeridos
    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // Buscamos en la BD que el usuario exista
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Comparamos contraseñas usando el encoder inyectado
        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Retornamos un token estructurado para que el Gateway lo pueda leer después
        String tokenSimulado = "mock-jwt-token-v2-" + usuario.getRol();
        return new AuthResponse(tokenSimulado, usuario.getEmail(), usuario.getRol());
    }
}