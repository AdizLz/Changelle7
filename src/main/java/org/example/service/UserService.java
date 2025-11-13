package org.example.service;

import org.example.DatabaseManager;
import org.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Servicio de gestión de usuarios.
 *
 * Proporciona operaciones CRUD (Create, Read, Update, Delete) para usuarios
 * en la base de datos PostgreSQL, incluyendo búsquedas y autenticación.
 *
 * Responsabilidades:
 * - Obtener, crear, actualizar y eliminar usuarios
 * - Buscar usuarios por nombre, email o ID
 * - Verificar existencia de usuarios
 * - Gestionar conexiones a base de datos a través de DatabaseManager
 *
 * Nota: Todas las operaciones se registran en logs para auditoría y debugging.
 *
 * @see DatabaseManager
 * @see User
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Obtiene todos los usuarios del sistema.
     *
     * @return Colección de todos los usuarios ordenados por fecha de creación (más recientes primero)
     */
    public Collection<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, created_at FROM users ORDER BY created_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }

            logger.debug("📋 Se obtuvieron {} usuarios", users.size());

        } catch (SQLException e) {
            logger.error("❌ Error al obtener usuarios", e);
        }

        return users;
    }

    /**
     * Obtiene un usuario específico por su ID.
     *
     * @param id Identificador único del usuario
     * @return Usuario encontrado, o null si no existe
     */
    public User get(String id) {
        String sql = "SELECT id, name, email, password FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));

                    logger.debug("✅ Usuario encontrado: {}", id);
                    return user;
                }
            }

        } catch (SQLException e) {
            logger.error("❌ Error al buscar usuario: {}", id, e);
        }

        logger.debug("⚠️ Usuario no encontrado: {}", id);
        return null;
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param user Usuario a registrar (debe tener ID, nombre, email y contraseña hasheada)
     * @throws RuntimeException Si ocurre un error en la base de datos
     */
    public void add(User user) {
        String sql = "INSERT INTO users (id, name, email, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                logger.info("✅ Usuario creado: {} ({})", user.getName(), user.getId());
            }

        } catch (SQLException e) {
            logger.error("❌ Error al crear usuario: {}", user.getId(), e);
            throw new RuntimeException("Error al crear usuario: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de un usuario existente.
     *
     * Nota: Actualmente actualiza nombre y email. Para cambiar contraseña,
     * se recomienda implementar un método específico con validaciones adicionales.
     *
     * @param id Identificador del usuario a actualizar
     * @param user Datos actualizados del usuario
     * @throws RuntimeException Si ocurre un error en la base de datos
     */
    public void update(String id, User user) {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                logger.info("✅ Usuario actualizado: {}", id);
            } else {
                logger.warn("⚠️ No se encontró usuario para actualizar: {}", id);
            }

        } catch (SQLException e) {
            logger.error("❌ Error al actualizar usuario: {}", id, e);
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage());
        }
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param id Identificador del usuario a eliminar
     * @throws RuntimeException Si ocurre un error en la base de datos
     */
    public void delete(String id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                logger.info("✅ Usuario eliminado: {}", id);
            } else {
                logger.warn("⚠️ No se encontró usuario para eliminar: {}", id);
            }

        } catch (SQLException e) {
            logger.error("❌ Error al eliminar usuario: {}", id, e);
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage());
        }
    }

    /**
     * Verifica si un usuario existe por su ID.
     *
     * @param id Identificador del usuario
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            logger.error("❌ Error al verificar existencia de usuario: {}", id, e);
        }

        return false;
    }

    /**
     * Busca usuarios por nombre o email (búsqueda case-insensitive).
     *
     * @param query Término de búsqueda (se busca en nombre y email)
     * @return Colección de usuarios que coinciden con la búsqueda
     */
    public Collection<User> search(String query) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email FROM users WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    users.add(user);
                }
            }

            logger.debug("🔍 Búsqueda '{}' encontró {} usuarios", query, users.size());

        } catch (SQLException e) {
            logger.error("❌ Error al buscar usuarios", e);
        }

        return users;
    }

    /**
     * Busca un usuario por email (utilizado para login).
     *
     * @param email Correo electrónico del usuario
     * @return Usuario encontrado con contraseña hasheada, o null si no existe
     */
    public User findByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM users WHERE email = ? LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    return user;
                }
            }

        } catch (SQLException e) {
            logger.error("❌ Error al buscar usuario por email: {}", email, e);
        }

        return null;
    }
}