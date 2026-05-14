package model;

public class Usuario {
    private int id;
    private String username;
    private String passwordHash;  // hash + salt separados por ":"
    private String nombreCompleto;
    private String email;
    private String rol;

    // Constructor vacío
    public Usuario() {}

    // Constructor completo
    public Usuario(int id, String username, String passwordHash, String nombreCompleto, String email, String rol) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

        public boolean esProfesor() {
        return "PROFESOR".equals(rol);
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", username='" + username + "', nombre='" + nombreCompleto + "', rol='" + rol + "'}";
    }
}
