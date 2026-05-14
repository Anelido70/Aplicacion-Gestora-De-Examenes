package view;

import dao.AuditoriaDAO;
import dao.PreguntaDAO;
import model.Pregunta;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class VerExamenesDialog {

    private JDialog dialog;
    private int usuarioId;
    private PreguntaDAO preguntaDAO;
    private String rol;

    public VerExamenesDialog(JFrame parent, int usuarioId, String rol) {
        this.preguntaDAO = new PreguntaDAO();
        this.usuarioId = usuarioId;
        this.rol = rol;

        dialog = new JDialog(parent, "Exámenes guardados", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        // Tabla de exámenes
        String[] columnas = {"ID", "Título", "Fecha", "Creado por", "Nº Preguntas"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        dialog.add(scroll, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnVer = new JButton("👁️ Ver examen");
        JButton btnExportar = new JButton("💾 Exportar a TXT");
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        btnEliminar.setVisible("PROFESOR".equals(rol));
        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnCerrar = new JButton("❌ Cerrar");

        panelBotones.add(btnVer);
        panelBotones.add(btnExportar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        // Cargar datos
        cargarExamenes(modeloTabla);

        // Acciones
        btnActualizar.addActionListener(e -> cargarExamenes(modeloTabla));

        btnVer.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(dialog, "Selecciona un examen");
                return;
            }
            int examenId = Integer.parseInt((String) modeloTabla.getValueAt(fila, 0));
            verExamen(examenId);
        });

        btnExportar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(dialog, "Selecciona un examen");
                return;
            }
            int examenId = Integer.parseInt((String) modeloTabla.getValueAt(fila, 0));
            String titulo = (String) modeloTabla.getValueAt(fila, 1);
            exportarExamen(examenId, titulo);
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(dialog, "Selecciona un examen");
                return;
            }
            int examenId = Integer.parseInt((String) modeloTabla.getValueAt(fila, 0));
            int conf = JOptionPane.showConfirmDialog(dialog, "¿Eliminar el examen?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    preguntaDAO.eliminarExamen(examenId);
                    cargarExamenes(modeloTabla);
                    AuditoriaDAO auditoriaDAO = new AuditoriaDAO();
                    auditoriaDAO.registrar(usuarioId, "ELIMINAR", "EXAMEN", examenId, "Examen eliminado");
                    JOptionPane.showMessageDialog(dialog, "✅ Examen eliminado");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "❌ Error: " + ex.getMessage());
                }
            }
        });

        btnCerrar.addActionListener(e -> dialog.dispose());
    }

    private void cargarExamenes(DefaultTableModel modeloTabla) {
        modeloTabla.setRowCount(0);
        try {
            List<String[]> examenes = preguntaDAO.obtenerExamenes();
            for (String[] e : examenes) {
                modeloTabla.addRow(e);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "❌ Error al cargar exámenes: " + ex.getMessage());
        }
    }

    private void verExamen(int examenId) {
        try {
            List<Pregunta> preguntas = preguntaDAO.obtenerPreguntasDeExamen(examenId);
            
            JDialog verDialog = new JDialog(dialog, "Vista previa del examen", true);
            verDialog.setSize(700, 600);
            verDialog.setLocationRelativeTo(dialog);
            verDialog.setLayout(new BorderLayout());

            JTextArea areaExamen = new JTextArea();
            areaExamen.setEditable(false);
            areaExamen.setFont(new Font("Monospaced", Font.PLAIN, 12));

            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════\n");
            sb.append("  IES CLARA DEL REY - EXAMEN\n");
            sb.append("═══════════════════════════════════════════\n\n");
            sb.append("Nombre: __________________________________\n");
            sb.append("Curso: ___________  Grupo: ___________\n");
            sb.append("Fecha: ___________\n\n");
            sb.append("═══════════════════════════════════════════\n\n");

            int num = 1;
            for (Pregunta p : preguntas) {
                sb.append(num).append(". ").append(p.getEnunciado()).append("\n");
                sb.append("   (Tipo: ").append(p.getTipo()).append(")\n");

                if ("TEST".equals(p.getTipo())) {
                    List<String> respuestas = preguntaDAO.getRespuestasTest(p.getId());
                    char letra = 'a';
                    for (String r : respuestas) {
                        sb.append("      ").append(letra).append(") ").append(r).append("\n");
                        letra++;
                    }
                } else {
                    sb.append("      Espacio para responder:\n");
                    sb.append("      _____________________________________________\n");
                    sb.append("      _____________________________________________\n");
                }
                sb.append("\n");
                num++;
            }

            sb.append("═══════════════════════════════════════════\n");

            areaExamen.setText(sb.toString());
            verDialog.add(new JScrollPane(areaExamen), BorderLayout.CENTER);

            JButton btnImprimir = new JButton("🖨️ Imprimir");
            btnImprimir.addActionListener(e -> {
                try { areaExamen.print(); } catch (Exception ex) {}
            });
            JPanel panelBtn = new JPanel();
            panelBtn.add(btnImprimir);
            verDialog.add(panelBtn, BorderLayout.SOUTH);

            verDialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "❌ Error: " + ex.getMessage());
        }
    }

    private void exportarExamen(int examenId, String titulo) {
        try {
            List<Pregunta> preguntas = preguntaDAO.obtenerPreguntasDeExamen(examenId);
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File(titulo.replace(" ", "_") + ".txt"));
            
            if (fileChooser.showSaveDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                String ruta = fileChooser.getSelectedFile().getAbsolutePath();
                if (!ruta.endsWith(".txt")) ruta += ".txt";
                
                PrintWriter writer = new PrintWriter(new FileWriter(ruta));
                
                writer.println("═══════════════════════════════════════════");
                writer.println("  IES CLARA DEL REY - EXAMEN");
                writer.println("═══════════════════════════════════════════");
                writer.println();
                writer.println("Nombre: __________________________________");
                writer.println("Curso: ___________  Grupo: ___________");
                writer.println("Fecha: ___________");
                writer.println();
                writer.println("═══════════════════════════════════════════");
                writer.println();

                int num = 1;
                for (Pregunta p : preguntas) {
                    writer.println(num + ". " + p.getEnunciado());
                    writer.println("   (Tipo: " + p.getTipo() + ")");

                    if ("TEST".equals(p.getTipo())) {
                        List<String> respuestas = preguntaDAO.getRespuestasTest(p.getId());
                        char letra = 'a';
                        for (String r : respuestas) {
                            writer.println("      " + letra + ") " + r);
                            letra++;
                        }
                    } else {
                        writer.println("      Espacio para responder:");
                        writer.println("      _____________________________________________");
                    }
                    writer.println();
                    num++;
                }

                writer.println("═══════════════════════════════════════════");
                writer.close();
                
                JOptionPane.showMessageDialog(dialog, "✅ Examen exportado a:\n" + ruta);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "❌ Error al exportar: " + ex.getMessage());
        }
    }

    public void mostrar() {
        dialog.setVisible(true);
    }
}
