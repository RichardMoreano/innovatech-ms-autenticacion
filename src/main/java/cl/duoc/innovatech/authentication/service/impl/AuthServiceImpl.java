package cl.duoc.innovatech.authentication.service.impl;

import cl.duoc.innovatech.authentication.dto.LoginRequest;
import cl.duoc.innovatech.authentication.model.Usuario;
import cl.duoc.innovatech.authentication.dto.AuthResponse;
import cl.duoc.innovatech.authentication.repository.UsuarioRepository;
import cl.duoc.innovatech.authentication.service.AuthService;
import cl.duoc.innovatech.authentication.util.JwtUtils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // 2. INYECTARLO EN EL CONSTRUCTOR
    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
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

        // Retornamos un token criptográfico real estructurado para el Gateway
        String tokenReal = jwtUtils.generarToken(usuario);
        return new AuthResponse(tokenReal, usuario.getEmail(), usuario.getRol());
    }
}