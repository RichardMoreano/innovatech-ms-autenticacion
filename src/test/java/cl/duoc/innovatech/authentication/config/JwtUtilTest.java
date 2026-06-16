package cl.duoc.innovatech.authentication.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas unitarias para JwtUtil: generamos un token y verificamos su contenido
class JwtUtilTest {

    @Test
    void generateToken_shouldContainSubjectAndRole() {
        // Creamos la instancia y seteamos secret y expiración por reflexión
        JwtUtil jwtUtil = new JwtUtil();

        String secret = "test-secret-which-is-long-enough-0123456789abcd";
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expirationMillis", 3600000L);

        String token = jwtUtil.generateToken("testuser", "ADMIN");
        assertNotNull(token, "El token no debe ser nulo");

        // Parseamos el token con la misma clave para verificar claims
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        assertEquals("testuser", claims.getSubject(), "El subject debe ser el username");
        assertEquals("ADMIN", claims.get("role", String.class), "El claim role debe existir");
    }
}
