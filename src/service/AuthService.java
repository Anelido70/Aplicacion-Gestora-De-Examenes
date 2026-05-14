package service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthService {

    public String generarSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public String hashPassword(String password, String salt) {
        try {
            String passwordConSalt = password + salt;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(passwordConSalt.getBytes());

            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }

    public String generarHashConSalt(String password) {
        String salt = generarSalt();
        String hash = hashPassword(password, salt);

        return hash + ":" + salt;
    }
    
    public boolean verificarPassword(String passwordIntroducida, String hashGuardado) {
        try {
            String[] partes = hashGuardado.split(":");

            String hashOriginal = partes[0];
            String salt = partes[1];

            String hashIntroducido = hashPassword(passwordIntroducida, salt);

            return hashOriginal.equals(hashIntroducido);

        } catch (Exception e) {
            return false;
        }
    }
}