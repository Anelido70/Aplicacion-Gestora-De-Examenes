package view;

import controller.ExamenController;
import controller.PreguntaController;
import dao.AuditoriaDAO;
import dao.PreguntaDAO;
import model.Pregunta;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GenerarExamenDialog {

    private JDialog dialog;
    private int usuarioId;
    private ExamenController examenController;
    private PreguntaController preguntaController;
    private List<Pregunta> preguntasDisponibles;
    private List<Pregunta> preguntasSeleccionadas;
    private DefaultTableModel modeloTabla;
    private JLabel lblContador;

    public GenerarExamenDialog(JFrame parent, int usuarioId) {
        PreguntaDAO preguntaDAO = new PreguntaDAO();
        this.examenController = new ExamenController(preguntaDAO, usuarioId);
        this.preguntaController = new PreguntaController(preguntaDAO, usuarioId);
        this.preguntasSeleccionadas = new ArrayList<>();
        this.usuarioId = usuarioId;

        dialog = new JDialog(parent, "Generar examen", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        // Panel de filtros
        JPanel panelFiltros = new JPanel(new GridLayout(2, 5, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de búsqueda"));

        JTextField filtroModulo = new JTextField();
        JTextField filtroRA = new JTextField();
        JTextField filtroTema = new JTextField();
        JComboBox<String> filtroTipo = new JComboBox<>(new String[]{"TODOS", "TEST", "DESARROLLO"});

        panelFiltros.add(new JLabel("Módulo:"));
        panelFiltros.add(filtroModulo);
        panelFiltros.add(new JLabel("RA:"));
        panelFiltros.add(filtroRA);
        panelFiltros.add(new JButton("🔍 Buscar") {{
            addActionListener(e -> cargarPreguntas(filtroModulo.getText(), filtroRA.getText(), 
                                                    filtroTema.getText(), (String) filtroTipo.getSelectedItem()));
        }});
        panelFiltros.add(new JLabel("Tema:"));
        panelFiltros.add(filtroTema);
        panelFiltros.add(new JLabel("Tipo:"));
        panelFiltros.add(filtroTipo);
        panelFiltros.add(new JLabel(""));

        dialog.add(panelFiltros, BorderLayout.NORTH);

        // Panel central dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);

        // Tabla de preguntas disponibles
        String[] columnas = {"ID", "Código", "Tipo", "Enunciado", "Curso", "Módulo", "RA", "Tema"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tablaDisponibles = new JTable(modeloTabla);
        JScrollPane scrollDisponibles = new JScrollPane(tablaDisponibles);
        scrollDisponibles.setBorder(BorderFactory.createTitledBorder("Preguntas disponibles"));
        splitPane.setLeftComponent(scrollDisponibles);

        // Panel de seleccionadas
        JPanel panelDerecho = new JPanel(new BorderLayout());
        DefaultTableModel modeloSel = new DefaultTableModel(new String[]{"#", "Enunciado", "Tipo"}, 0);
        JTable tablaSeleccionadas = new JTable(modeloSel);
        JScrollPane scrollSeleccionadas = new JScrollPane(tablaSeleccionadas);
        scrollSeleccionadas.setBorder(BorderFactory.createTitledBorder("Seleccionadas"));

        lblContador = new JLabel("0 preguntas seleccionadas");
        lblContador.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        panelDerecho.add(scrollSeleccionadas, BorderLayout.CENTER);
        panelDerecho.add(lblContador, BorderLayout.SOUTH);
        splitPane.setRightComponent(panelDerecho);

        dialog.add(splitPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAgregar = new JButton("➕ Agregar seleccionada");
        JButton btnAleatorio = new JButton("🎲 Agregar aleatorias");
        JButton btnQuitar = new JButton("➖ Quitar seleccionada");
        JButton btnGenerar = new JButton("📄 Generar examen");
        JButton btnCancelar = new JButton("❌ Cancelar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnAleatorio);
        panelBotones.add(btnQuitar);
        panelBotones.add(btnGenerar);
        panelBotones.add(btnCancelar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        // Acciones
        btnAgregar.addActionListener(e -> {
            int fila = tablaDisponibles.getSelectedRow();
            if (fila != -1) {
                int id = (int) modeloTabla.getValueAt(fila, 0);
                agregarSeleccionada(id, modeloSel);
            }
        });

        btnAleatorio.addActionListener(e -> {
            String cantStr = JOptionPane.showInputDialog(dialog, "¿Cuántas preguntas aleatorias?", "5");
            if (cantStr != null && !cantStr.isEmpty()) {
                try {
                    int cant = Integer.parseInt(cantStr);
                    List<Pregunta> aleatorias = examenController.seleccionarAleatorias(preguntasDisponibles, cant);
                    for (Pregunta p : aleatorias) {
                        if (!existeEnSeleccionadas(p.getId())) {
                            preguntasSeleccionadas.add(p);
                        }
                    }
                    actualizarTablaSeleccionadas(modeloSel);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Número no válido");
                }
            }
        });

        btnQuitar.addActionListener(e -> {
            int fila = tablaSeleccionadas.getSelectedRow();
            if (fila != -1) {
                preguntasSeleccionadas.remove(fila);
                actualizarTablaSeleccionadas(modeloSel);
            }
        });

        btnGenerar.addActionListener(e -> {
            if (preguntasSeleccionadas.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Selecciona al menos una pregunta", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            generarVistaPrevia();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        // Cargar datos iniciales
        cargarPreguntas("", "", "", "TODOS");
    }

    private void cargarPreguntas(String modulo, String ra, String tema, String tipo) {
        preguntasDisponibles = examenController.buscarConFiltros(modulo, ra, tema, tipo);
        modeloTabla.setRowCount(0);
        for (Pregunta p : preguntasDisponibles) {
            modeloTabla.addRow(new Object[]{
                p.getId(), p.getCodigo(), p.getTipo(),
                p.getEnunciado().length() > 40 ? p.getEnunciado().substring(0, 40) + "..." : p.getEnunciado(),
                p.getCurso(), p.getModulo(), p.getRa(), p.getTema()
            });
        }
    }

    private void agregarSeleccionada(int id, DefaultTableModel modeloSel) {
        for (Pregunta p : preguntasDisponibles) {
            if (p.getId() == id && !existeEnSeleccionadas(id)) {
                preguntasSeleccionadas.add(p);
                break;
            }
        }
        actualizarTablaSeleccionadas(modeloSel);
    }

    private boolean existeEnSeleccionadas(int id) {
        return preguntasSeleccionadas.stream().anyMatch(p -> p.getId() == id);
    }

    private void actualizarTablaSeleccionadas(DefaultTableModel modeloSel) {
        modeloSel.setRowCount(0);
        int i = 1;
        for (Pregunta p : preguntasSeleccionadas) {
            modeloSel.addRow(new Object[]{
                i++,
                p.getEnunciado().length() > 50 ? p.getEnunciado().substring(0, 50) + "..." : p.getEnunciado(),
                p.getTipo()
            });
        }
        lblContador.setText(preguntasSeleccionadas.size() + " preguntas seleccionadas");
    }

    private void generarVistaPrevia() {
        JDialog previewDialog = new JDialog(dialog, "Vista previa del examen", true);
        previewDialog.setSize(700, 600);
        previewDialog.setLocationRelativeTo(dialog);
        previewDialog.setLayout(new BorderLayout());

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
        for (Pregunta p : preguntasSeleccionadas) {
            sb.append(num).append(". ").append(p.getEnunciado()).append("\n");
            sb.append("   (Tipo: ").append(p.getTipo()).append(")");
            sb.append(" | Módulo: ").append(p.getModulo() != null ? p.getModulo() : "N/A");
            sb.append(" | RA: ").append(p.getRa() != null ? p.getRa() : "N/A").append("\n");

            if ("TEST".equals(p.getTipo())) {
                List<String> respuestas = examenController.getRespuestasTest(p.getId());
                char letra = 'a';
                for (String r : respuestas) {
                    sb.append("      ").append(letra).append(") ").append(r.replace(" ✓", "")).append("\n");
                    letra++;
                }
            } else {
                sb.append("      Respuesta modelo: ").append(examenController.getRespuestaDesarrollo(p.getId())).append("\n");
            }
            sb.append("\n");
            num++;
        }

        sb.append("═══════════════════════════════════════════\n");
        sb.append("  ¡Buena suerte!\n");
        sb.append("═══════════════════════════════════════════\n");

        areaExamen.setText(sb.toString());
        JScrollPane scrollPreview = new JScrollPane(areaExamen);
        previewDialog.add(scrollPreview, BorderLayout.CENTER);

        JPanel panelPreviewBotones = new JPanel();
        JButton btnImprimir = new JButton("🖨️ Imprimir");
        JButton btnGuardarExamen = new JButton("💾 Guardar examen");
        JButton btnCerrar = new JButton("Cerrar");

        btnImprimir.addActionListener(e -> {
            try {
                areaExamen.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(previewDialog, "Error al imprimir: " + ex.getMessage());
            }
        });

        btnGuardarExamen.addActionListener(e -> {
            String titulo = JOptionPane.showInputDialog(previewDialog, "Título del examen:", "Examen");
            if (titulo != null && !titulo.isEmpty()) {
                String filtros = "Examen generado desde el sistema";
                int examenId = examenController.guardarExamen(titulo, filtros);
                if (examenId != -1) {
                    for (int i = 0; i < preguntasSeleccionadas.size(); i++) {
                        examenController.agregarPreguntaAExamen(examenId, preguntasSeleccionadas.get(i).getId(), i + 1);
                    }
                    AuditoriaDAO auditoriaDAO = new AuditoriaDAO();
                    auditoriaDAO.registrar(usuarioId, "GENERAR_EXAMEN", "EXAMEN", examenId, "Examen: " + titulo);
                    JOptionPane.showMessageDialog(previewDialog, "✅ Examen guardado con ID: " + examenId);
                } else {
                    JOptionPane.showMessageDialog(previewDialog, "❌ Error al guardar el examen");
                }

            }
        });

        btnCerrar.addActionListener(e -> previewDialog.dispose());

        panelPreviewBotones.add(btnImprimir);
        panelPreviewBotones.add(btnGuardarExamen);
        panelPreviewBotones.add(btnCerrar);
        previewDialog.add(panelPreviewBotones, BorderLayout.SOUTH);

        previewDialog.setVisible(true);
    }

    public void mostrar() {
        // Cargar todas las preguntas inicialmente
        dialog.setVisible(true);
    }
}