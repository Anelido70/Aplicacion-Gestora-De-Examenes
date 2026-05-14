package view;

import controller.UsuarioController;
import dao.UsuarioDAO;
import model.Usuario;
import service.AuthService;
import javax.swing.*;

public class LoginUI {

    private UsuarioController usuarioController;

    public LoginUI() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        AuthService authService = new AuthService();
        this.usuarioController = new UsuarioController(usuarioDAO, authService);
    }

    public void mostrar() {
        JFrame frame = new JFrame("Login - IES Clara del Rey");
        frame.setSize(350, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setBounds(30, 30, 80, 25);
        JTextField userField = new JTextField();
        userField.setBounds(120, 30, 180, 25);

        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setBounds(30, 70, 80, 25);
        JPasswordField passField = new JPasswordField();
        passField.setBounds(120, 70, 180, 25);

        JButton loginButton = new JButton("Iniciar sesión");
        loginButton.setBounds(30, 120, 130, 30);

        JButton registerButton = new JButton("Registrarse");
        registerButton.setBounds(170, 120, 130, 30);

        JLabel mensajeLabel = new JLabel("");
        mensajeLabel.setBounds(30, 160, 280, 25);

        // Acción LOGIN
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            Usuario usuario = usuarioController.login(username, password);
            
            if (usuario != null) {
                frame.dispose();
                if (usuario.esProfesor()) {
                    new MainFrameProfesor(usuario).mostrar();
                } else {
                    new MainFrameAlumno(usuario).mostrar();
                }
            } else {
                mensajeLabel.setText("❌ Usuario o contraseña incorrectos");
                passField.setText("");
            }
        });

        // Acción REGISTRO
        registerButton.addActionListener(e -> {
            frame.dispose();
            new RegistroUI().mostrar();
        });

        frame.add(userLabel);
        frame.add(userField);
        frame.add(passLabel);
        frame.add(passField);
        frame.add(loginButton);
        frame.add(registerButton);
        frame.add(mensajeLabel);

        frame.setVisible(true);
    }
}