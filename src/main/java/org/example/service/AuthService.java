package org.example.service;

import org.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Servicio de autenticación y registro de usuarios.
 *
 * Responsabilidades:
 * - Validar credenciales de usuario (email y contraseña)
 * - Registrar nuevos usuarios con validaciones
 * - Hashear y verificar contraseñas usando SHA-256 + Base64
 * - Gestionar sesiones de usuario a través de UserService
 *
 * Nota: Las contraseñas se hashean usando SHA-256 + Base64 sin dependencias externas.
 * En producción, se recomienda usar bcrypt u otro algoritmo más seguro.
 *
 * @see UserService
 * @see User
 */
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;

    /**
     * Constructor del servicio de autenticación.
     *
     * @param userService Servicio de usuarios que se inyecta como dependencia
     */
    public AuthService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Hashea una contraseña en texto plano usando SHA-256 + Base64.
     *
     * Nota: Este método es privado y se usa internamente para hashear contraseñas
     * al registrar y verificar credenciales de login.
     *
     * @param plainPassword Contraseña en texto plano
     * @return Contraseña hasheada en formato Base64
     * @throws RuntimeException Si ocurre un error al calcular el hash SHA-256
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
     * Verifica si una contraseña en texto plano coincide con el hash almacenado.
     *
     * @param plainPassword Contraseña en texto plano a verificar
     * @param storedHash Hash de contraseña almacenado en la base de datos
     * @return true si las contraseñas coinciden, false en caso contrario
     */
    private boolean verifyPassword(String plainPassword, String storedHash) {
        return hashPassword(plainPassword).equals(storedHash);
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * Validaciones:
     * - El ID del usuario no debe existir previamente
     * - El email debe ser único (no registrado anteriormente)
     * - Se hashea la contraseña antes de almacenarla
     *
     * @param id Identificador único del usuario (UUID recomendado)
     * @param name Nombre completo del usuario
     * @param email Correo electrónico del usuario (debe ser único)
     * @param plainPassword Contraseña en texto plano
     * @return Usuario registrado con contraseña hasheada
     * @throws IllegalArgumentException Si el ID ya existe o el email ya está registrado
     */
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

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     *
     * Proceso:
     * 1. Busca el usuario por email
     * 2. Si existe, verifica que la contraseña coincida con el hash almacenado
     * 3. Retorna el usuario si las credenciales son válidas, null en caso contrario
     *
     * @param email Correo electrónico del usuario
     * @param plainPassword Contraseña en texto plano
     * @return Usuario autenticado si las credenciales son válidas, null si no existe o contraseña es incorrecta
     */
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
