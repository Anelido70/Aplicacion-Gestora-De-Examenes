package view;

import dao.PreguntaDAO;
import dao.UsuarioDAO;
import model.Pregunta;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BusquedaAvanzadaDialog {

    private JDialog dialog;
    private PreguntaDAO preguntaDAO;
    private DefaultTableModel modeloTabla;
    private JTextField filtroModulo, filtroRA, filtroTema, filtroAutor, filtroFecha;

    public BusquedaAvanzadaDialog(JFrame parent, DefaultTableModel modeloTabla) {
        this.preguntaDAO = new PreguntaDAO();
        this.modeloTabla = modeloTabla;

        dialog = new JDialog(parent, "Búsqueda avanzada", true);
        dialog.setSize(450, 320);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel panelFiltros = new JPanel(new GridLayout(6, 2, 10, 10));
        panelFiltros.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panelFiltros.add(new JLabel("Módulo:"));
        filtroModulo = new JTextField();
        panelFiltros.add(filtroModulo);

        panelFiltros.add(new JLabel("RA:"));
        filtroRA = new JTextField();
        panelFiltros.add(filtroRA);

        panelFiltros.add(new JLabel("Tema:"));
        filtroTema = new JTextField();
        panelFiltros.add(filtroTema);

        panelFiltros.add(new JLabel("Autor (username):"));
        filtroAutor = new JTextField();
        panelFiltros.add(filtroAutor);

        panelFiltros.add(new JLabel("Fecha (YYYY-MM-DD):"));
        filtroFecha = new JTextField();
        panelFiltros.add(filtroFecha);

        dialog.add(panelFiltros, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnBuscar = new JButton("🔍 Buscar");
        JButton btnLimpiar = new JButton("🔄 Limpiar filtros");
        JButton btnCerrar = new JButton("❌ Cerrar");

        panelBotones.add(btnBuscar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCerrar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> realizarBusqueda());
        btnLimpiar.addActionListener(e -> {
            filtroModulo.setText("");
            filtroRA.setText("");
            filtroTema.setText("");
            filtroAutor.setText("");
            filtroFecha.setText("");
        });
        btnCerrar.addActionListener(e -> dialog.dispose());
    }

    private void realizarBusqueda() {
        try {
            String sql = "SELECT * FROM preguntas WHERE 1=1";
            
            if (!filtroModulo.getText().trim().isEmpty())
                sql += " AND modulo LIKE '%" + filtroModulo.getText().trim() + "%'";
            if (!filtroRA.getText().trim().isEmpty())
                sql += " AND ra LIKE '%" + filtroRA.getText().trim() + "%'";
            if (!filtroTema.getText().trim().isEmpty())
                sql += " AND tema LIKE '%" + filtroTema.getText().trim() + "%'";
            if (!filtroFecha.getText().trim().isEmpty())
                sql += " AND DATE(fecha_creacion) = '" + filtroFecha.getText().trim() + "'";
            if (!filtroAutor.getText().trim().isEmpty()) {
                // Buscar ID del autor por username
                String autor = filtroAutor.getText().trim();
                sql += " AND autor_id IN (SELECT id FROM usuarios WHERE username LIKE '%" + autor + "%')";
            }
            
            sql += " ORDER BY fecha_creacion DESC";
            
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);
            
            modeloTabla.setRowCount(0);
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("codigo"),
                    rs.getString("tipo"),
                    rs.getString("enunciado").length() > 50 ? 
                        rs.getString("enunciado").substring(0, 50) + "..." : rs.getString("enunciado"),
                    rs.getString("curso"),
                    rs.getString("modulo"),
                    rs.getString("ra"),
                    rs.getString("tema"),
                    obtenerUsername(rs.getInt("autor_id")),
                    rs.getTimestamp("fecha_creacion") != null ? 
                        rs.getTimestamp("fecha_creacion").toString().substring(0, 10) : "N/A"
                });
            }
            rs.close();
            stmt.close();
            conn.close();
            
            dialog.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "❌ Error: " + ex.getMessage());
        }
    }

    private String obtenerUsername(int id) {
    try {
             UsuarioDAO dao = new UsuarioDAO();
             return dao.buscarPorId(id);
         } catch (Exception e) {
            return "N/A";
         }
    }

    public void mostrar() {
        dialog.setVisible(true);
    }
}
