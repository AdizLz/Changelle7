package org.example.service;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ItemServiceIntegrationTest {

    private static Connection conn;

    @BeforeAll
    static void setupDb() throws Exception {
        // Crear BD H2 en memoria
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        try (Statement st = conn.createStatement()) {
            // Crear tabla items
            st.execute("""
                CREATE TABLE items (
                    id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(200) NOT NULL,
                    description TEXT,
                    price VARCHAR(50),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
        }
    }

    @AfterAll
    static void teardown() throws Exception {
        if (conn != null) conn.close();
    }

    @Test
    void insertar_item_y_consultar_correctamente() throws Exception {
        // Arrange
        String id = "test-item1";
        String name = "Gorra de prueba";
        String description = "Descripción de prueba";
        String price = "$999.99 USD";

        // Act - Insertar
        try (PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO items (id, name, description, price) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setString(4, price);
            int rows = ps.executeUpdate();
            assertEquals(1, rows, "Debe insertar 1 fila");
        }

        // Assert - Verificar que se insertó
        try (PreparedStatement q = conn.prepareStatement(
            "SELECT id, name, description, price FROM items WHERE id = ?")) {
            q.setString(1, id);
            try (ResultSet rs = q.executeQuery()) {
                assertTrue(rs.next(), "Debe encontrar el item insertado");
                assertEquals(id, rs.getString("id"));
                assertEquals(name, rs.getString("name"));
                assertEquals(description, rs.getString("description"));
                assertEquals(price, rs.getString("price"));
            }
        }
    }

    @Test
    void actualizar_precio_item_correctamente() throws Exception {
        // Arrange
        String id = "test-item2";
        String newPrice = "$1500.00 USD";

        // Insertar item inicial
        try (PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO items (id, name, description, price) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, id);
            ps.setString(2, "Item para update");
            ps.setString(3, "Desc");
            ps.setString(4, "$500.00 USD");
            ps.executeUpdate();
        }

        // Act - Actualizar precio
        try (PreparedStatement ps = conn.prepareStatement(
            "UPDATE items SET price = ? WHERE id = ?")) {
            ps.setString(1, newPrice);
            ps.setString(2, id);
            int rows = ps.executeUpdate();
            assertEquals(1, rows, "Debe actualizar 1 fila");
        }

        // Assert - Verificar que se actualizó
        try (PreparedStatement q = conn.prepareStatement(
            "SELECT price FROM items WHERE id = ?")) {
            q.setString(1, id);
            try (ResultSet rs = q.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(newPrice, rs.getString("price"));
            }
        }
    }

    @Test
    void contar_items_en_tabla() throws Exception {
        // Arrange
        try (PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO items (id, name, description, price) VALUES (?, ?, ?, ?)")) {
            for (int i = 0; i < 3; i++) {
                ps.setString(1, "item-count-" + i);
                ps.setString(2, "Item " + i);
                ps.setString(3, "Desc " + i);
                ps.setString(4, "$" + (100 * (i + 1)) + ".00 USD");
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            assertEquals(3, results.length);
        }

        // Act & Assert - Contar items
        try (PreparedStatement q = conn.prepareStatement(
            "SELECT COUNT(*) as cnt FROM items WHERE id LIKE 'item-count-%'")) {
            try (ResultSet rs = q.executeQuery()) {
                assertTrue(rs.next());
                int count = rs.getInt("cnt");
                assertEquals(3, count);
            }
        }
    }

    @Test
    void eliminar_item_correctamente() throws Exception {
        // Arrange
        String id = "test-item-delete";

        // Insertar item
        try (PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO items (id, name, description, price) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, id);
            ps.setString(2, "Item para eliminar");
            ps.setString(3, "Será eliminado");
            ps.setString(4, "$100.00 USD");
            ps.executeUpdate();
        }

        // Act - Eliminar
        try (PreparedStatement ps = conn.prepareStatement(
            "DELETE FROM items WHERE id = ?")) {
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            assertEquals(1, rows);
        }

        // Assert - Verificar que fue eliminado
        try (PreparedStatement q = conn.prepareStatement(
            "SELECT COUNT(*) as cnt FROM items WHERE id = ?")) {
            q.setString(1, id);
            try (ResultSet rs = q.executeQuery()) {
                rs.next();
                assertEquals(0, rs.getInt("cnt"));
            }
        }
    }
}

