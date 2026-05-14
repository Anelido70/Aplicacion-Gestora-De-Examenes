package view;

import dao.PreguntaDAO;
import model.Pregunta;
import model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainFrameAlumno {

    private Usuario usuarioActual;
    private PreguntaDAO preguntaDAO;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;

    public MainFrameAlumno(Usuario usuario) {
        this.usuarioActual = usuario;
        this.preguntaDAO = new PreguntaDAO();
    }

    public void mostrar() {
        JFrame frame = new JFrame("Gestión de Exámenes - IES Clara del Rey [ALUMNO]");
        frame.setSize(900, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(39, 174, 96));
        JLabel bienvenidaLabel = new JLabel("  🎓 Alumno: " + usuarioActual.getNombreCompleto());
        bienvenidaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bienvenidaLabel.setForeground(Color.WHITE);
        panelSuperior.add(bienvenidaLabel, BorderLayout.WEST);

        // Barra de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBusqueda.setBackground(new Color(39, 174, 96));
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
        String[] columnas = {"ID", "Código", "Tipo", "Enunciado", "Módulo", "RA", "Tema"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tabla);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(new Color(236, 240, 241));

        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnVerExamenes = new JButton("📋 Ver exámenes");
        JButton btnPracticar = new JButton("📝 Modo práctica");
        JButton btnSalir = new JButton("🚪 Salir");

        panelBotones.add(btnActualizar);
        panelBotones.add(btnVerExamenes);
        panelBotones.add(btnPracticar);
        panelBotones.add(btnSalir);
        frame.add(panelBotones, BorderLayout.SOUTH);

        // Acciones
        btnActualizar.addActionListener(e -> cargarPreguntas());

        btnBuscar.addActionListener(e -> buscarPreguntas());
        btnLimpiar.addActionListener(e -> {
            campoBusqueda.setText("");
            cargarPreguntas();
        });

        btnVerExamenes.addActionListener(e -> {
           new VerExamenesDialog(frame, usuarioActual.getId(), "ALUMNO").mostrar();
        });

        btnPracticar.addActionListener(e -> {
        try {
            List<Pregunta> todas = preguntaDAO.obtenerTodas();
            List<Pregunta> test = new ArrayList<>();
              for (Pregunta p : todas) {
                if ("TEST".equals(p.getTipo())) {
                test.add(p);
            }
        }
        if (test.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No hay preguntas tipo test disponibles", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Obtener lista de módulos únicos
        List<String> modulos = new ArrayList<>();
        for (Pregunta p : test) {
            String mod = p.getModulo() != null && !p.getModulo().isEmpty() ? p.getModulo() : "Sin módulo";
            if (!modulos.contains(mod)) {
                modulos.add(mod);
            }
        }
        
        // Añadir opción "Todos"
        modulos.add(0, "TODOS");
        
        // Mostrar selector
        String moduloSeleccionado = (String) JOptionPane.showInputDialog(
            frame,
            "Selecciona el módulo para practicar:",
            "Elegir módulo",
            JOptionPane.QUESTION_MESSAGE,
            null,
            modulos.toArray(),
            "TODOS"
        );
        
        if (moduloSeleccionado == null) return; // Canceló
        
        // Filtrar por módulo
        List<Pregunta> filtradas = new ArrayList<>();
        if ("TODOS".equals(moduloSeleccionado)) {
            filtradas.addAll(test);
        } else {
            for (Pregunta p : test) {
                String mod = p.getModulo() != null ? p.getModulo() : "Sin módulo";
                if (mod.equals(moduloSeleccionado)) {
                    filtradas.add(p);
                }
            }
        }
        
        // Preguntar cuántas preguntas
        String cantStr = JOptionPane.showInputDialog(frame, 
            "¿Cuántas preguntas quieres? (máx " + filtradas.size() + ")", 
            String.valueOf(Math.min(5, filtradas.size())));
        
        if (cantStr == null || cantStr.isEmpty()) return;
        
        int cantidad = Integer.parseInt(cantStr);
        if (cantidad > filtradas.size()) cantidad = filtradas.size();
        if (cantidad < 1) cantidad = 1;
        
        // Seleccionar aleatorias
        Collections.shuffle(filtradas);
        List<Pregunta> seleccionadas = filtradas.subList(0, cantidad);
        
        new ModoPracticaDialog(frame, seleccionadas).mostrar();
        
        } catch (Exception ex) {
        JOptionPane.showMessageDialog(frame, "❌ Error: " + ex.getMessage());
        }
        });

        btnSalir.addActionListener(e -> {
            frame.dispose();
            new LoginUI().mostrar();
        });

        cargarPreguntas();
        frame.setVisible(true);
    }

    private void buscarPreguntas() {
        String palabra = campoBusqueda.getText().trim();
        if (palabra.isEmpty()) {
            cargarPreguntas();
            return;
        }
        try {
            List<Pregunta> preguntas = preguntaDAO.buscarPorPalabraClave(palabra);
            modeloTabla.setRowCount(0);
            for (Pregunta p : preguntas) {
                modeloTabla.addRow(new Object[]{
                    p.getId(), p.getCodigo(), p.getTipo(),
                    p.getEnunciado().length() > 60 ? p.getEnunciado().substring(0, 60) + "..." : p.getEnunciado(),
                    p.getModulo(), p.getRa(), p.getTema()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "❌ Error al buscar: " + ex.getMessage());
        }
    }

    private void cargarPreguntas() {
        modeloTabla.setRowCount(0);
        try {
            List<Pregunta> preguntas = preguntaDAO.obtenerTodas();
            for (Pregunta p : preguntas) {
                modeloTabla.addRow(new Object[]{
                    p.getId(), p.getCodigo(), p.getTipo(),
                    p.getEnunciado().length() > 60 ? p.getEnunciado().substring(0, 60) + "..." : p.getEnunciado(),
                    p.getModulo(), p.getRa(), p.getTema()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "❌ Error al cargar preguntas: " + ex.getMessage());
        }
    }
}
