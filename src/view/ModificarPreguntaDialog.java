package view;

import controller.PreguntaController;
import dao.AuditoriaDAO;
import dao.PreguntaDAO;
import model.Pregunta;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModificarPreguntaDialog {

    private JDialog dialog;
    private PreguntaController preguntaController;
    private Pregunta pregunta;
    private PreguntaDAO preguntaDAO;
    private int usuarioId;

    public ModificarPreguntaDialog(JFrame parent, PreguntaController preguntaController, Pregunta pregunta, int usuarioId) {
        this.preguntaController = preguntaController;
        this.pregunta = pregunta;
        this.preguntaDAO = new PreguntaDAO();
        this.usuarioId = usuarioId; 

        dialog = new JDialog(parent, "Modificar pregunta", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(8, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelForm.add(new JLabel("Tipo:"));
        panelForm.add(new JLabel(pregunta.getTipo()));

        panelForm.add(new JLabel("Enunciado:"));
        JTextArea enunciadoArea = new JTextArea(pregunta.getEnunciado(), 3, 20);
        panelForm.add(new JScrollPane(enunciadoArea));

        panelForm.add(new JLabel("Curso:"));
        JTextField cursoField = new JTextField(pregunta.getCurso() != null ? pregunta.getCurso() : "");
        panelForm.add(cursoField);

        panelForm.add(new JLabel("Grupo:"));
        JTextField grupoField = new JTextField(pregunta.getGrupo() != null ? pregunta.getGrupo() : "");
        panelForm.add(grupoField);

        panelForm.add(new JLabel("Módulo:"));
        JTextField moduloField = new JTextField(pregunta.getModulo() != null ? pregunta.getModulo() : "");
        panelForm.add(moduloField);

        panelForm.add(new JLabel("RA:"));
        JTextField raField = new JTextField(pregunta.getRa() != null ? pregunta.getRa() : "");
        panelForm.add(raField);

        panelForm.add(new JLabel("Tema:"));
        JTextField temaField = new JTextField(pregunta.getTema() != null ? pregunta.getTema() : "");
        panelForm.add(temaField);

        panelForm.add(new JLabel("Palabras clave:"));
        JTextField palabrasField = new JTextField(pregunta.getPalabrasClave() != null ? pregunta.getPalabrasClave() : "");
        panelForm.add(palabrasField);

        dialog.add(panelForm, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnGuardar = new JButton("💾 Guardar cambios");
        JButton btnEditarRespuestas = new JButton("📝 Editar respuestas");
        
        btnGuardar.addActionListener(e -> {
            preguntaController.actualizarPregunta(
                pregunta.getId(),
                enunciadoArea.getText().trim(),
                cursoField.getText().trim(),
                grupoField.getText().trim(),
                moduloField.getText().trim(),
                raField.getText().trim(),
                temaField.getText().trim(),
                palabrasField.getText().trim()
            );
            dialog.dispose();
        });

        btnEditarRespuestas.addActionListener(e -> {
            if ("TEST".equals(pregunta.getTipo())) {
                editarRespuestasTest();
            } else {
                editarRespuestaDesarrollo();
            }
        });

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditarRespuestas);
        dialog.add(panelBotones, BorderLayout.SOUTH);
    }

    private void editarRespuestasTest() {
        List<String> respuestas = new ArrayList<>();
        int correctaActual = 1;
        
        try {
            respuestas = preguntaDAO.obtenerRespuestasTest(pregunta.getId());
            // Buscar cuál es la correcta
            for (int i = 0; i < respuestas.size(); i++) {
                int indiceEnBD = preguntaDAO.obtenerIndiceCorrecta(pregunta.getId());
                // La correcta es la que coincide con el índice retornado
                if (i + 1 == indiceEnBD) {
                    correctaActual = i + 1;
                }
            }
        } catch (SQLException ex) {
            respuestas = new ArrayList<>();
            respuestas.add(""); respuestas.add(""); respuestas.add(""); respuestas.add("");
        }

        // Asegurar que hay 4 respuestas
        while (respuestas.size() < 4) respuestas.add("");

        JTextField r1 = new JTextField(respuestas.get(0));
        JTextField r2 = new JTextField(respuestas.get(1));
        JTextField r3 = new JTextField(respuestas.get(2));
        JTextField r4 = new JTextField(respuestas.get(3));
        JComboBox<Integer> correctaCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        correctaCombo.setSelectedItem(correctaActual);

        Object[] campos = {
            "Respuesta 1:", r1,
            "Respuesta 2:", r2,
            "Respuesta 3:", r3,
            "Respuesta 4:", r4,
            "Correcta (1-4):", correctaCombo
        };

        int opcion = JOptionPane.showConfirmDialog(dialog, campos, "Editar respuestas", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            List<String> nuevasRespuestas = new ArrayList<>();
            nuevasRespuestas.add(r1.getText());
            nuevasRespuestas.add(r2.getText());
            nuevasRespuestas.add(r3.getText());
            nuevasRespuestas.add(r4.getText());
            
            try {
                preguntaDAO.actualizarRespuestasTest(pregunta.getId(), nuevasRespuestas, (int) correctaCombo.getSelectedItem());
                AuditoriaDAO auditoriaDAO = new AuditoriaDAO();
                auditoriaDAO.registrar(usuarioId, "MODIFICAR", "PREGUNTA", pregunta.getId(), "Respuestas test actualizadas");
                JOptionPane.showMessageDialog(dialog, "✅ Respuestas actualizadas correctamente");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarRespuestaDesarrollo() {
        String textoActual = "";
        
        try {
            String sql = "SELECT texto_modelo FROM respuestas_desarrollo WHERE pregunta_id = ?";
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, pregunta.getId());
            java.sql.ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                textoActual = rs.getString("texto_modelo");
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            textoActual = "";
        }

        JTextArea respuestaArea = new JTextArea(textoActual != null ? textoActual : "", 5, 20);
        JScrollPane scroll = new JScrollPane(respuestaArea);

        int opcion = JOptionPane.showConfirmDialog(dialog, scroll, "Editar respuesta modelo", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                preguntaDAO.actualizarRespuestaDesarrollo(pregunta.getId(), respuestaArea.getText());
                AuditoriaDAO auditoriaDAO = new AuditoriaDAO();
                auditoriaDAO.registrar(usuarioId, "MODIFICAR", "PREGUNTA", pregunta.getId(), "Respuesta desarrollo actualizada");
                JOptionPane.showMessageDialog(dialog, "✅ Respuesta actualizada");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void mostrar() {
        dialog.setVisible(true);
    }
}