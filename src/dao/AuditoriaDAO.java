package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AuditoriaDAO {

    public void registrar(int usuarioId, String accion, String entidad, int entidadId, String detalle) {
        String sql = "INSERT INTO auditoria (usuario_id, accion, entidad, entidad_id, detalle, fecha) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setString(2, accion);
            stmt.setString(3, entidad);
            stmt.setInt(4, entidadId);
            stmt.setString(5, detalle);
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error al registrar auditoría: " + e.getMessage());
        }
    }
}
