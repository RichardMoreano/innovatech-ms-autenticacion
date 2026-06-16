package cl.duoc.innovatech.authentication.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Password cifrado con BCrypt desde el servicio

    @Column(nullable = false)
    private String role;

    // Constructor personalizado omitiendo el ID (JPA lo genera automáticamente)
    public Usuario(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}