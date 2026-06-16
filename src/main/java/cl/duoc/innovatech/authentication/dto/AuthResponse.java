package cl.duoc.innovatech.authentication.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String type = "Bearer";

    public AuthResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getType() { return type; }
}
