package cl.duoc.innovatech.authentication.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void usuario_gettersAndSetters_shouldWork() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setUsername("Richard");
        u.setPassword("papitafrita2");
        u.setRole("ROLE_USER");

        assertEquals(1L, u.getId());
        assertEquals("Richard", u.getUsername());
        assertEquals("papitafrita2", u.getPassword());
        assertEquals("ROLE_USER", u.getRole());
    }
}
