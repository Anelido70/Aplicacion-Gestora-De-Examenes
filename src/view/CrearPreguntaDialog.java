package view;

import controller.PreguntaController;
import javax.swing.*;
import java.awt.*;

public class CrearPreguntaDialog {

    private JDialog dialog;
    private PreguntaController preguntaController;
    private JTextArea enunciadoArea;
    private JComboBox<String> tipoCombo;
    private JTextField cursoField, grupoField, moduloField, raField, temaField, palabrasField;

    public CrearPreguntaDialog(JFrame parent, PreguntaController preguntaController) {
        this.preguntaController = preguntaController;
        
        dialog = new JDialog(parent, "Crear nueva pregunta", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        // Panel de formulario
        JPanel panelForm = new JPanel(new GridLayout(9, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelForm.add(new JLabel("Tipo:"));
        tipoCombo = new JComboBox<>(new String[]{"TEST", "DESARROLLO"});
        panelForm.add(tipoCombo);

        panelForm.add(new JLabel("Enunciado:"));
        enunciadoArea = new JTextArea(3, 20);
        JScrollPane enunciadoScroll = new JScrollPane(enunciadoArea);
        panelForm.add(enunciadoScroll);

        panelForm.add(new JLabel("Curso:"));
        cursoField = new JTextField();
        panelForm.add(cursoField);

        panelForm.add(new JLabel("Grupo:"));
        grupoField = new JTextField();
        panelForm.add(grupoField);

        panelForm.add(new JLabel("Módulo:"));
        moduloField = new JTextField();
        panelForm.add(moduloField);

        panelForm.add(new JLabel("RA:"));
        raField = new JTextField();
        panelForm.add(raField);

        panelForm.add(new JLabel("Tema:"));
        temaField = new JTextField();
        panelForm.add(temaField);

        panelForm.add(new JLabel("Palabras clave:"));
        palabrasField = new JTextField();
        panelForm.add(palabrasField);

        dialog.add(panelForm, BorderLayout.CENTER);

        // Botón guardar
        JButton btnGuardar = new JButton("💾 Guardar");
        btnGuardar.addActionListener(e -> guardarPregunta());
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnGuardar);
        dialog.add(panelBoton, BorderLayout.SOUTH);
    }

    private void guardarPregunta() {
        String tipo = (String) tipoCombo.getSelectedItem();
        String enunciado = enunciadoArea.getText().trim();
        String curso = cursoField.getText().trim();
        String grupo = grupoField.getText().trim();
        String modulo = moduloField.getText().trim();
        String ra = raField.getText().trim();
        String tema = temaField.getText().trim();
        String palabrasClave = palabrasField.getText().trim();

        if (enunciado.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "El enunciado no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("TEST".equals(tipo)) {
            mostrarDialogoTest(enunciado, curso, grupo, modulo, ra, tema, palabrasClave);
        } else {
            mostrarDialogoDesarrollo(enunciado, curso, grupo, modulo, ra, tema, palabrasClave);
        }
        dialog.dispose();
    }

    private void mostrarDialogoTest(String enunciado, String curso, String grupo, String modulo, 
                                    String ra, String tema, String palabrasClave) {
        JTextField r1 = new JTextField();
        JTextField r2 = new JTextField();
        JTextField r3 = new JTextField();
        JTextField r4 = new JTextField();
        JComboBox<Integer> correctaCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4});

        Object[] campos = {
            "Respuesta 1:", r1,
            "Respuesta 2:", r2,
            "Respuesta 3:", r3,
            "Respuesta 4:", r4,
            "Correcta (1-4):", correctaCombo
        };

        int opcion = JOptionPane.showConfirmDialog(dialog, campos, "Respuestas tipo test", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            preguntaController.crearPreguntaTest(enunciado, r1.getText(), r2.getText(), r3.getText(), 
                                                r4.getText(), (int) correctaCombo.getSelectedItem(), 
                                                curso, grupo, modulo, ra, tema, palabrasClave);
        }
    }

    private void mostrarDialogoDesarrollo(String enunciado, String curso, String grupo, String modulo, 
                                          String ra, String tema, String palabrasClave) {
        JTextArea respuestaArea = new JTextArea(5, 20);
        JScrollPane scroll = new JScrollPane(respuestaArea);

        int opcion = JOptionPane.showConfirmDialog(dialog, scroll, "Respuesta modelo", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            preguntaController.crearPreguntaDesarrollo(enunciado, respuestaArea.getText(), 
                                                      curso, grupo, modulo, ra, tema, palabrasClave);
        }
    }

    public void mostrar() {
        dialog.setVisible(true);
    }
}