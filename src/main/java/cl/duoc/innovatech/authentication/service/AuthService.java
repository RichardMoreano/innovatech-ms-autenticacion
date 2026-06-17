package cl.duoc.innovatech.authentication.service;

import cl.duoc.innovatech.authentication.dto.LoginRequest;
import cl.duoc.innovatech.authentication.dto.AuthResponse;

public interface AuthService {
    AuthResponse authenticate(LoginRequest request);
}