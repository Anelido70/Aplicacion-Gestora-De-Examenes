package controller;

import dao.UsuarioDAO;
import model.Usuario;
import service.AuthService;
import java.sql.SQLException;

public class UsuarioController {

    private UsuarioDAO usuarioDAO;
    private AuthService authService;

    public UsuarioController(UsuarioDAO usuarioDAO, AuthService authService) {
        this.usuarioDAO = usuarioDAO;
        this.authService = authService;
    }

    // LOGIN
    public Usuario login(String username, String password) {
    try {
        Usuario usuario = usuarioDAO.buscarPorUsername(username);
        if (usuario == null) {
            return null;
        }
        if (authService.verificarPassword(password, usuario.getPasswordHash())) {
            return usuario;
        }
    } catch (SQLException e) {
        System.out.println("❌ Error en login: " + e.getMessage());
        }
        return null;
    }

    // REGISTRO de alumno
    public boolean registrarAlumno(String username, String password, String nombreCompleto, String email) {
        try {
            if (usuarioDAO.existeUsername(username)) {
                return false; // Ya existe
            }
            
            String passwordSegura = authService.generarHashConSalt(password);
            
            Usuario nuevo = new Usuario();
            nuevo.setUsername(username);
            nuevo.setPasswordHash(passwordSegura);
            nuevo.setNombreCompleto(nombreCompleto);
            nuevo.setEmail(email);
            nuevo.setRol("ALUMNO");
            
            return usuarioDAO.insertar(nuevo);
        } catch (SQLException e) {
            System.out.println("❌ Error en registro: " + e.getMessage());
            return false;
        }
    }
}