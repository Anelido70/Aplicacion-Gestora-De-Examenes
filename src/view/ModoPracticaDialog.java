package view;

import dao.PreguntaDAO;
import model.Pregunta;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModoPracticaDialog {

    private JDialog dialog;
    private PreguntaDAO preguntaDAO;
    private List<Pregunta> preguntas;
    private List<String> respuestasAlumno;
    private int preguntaActual = 0;
    private JTextArea areaEnunciado;
    private JPanel panelRespuestas;
    private JLabel lblProgreso;
    private ButtonGroup grupoRespuestas;
    private JButton btnSiguiente, btnAnterior, btnFinalizar;

    public ModoPracticaDialog(JFrame parent, List<Pregunta> preguntasSeleccionadas) {
        this.preguntaDAO = new PreguntaDAO();
        this.preguntas = preguntasSeleccionadas;
        this.respuestasAlumno = new ArrayList<>();
        for (int i = 0; i < preguntas.size(); i++) {
            respuestasAlumno.add(null);
        }

        dialog = new JDialog(parent, "Modo Práctica", true);
        dialog.setSize(750, 550);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        // Panel superior con progreso
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSuperior.setBackground(new Color(52, 73, 94));
        lblProgreso = new JLabel();
        lblProgreso.setForeground(Color.WHITE);
        lblProgreso.setFont(new Font("Arial", Font.BOLD, 14));
        panelSuperior.add(lblProgreso);
        dialog.add(panelSuperior, BorderLayout.NORTH);

        // Panel central
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Enunciado
        areaEnunciado = new JTextArea();
        areaEnunciado.setEditable(false);
        areaEnunciado.setFont(new Font("Arial", Font.PLAIN, 14));
        areaEnunciado.setLineWrap(true);
        areaEnunciado.setWrapStyleWord(true);
        JScrollPane scrollEnunciado = new JScrollPane(areaEnunciado);
        scrollEnunciado.setPreferredSize(new Dimension(700, 120));
        panelCentral.add(scrollEnunciado, BorderLayout.NORTH);

        // Panel de respuestas
        panelRespuestas = new JPanel();
        panelRespuestas.setLayout(new BoxLayout(panelRespuestas, BoxLayout.Y_AXIS));
        panelRespuestas.setBorder(BorderFactory.createTitledBorder("Selecciona la respuesta correcta:"));
        JScrollPane scrollRespuestas = new JScrollPane(panelRespuestas);
        panelCentral.add(scrollRespuestas, BorderLayout.CENTER);

        dialog.add(panelCentral, BorderLayout.CENTER);

        // Panel de botones de navegación
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnAnterior = new JButton("⬅ Anterior");
        btnSiguiente = new JButton("Siguiente ➡");
        btnFinalizar = new JButton("✅ Finalizar y ver nota");

        panelBotones.add(btnAnterior);
        panelBotones.add(btnSiguiente);
        panelBotones.add(btnFinalizar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        // Acciones
        btnAnterior.addActionListener(e -> {
            guardarRespuestaActual();
            if (preguntaActual > 0) {
                preguntaActual--;
                mostrarPregunta();
            }
        });

        btnSiguiente.addActionListener(e -> {
            guardarRespuestaActual();
            if (preguntaActual < preguntas.size() - 1) {
                preguntaActual++;
                mostrarPregunta();
            }
        });

        btnFinalizar.addActionListener(e -> {
            guardarRespuestaActual();
            
            // Verificar si hay preguntas sin responder
            int sinResponder = 0;
            for (String r : respuestasAlumno) {
                if (r == null) sinResponder++;
            }
            
            if (sinResponder > 0) {
                int confirm = JOptionPane.showConfirmDialog(dialog, 
                    "Hay " + sinResponder + " preguntas sin responder. ¿Quieres finalizar igualmente?", 
                    "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            }
            
            calcularNota();
        });

        mostrarPregunta();
    }

    private void mostrarPregunta() {
        Pregunta p = preguntas.get(preguntaActual);
        
        // Actualizar progreso
        lblProgreso.setText("Pregunta " + (preguntaActual + 1) + " de " + preguntas.size());
        
        // Mostrar enunciado
        areaEnunciado.setText("" + (preguntaActual + 1) + ". " + p.getEnunciado() + "\n\n" +
                             "Tipo: " + p.getTipo() + " | Módulo: " + p.getModulo());
        
        // Limpiar panel de respuestas
        panelRespuestas.removeAll();
        grupoRespuestas = new ButtonGroup();
        
        if ("TEST".equals(p.getTipo())) {
            try {
                List<String> respuestas = preguntaDAO.obtenerRespuestasTest(p.getId());
                // Mezclar respuestas para que no siempre estén en el mismo orden
                List<Integer> indices = new ArrayList<>();
                for (int i = 0; i < respuestas.size(); i++) indices.add(i);
                Collections.shuffle(indices);
                
                for (int i = 0; i < respuestas.size(); i++) {
                    String texto = respuestas.get(indices.get(i));
                    JRadioButton radio = new JRadioButton(texto);
                    radio.setFont(new Font("Arial", Font.PLAIN, 13));
                    radio.setActionCommand(texto);
                    
                    // Restaurar respuesta anterior si existe
                    if (respuestasAlumno.get(preguntaActual) != null && 
                        respuestasAlumno.get(preguntaActual).equals(String.valueOf(indices.get(i)))) {
                        radio.setSelected(true);
                    }
                    
                    grupoRespuestas.add(radio);
                    panelRespuestas.add(radio);
                    panelRespuestas.add(Box.createVerticalStrut(5));
                }
            } catch (Exception e) {
                panelRespuestas.add(new JLabel("Error al cargar respuestas"));
            }
        } else {
            // Pregunta de desarrollo
            JTextArea areaDesarrollo = new JTextArea(5, 40);
            areaDesarrollo.setFont(new Font("Arial", Font.PLAIN, 13));
            areaDesarrollo.setLineWrap(true);
            areaDesarrollo.setWrapStyleWord(true);
            
            if (respuestasAlumno.get(preguntaActual) != null) {
                areaDesarrollo.setText(respuestasAlumno.get(preguntaActual));
            }
            
            // Guardar respuesta de desarrollo al cambiar
            areaDesarrollo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void changedUpdate(javax.swing.event.DocumentEvent e) { guardar(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { guardar(); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { guardar(); }
                private void guardar() {
                    respuestasAlumno.set(preguntaActual, areaDesarrollo.getText());
                }
            });
            
            panelRespuestas.add(new JScrollPane(areaDesarrollo));
        }
        
        // Actualizar botones
        btnAnterior.setEnabled(preguntaActual > 0);
        btnSiguiente.setEnabled(preguntaActual < preguntas.size() - 1);
        
        panelRespuestas.revalidate();
        panelRespuestas.repaint();
    }

    private void guardarRespuestaActual() {
        Pregunta p = preguntas.get(preguntaActual);
        
        if ("TEST".equals(p.getTipo())) {
            if (grupoRespuestas.getSelection() != null) {
                respuestasAlumno.set(preguntaActual, grupoRespuestas.getSelection().getActionCommand());
            }
        }
        // Para desarrollo ya se guarda automáticamente con el listener
    }

private void calcularNota() {
    int aciertos = 0;
    int totalTest = 0;
    
    for (int i = 0; i < preguntas.size(); i++) {
        Pregunta p = preguntas.get(i);
        if ("TEST".equals(p.getTipo())) {
            totalTest++;
            try {
                // Obtener el TEXTO de la respuesta correcta
                List<String> respuestas = preguntaDAO.obtenerRespuestasTest(p.getId());
                int indiceCorrecta = preguntaDAO.obtenerIndiceCorrecta(p.getId());
                String textoCorrecto = respuestas.get(indiceCorrecta - 1);
                
                // La respuesta del alumno es el texto del radio seleccionado
                String respuestaAlumno = respuestasAlumno.get(i);
                
                // Comparar textos
                if (respuestaAlumno != null && respuestaAlumno.equals(textoCorrecto)) {
                    aciertos++;
                }
            } catch (Exception e) {
                System.out.println("Error al corregir: " + e.getMessage());
            }
        }
    }
    
    double nota = totalTest > 0 ? (aciertos * 10.0) / totalTest : 0;
    nota = Math.round(nota * 100.0) / 100.0;
    
    String mensaje = "📊 RESULTADOS DEL EXAMEN\n\n" +
                     "Preguntas tipo test: " + totalTest + "\n" +
                     "Aciertos: " + aciertos + "\n" +
                     "Fallos: " + (totalTest - aciertos) + "\n" +
                     "Nota: " + nota + " / 10\n\n";
    
    if (nota >= 5) {
        mensaje += "✅ ¡APROBADO! ¡Buen trabajo!";
    } else {
        mensaje += "❌ SUSPENSO. ¡Sigue practicando!";
    }
    
    JOptionPane.showMessageDialog(dialog, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);
    dialog.dispose();
}

    public void mostrar() {
        dialog.setVisible(true);
    }
}
