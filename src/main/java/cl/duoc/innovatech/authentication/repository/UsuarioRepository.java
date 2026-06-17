package cl.duoc.innovatech.authentication.repository;

import cl.duoc.innovatech.authentication.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Spring Boot genera la query automáticamente gracias al Query Method
    Optional<Usuario> findByEmail(String email);
}