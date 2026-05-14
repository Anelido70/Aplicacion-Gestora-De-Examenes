import config.InicializadorBD;
import view.LoginUI;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Crear usuario admin si no existe
        InicializadorBD.inicializar();
        
        // Abrir ventana de login
        SwingUtilities.invokeLater(() -> {
            new LoginUI().mostrar();
        });
    }
}