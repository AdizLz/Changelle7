package org.example.service;

import org.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.MessageDigest;
import java.util.Base64;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Hash de contraseña usando SHA-256 + Base64 (sin dependencias externas)
     */
    private String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(plainPassword.getBytes());
            return Base64.getEncoder().encodeToString(encoded);
        } catch (Exception e) {
            logger.error("Error al hashear contraseña", e);
            throw new RuntimeException("Error al hashear contraseña: " + e.getMessage());
        }
    }

    /**
     * Verifica si una contraseña coincide con el hash almacenado
     */
    private boolean verifyPassword(String plainPassword, String storedHash) {
        return hashPassword(plainPassword).equals(storedHash);
    }

    public User register(String id, String name, String email, String plainPassword) {
        if (userService.exists(id)) {
            throw new IllegalArgumentException("User already exists");
        }
        if (userService.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        String hashed = hashPassword(plainPassword);
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        u.setPassword(hashed);
        userService.add(u);
        logger.info("Usuario registrado: {} ({})", name, id);
        return u;
    }

    public User login(String email, String plainPassword) {
        User u = userService.findByEmail(email);
        if (u == null) return null;
        String hashed = u.getPassword();
        if (hashed == null) return null;
        if (verifyPassword(plainPassword, hashed)) {
            return u;
        }
        return null;
    }
}
