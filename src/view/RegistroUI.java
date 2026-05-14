package view;

import controller.UsuarioController;
import dao.UsuarioDAO;
import service.AuthService;
import javax.swing.*;

public class RegistroUI {

    private UsuarioController usuarioController;

    public RegistroUI() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        AuthService authService = new AuthService();
        this.usuarioController = new UsuarioController(usuarioDAO, authService);
    }

    public void mostrar() {
        JFrame frame = new JFrame("Registro de Alumno - IES Clara del Rey");
        frame.setSize(380, 320);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("📝 Registro de nuevo alumno");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        titleLabel.setBounds(40, 15, 300, 25);
        frame.add(titleLabel);

        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setBounds(30, 55, 100, 25);
        JTextField userField = new JTextField();
        userField.setBounds(140, 55, 190, 25);
        frame.add(userLabel);
        frame.add(userField);

        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setBounds(30, 90, 100, 25);
        JPasswordField passField = new JPasswordField();
        passField.setBounds(140, 90, 190, 25);
        frame.add(passLabel);
        frame.add(passField);

        JLabel nombreLabel = new JLabel("Nombre completo:");
        nombreLabel.setBounds(30, 125, 100, 25);
        JTextField nombreField = new JTextField();
        nombreField.setBounds(140, 125, 190, 25);
        frame.add(nombreLabel);
        frame.add(nombreField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(30, 160, 100, 25);
        JTextField emailField = new JTextField();
        emailField.setBounds(140, 160, 190, 25);
        frame.add(emailLabel);
        frame.add(emailField);

        JButton registerButton = new JButton("✅ Registrarse");
        registerButton.setBounds(30, 210, 140, 30);
        frame.add(registerButton);

        JButton backButton = new JButton("🔙 Volver al login");
        backButton.setBounds(190, 210, 140, 30);
        frame.add(backButton);

        JLabel mensajeLabel = new JLabel("");
        mensajeLabel.setBounds(30, 250, 300, 25);
        frame.add(mensajeLabel);

        // Acción REGISTRO
        registerButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            String nombre = nombreField.getText().trim();
            String email = emailField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
                mensajeLabel.setText("❌ Rellena todos los campos obligatorios");
                return;
            }

            if (password.length() < 4) {
                mensajeLabel.setText("❌ La contraseña debe tener al menos 4 caracteres");
                return;
            }

            boolean registrado = usuarioController.registrarAlumno(username, password, nombre, email);

            if (registrado) {
                JOptionPane.showMessageDialog(frame, "✅ Registro exitoso. Ya puedes iniciar sesión.");
                frame.dispose();
                new LoginUI().mostrar();
            } else {
                mensajeLabel.setText("❌ El usuario ya existe");
            }
        });

        // Volver al login
        backButton.addActionListener(e -> {
            frame.dispose();
            new LoginUI().mostrar();
        });

        frame.setVisible(true);
    }
}
