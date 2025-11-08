package org.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserService userService;

    @InjectMocks
    AuthService authService;

    @BeforeEach
    void setUp() {
        // Mock setup
    }

    // ===== PRUEBAS DE REGISTRO (POSITIVAS) =====

    @Test
    void registrar_usuario_valido_exitoso() {
        // Arrange
        when(userService.exists("user1")).thenReturn(false);
        when(userService.findByEmail("juan@example.com")).thenReturn(null);
        doNothing().when(userService).add(any(User.class));

        // Act
        User result = authService.register("user1", "Juan Pérez", "juan@example.com", "password123");

        // Assert
        assertNotNull(result);
        assertEquals("user1", result.getId());
        assertEquals("Juan Pérez", result.getName());
        assertEquals("juan@example.com", result.getEmail());
        assertNotNull(result.getPassword());
        assertNotEquals("password123", result.getPassword()); // Password debe estar hasheado
        verify(userService).add(any(User.class));
    }

    // ===== PRUEBAS DE REGISTRO (NEGATIVAS) =====

    @Test
    void registrar_usuario_id_duplicado_lanza_excepcion() {
        // Arrange
        when(userService.exists("user1")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> authService.register("user1", "Juan", "juan@example.com", "pass"));
        verify(userService, never()).add(any());
    }

    @Test
    void registrar_usuario_email_duplicado_lanza_excepcion() {
        // Arrange
        when(userService.exists("user1")).thenReturn(false);
        User existingUser = new User("other-id", "Other", "juan@example.com");
        when(userService.findByEmail("juan@example.com")).thenReturn(existingUser);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> authService.register("user1", "Juan", "juan@example.com", "pass"));
        verify(userService, never()).add(any());
    }

    // ===== PRUEBAS DE LOGIN (POSITIVAS) =====

    @Test
    void login_credenciales_validas_retorna_usuario() {
        // Arrange
        User storedUser = new User("user1", "Juan", "juan@example.com");
        // Hash de "password123" usando SHA-256 + Base64
        String hashedPassword = hashPassword("password123");
        storedUser.setPassword(hashedPassword);

        when(userService.findByEmail("juan@example.com")).thenReturn(storedUser);

        // Act
        User result = authService.login("juan@example.com", "password123");

        // Assert
        assertNotNull(result);
        assertEquals("juan@example.com", result.getEmail());
        assertEquals("Juan", result.getName());
    }

    @Test
    void login_email_no_existe_retorna_nulo() {
        // Arrange
        when(userService.findByEmail("no@exist.com")).thenReturn(null);

        // Act
        User result = authService.login("no@exist.com", "password");

        // Assert
        assertNull(result);
    }

    @Test
    void login_password_incorrecto_retorna_nulo() {
        // Arrange
        User storedUser = new User("user1", "Juan", "juan@example.com");
        String hashedPassword = hashPassword("correctPassword");
        storedUser.setPassword(hashedPassword);

        when(userService.findByEmail("juan@example.com")).thenReturn(storedUser);

        // Act
        User result = authService.login("juan@example.com", "wrongPassword");

        // Assert
        assertNull(result);
    }

    @Test
    void login_usuario_sin_password_retorna_nulo() {
        // Arrange
        User storedUser = new User("user1", "Juan", "juan@example.com");
        storedUser.setPassword(null); // Sin password

        when(userService.findByEmail("juan@example.com")).thenReturn(storedUser);

        // Act
        User result = authService.login("juan@example.com", "anyPassword");

        // Assert
        assertNull(result);
    }

    // ===== HELPER PARA HASHEAR PASSWORD (SHA-256) =====
    private String hashPassword(String plainPassword) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(plainPassword.getBytes());
            return java.util.Base64.getEncoder().encodeToString(encoded);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

