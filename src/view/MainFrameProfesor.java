package view;

import controller.PreguntaController;
import dao.PreguntaDAO;
import dao.UsuarioDAO;
import model.Pregunta;
import model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrameProfesor {

    private Usuario usuarioActual;
    private PreguntaController preguntaController;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;

    public MainFrameProfesor(Usuario usuario) {
        this.usuarioActual = usuario;
        PreguntaDAO preguntaDAO = new PreguntaDAO();
        this.preguntaController = new PreguntaController(preguntaDAO, usuario.getId());
    }

    public void mostrar() {
        JFrame frame = new JFrame("Gestión de Exámenes - IES Clara del Rey [PROFESOR]");
        frame.setSize(900, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(41, 128, 185));
        JLabel bienvenidaLabel = new JLabel("  👨‍🏫 Profesor: " + usuarioActual.getNombreCompleto());
        bienvenidaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bienvenidaLabel.setForeground(Color.WHITE);
        panelSuperior.add(bienvenidaLabel, BorderLayout.WEST);

        // Barra de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBusqueda.setBackground(new Color(41, 128, 185));
        campoBusqueda = new JTextField(15);
        JButton btnBuscar = new JButton("🔍 Buscar");
        JButton btnLimpiar = new JButton("🔄 Limpiar");
        panelBusqueda.add(new JLabel("Palabra clave:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiar);
        panelSuperior.add(panelBusqueda, BorderLayout.EAST);

        frame.add(panelSuperior, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Código", "Tipo", "Enunciado", "Curso", "Módulo", "RA", "Tema", "Autor", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tabla);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(new Color(236, 240, 241));

        JButton btnCrear = new JButton("➕ Crear");
        JButton btnModificar = new JButton("✏️ Modificar");
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnGenerar = new JButton("📄 Generar examen");
        JButton btnVerExamenes = new JButton("📋 Ver exámenes");
        JButton btnSalir = new JButton("🚪 Salir");
        JButton btnBusquedaAvanzada = new JButton("🔎 Búsqueda avanzada");
        JButton btnAuditoria = new JButton("📜 Auditoría");

        panelBotones.add(btnAuditoria);
        panelBotones.add(btnBusquedaAvanzada);
        panelBotones.add(btnCrear);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnGenerar);
        panelBotones.add(btnVerExamenes);
        panelBotones.add(btnSalir);
        frame.add(panelBotones, BorderLayout.SOUTH);

        // Acciones
        btnCrear.addActionListener(e -> {
            new CrearPreguntaDialog(frame, preguntaController).mostrar();
            cargarPreguntas();
        });

        btnAuditoria.addActionListener(e -> {
            new VisorAuditoriaDialog(frame);
        });

        btnModificar.addActionListener(e -> modificarPregunta(frame, tabla));
        btnEliminar.addActionListener(e -> eliminarPregunta(tabla));
        btnActualizar.addActionListener(e -> cargarPreguntas());

        btnBuscar.addActionListener(e -> buscarPreguntas());
        btnLimpiar.addActionListener(e -> {
            campoBusqueda.setText("");
            cargarPreguntas();
        });

        btnBusquedaAvanzada.addActionListener(e -> {
            new BusquedaAvanzadaDialog(frame, modeloTabla).mostrar();
        });

        btnGenerar.addActionListener(e -> {
            new GenerarExamenDialog(frame, usuarioActual.getId()).mostrar();
        });

        btnVerExamenes.addActionListener(e -> {
            new VerExamenesDialog(frame, usuarioActual.getId(), "PROFESOR").mostrar();
        });

        btnSalir.addActionListener(e -> {
            frame.dispose();
            new LoginUI().mostrar();
        });

        cargarPreguntas();
        frame.setVisible(true);
    }

    private void modificarPregunta(JFrame frame, JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(frame, "Selecciona una pregunta para modificar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Pregunta pregunta = preguntaController.obtenerPorId(id);
        if (pregunta != null) {
            new ModificarPreguntaDialog(frame, preguntaController, pregunta, usuarioActual.getId()).mostrar();
            cargarPreguntas();
        }
    }

    private void eliminarPregunta(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona una pregunta", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        int conf = JOptionPane.showConfirmDialog(null, "¿Eliminar pregunta ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            preguntaController.eliminarPregunta(id);
            cargarPreguntas();
        }
    }

    private void buscarPreguntas() {
        String palabra = campoBusqueda.getText().trim();
        if (palabra.isEmpty()) {
            cargarPreguntas();
            return;
        }
        List<Pregunta> preguntas = preguntaController.buscarPorPalabraClave(palabra);
        modeloTabla.setRowCount(0);
        for (Pregunta p : preguntas) {
               modeloTabla.addRow(new Object[]{
                  p.getId(), p.getCodigo(), p.getTipo(),
                  p.getEnunciado().length() > 50 ? p.getEnunciado().substring(0, 50) + "..." : p.getEnunciado(),
                  p.getCurso(), p.getModulo(), p.getRa(), p.getTema(),
                  obtenerNombreAutor(p.getAutorId()),
                  p.getFechaCreacion() != null ? p.getFechaCreacion().toString().substring(0, 10) : "N/A"
            });
        }
    }

    private void cargarPreguntas() {
        modeloTabla.setRowCount(0);
        List<Pregunta> preguntas = preguntaController.obtenerTodas();
        for (Pregunta p : preguntas) {
               modeloTabla.addRow(new Object[]{
                  p.getId(), p.getCodigo(), p.getTipo(),
                  p.getEnunciado().length() > 50 ? p.getEnunciado().substring(0, 50) + "..." : p.getEnunciado(),
                  p.getCurso(), p.getModulo(), p.getRa(), p.getTema(),
                  obtenerNombreAutor(p.getAutorId()),
                  p.getFechaCreacion() != null ? p.getFechaCreacion().toString().substring(0, 10) : "N/A"
           });
        }
    }
    private String obtenerNombreAutor(int autorId) {
    try {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        // Como no tenemos buscarPorId, usamos una consulta directa
        String sql = "SELECT username FROM usuarios WHERE id = ?";
        java.sql.Connection conn = database.DatabaseConnection.getConnection();
        java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, autorId);
        java.sql.ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("username");
        }
        rs.close();
        stmt.close();
        conn.close();
    } catch (Exception e) {}
    return "N/A";
}
}