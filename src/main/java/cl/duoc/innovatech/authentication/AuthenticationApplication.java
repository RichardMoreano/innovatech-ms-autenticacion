package cl.duoc.innovatech.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthenticationApplication {

    // Arrancamos el microservicio de autenticación en el puerto 8083
    // Este servicio será responsable de gestionar usuarios y emitir JWTs.
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }
}
