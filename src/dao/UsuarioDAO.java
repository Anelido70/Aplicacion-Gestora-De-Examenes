package dao;

import database.DatabaseConnection;
import model.Usuario;
import java.sql.*;

public class UsuarioDAO {

    // Buscar usuario por username (para el login)
    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        }
        return null;
    }

    // Insertar nuevo usuario (con rol)
    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password_hash, nombre_completo, email, rol) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPasswordHash());
            stmt.setString(3, usuario.getNombreCompleto());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getRol() != null ? usuario.getRol() : "ALUMNO");
            
            return stmt.executeUpdate() > 0;
        }
    }

    // Verificar si un username ya existe
    public boolean existeUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Mapear ResultSet a objeto Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setNombreCompleto(rs.getString("nombre_completo"));
        usuario.setEmail(rs.getString("email"));
        // Manejar el caso de que la columna rol no exista (compatibilidad)
        try {
            usuario.setRol(rs.getString("rol"));
        } catch (SQLException e) {
            usuario.setRol("PROFESOR");
        }
        return usuario;
    }
    public String buscarPorId(int id) throws SQLException {
    String sql = "SELECT username FROM usuarios WHERE id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("username");
        }
    }
    return null;
}
}
