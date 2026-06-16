package cl.duoc.innovatech.authentication.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret:mi-clave-secreta-autenticacion}")
    private String secret;

    @Value("${security.jwt.expiration:3600000}")
    private long expirationMillis;

    // Generamos un JWT simple con subject = username y claim role
    public String generateToken(String username, String role) {
    Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMillis);

    // Usamos la API fluida moderna de JJWT 0.12+: subject(), issuedAt(), expiration() y signWith(key)
    return Jwts.builder()
        .subject(username)
        .claim("role", role)
        .issuedAt(now)
        .expiration(exp)
        .signWith(key)
        .compact();
    }
}
