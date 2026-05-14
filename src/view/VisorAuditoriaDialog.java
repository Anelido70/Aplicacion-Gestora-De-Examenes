package view;

import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VisorAuditoriaDialog {

    public VisorAuditoriaDialog(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Registro de Auditoría", true);
        dialog.setSize(850, 500);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        String[] columnas = {"ID", "Usuario", "Acción", "Entidad", "ID Entidad", "Detalle", "Fecha"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(tabla);
        dialog.add(scroll, BorderLayout.CENTER);

        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnCerrar = new JButton("❌ Cerrar");
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> cargarAuditoria(modeloTabla));
        btnCerrar.addActionListener(e -> dialog.dispose());

        cargarAuditoria(modeloTabla);
        dialog.setVisible(true);
    }

    private void cargarAuditoria(DefaultTableModel modeloTabla) {
        modeloTabla.setRowCount(0);
        try {
            String sql = "SELECT a.*, u.username FROM auditoria a " +
                        "JOIN usuarios u ON a.usuario_id = u.id ORDER BY a.fecha DESC LIMIT 200";
            java.sql.Connection conn = DatabaseConnection.getConnection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("accion"),
                    rs.getString("entidad"),
                    rs.getInt("entidad_id"),
                    rs.getString("detalle"),
                    rs.getTimestamp("fecha").toString().substring(0, 19)
                });
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "❌ Error: " + e.getMessage());
        }
    }
}
