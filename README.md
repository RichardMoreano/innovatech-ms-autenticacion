# Innovatech Solutions - MS Autenticación

Microservicio basado en Spring Boot responsable de la identidad de los usuarios, control de acceso perimetral y la emisión/validación de tokens JWT dentro del ecosistema Innovatech.

## Resumen Técnico

- **Nombre del Módulo:** `innovatech-ms-autenticacion` (Contenedor: `ms-auth`)
- **Tecnologías Core:** Java 17, Spring Boot, Spring Security, Spring Data JPA, JSON Web Tokens (JWT).
- **Puerto Base (Host):** `8082` (Enrutado internamente desde el API Gateway en el puerto `8083`).
- **Base de Datos:** PostgreSQL 15 (`innovatech_db`).

---

## Estructura del Proyecto

- `src/main/java/.../controller/v2/AuthController.java` — Controladores REST para endpoints públicos de sesión.
- `src/main/java/.../service/` — Capa de negocio (`AuthService` y `AuthServiceImpl`).
- `src/main/java/.../repository/UsuarioRepository.java` — Abstracción de datos con `JpaRepository`.
- `src/main/java/.../model/Usuario.java` — Entidad JPA mapeada para persistencia relacional.
- `src/main/java/.../util/JwtUtils.java` — Componente utilitario de firma y parseo HMAC-SHA de tokens.
- `src/main/java/.../config/SecurityConfig.java` — Configuración de Spring Security con política de sesión **Stateless**.
- `src/main/resources/application.properties` — Configuración base de la aplicación.

---

## Contratos de API (Endpoints)

**Ruta Base:** `/api/v2/auth`

### 1. Autenticación de Usuario

- **Método:** `POST`
- **Path:** `/api/v2/auth/login`

#### Payload de Entrada

```json
{
  "email": "admin@innovatech.com",
  "password": "supersecretpassword"
}
```

#### Respuesta Exitosa (200 OK)

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "admin@innovatech.com",
  "rol": "ROLE_USER"
}
```

#### Códigos de Error

- `401 Unauthorized` - Credenciales incorrectas o usuario no encontrado.

---

### 2. Registro de Usuario

- **Método:** `POST`
- **Path:** `/api/v2/auth/register`

#### Payload de Entrada

```json
{
  "email": "nuevo@innovatech.com",
  "password": "password123",
  "nombre": "Richard Moreano",
  "rol": "ROLE_USER"
}
```

#### Respuesta Exitosa (201 Created)

```json
{
  "message": "Usuario registrado exitosamente",
  "email": "nuevo@innovatech.com"
}
```

#### Códigos de Error

- `400 Bad Request` - Email ya registrado o errores de validación.

#### Validaciones

Se aplican restricciones automáticas mediante Jakarta Validation:

- `@NotBlank`
- `@Email`
- `@Size`

---

## Parámetros de Configuración y Seguridad

### Criptografía

Las contraseñas se almacenan utilizando hash seguro mediante **BCrypt** (`PasswordEncoder`).

### Variables de Entorno Requeridas

| Propiedad Spring | Variable de Entorno | Descripción |
|------------------|--------------------|-------------|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | String de conexión JDBC PostgreSQL. |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | Usuario administrador de la base de datos. |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos. |
| `jwt.secret` | `JWT_SECRET` | Clave HMAC-SHA de al menos 256 bits. |

---

# Instrucciones de Ejecución

## Requisitos Mínimos

- Java JDK 17
- Apache Maven 3.8+
- PostgreSQL activo (local o mediante contenedor)

---

## Opción 1: Desarrollo Local con Maven

```bash
# Navegar al directorio del proyecto
cd innovatech-ms-autenticacion

# Ejecutar la aplicación
mvn spring-boot:run
```

---

## Opción 2: Compilación y Ejecución del Artefacto

### Compilar

```bash
mvn clean package -DskipTests
```

### Ejecutar

```bash
java -jar target/innovatech-ms-autenticacion-0.0.1-SNAPSHOT.jar
```

---

## Opción 3: Ejecución con Docker

### Construir imagen

```bash
docker build -t innovatech-ms-autenticacion .
```

### Ejecutar contenedor

```bash
docker run -p 8082:8082 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/innovatech_db" \
  -e SPRING_DATASOURCE_USERNAME="admin" \
  -e SPRING_DATASOURCE_PASSWORD="supersecretpassword" \
  -e JWT_SECRET="innovatech2026_fullstack3_RichardMoreano" \
  innovatech-ms-autenticacion
```

---

# Pruebas Unitarias

El proyecto incorpora pruebas unitarias e integración utilizando **JUnit 5**.

### Ejecutar pruebas

```bash
mvn test
```

---

# Flujo de Integración en el Ecosistema

1. El **API Gateway** recibe solicitudes dirigidas a:

```text
/api/v2/auth/**
```

2. El Gateway redirige internamente las peticiones hacia:

```text
http://ms-auth:8082
```

3. El microservicio autentica al usuario y emite un token JWT firmado.

4. En solicitudes posteriores, el API Gateway valida la firma del JWT antes de permitir el acceso a los servicios protegidos.

5. Una vez validado el token, el Gateway enruta la solicitud hacia el BFF o el microservicio correspondiente.