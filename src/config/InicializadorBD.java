package config;

import dao.UsuarioDAO;
import model.Usuario;
import service.AuthService;
import java.sql.SQLException;

public class InicializadorBD {

    public static void inicializar() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        AuthService authService = new AuthService();

        try {
            if (usuarioDAO.buscarPorUsername("admin") == null) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPasswordHash(authService.generarHashConSalt("admin123"));
                admin.setNombreCompleto("Administrador del Sistema");
                admin.setEmail("admin@iesclaradelrey.es");
                admin.setRol("PROFESOR");
                
                usuarioDAO.insertar(admin);
                System.out.println("✅ Usuario admin creado (admin / admin123)");
            } else {
                System.out.println("✅ Usuario admin ya existe");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al inicializar BD: " + e.getMessage());
        }
    }
}