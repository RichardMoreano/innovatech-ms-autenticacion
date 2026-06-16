package cl.duoc.innovatech.authentication.infrastructure.config;

import cl.duoc.innovatech.authentication.domain.Usuario;
import cl.duoc.innovatech.authentication.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner init(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        return args -> {
            // Creamos un usuario de prueba si la tabla está vacía
            if (userRepository.count() == 0) {
                Usuario u = new Usuario();
                u.setUsername("admin");
                u.setPassword(encoder.encode("123456"));
                u.setRole("ADMIN");
                userRepository.save(u);
            }
        };
    }
}
