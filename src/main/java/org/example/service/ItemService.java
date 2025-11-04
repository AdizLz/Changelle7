package org.example.service;

import org.example.DatabaseManager;
import org.example.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private static final Gson gson = new Gson();
    private static volatile List<Item> resourceItemsCache = null;

    /** Obtiene todos los items */
    public Collection<Item> getAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT id, name, description, price FROM items ORDER BY created_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getString("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setPrice(rs.getString("price"));
                items.add(item);
            }
            logger.debug("📋 Se obtuvieron {} items desde DB", items.size());

        } catch (SQLException e) {
            logger.error("❌ Error al obtener items", e);
        }

        // Merge con JSON
        try {
            List<Item> resourceItems = loadItemsFromResource();
            if (resourceItems != null) {
                Set<String> ids = new HashSet<>();
                for (Item it : items)
                    if (it != null && it.getId() != null) ids.add(it.getId());
                for (Item rit : resourceItems) {
                    if (rit != null && rit.getId() != null && !ids.contains(rit.getId())) {
                        items.add(rit);
                    }
                }
                logger.debug("📋 Tras merge con recursos, total {} items", items.size());
            }
        } catch (Exception e) {
            logger.warn("No se pudo mergear items de recurso: {}", e.getMessage());
        }

        return items;
    }

    /** Obtiene un item por ID */
    public Item get(String id) {
        String sql = "SELECT id, name, description, price FROM items WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getString("id"));
                    item.setName(rs.getString("name"));
                    item.setDescription(rs.getString("description"));
                    item.setPrice(rs.getString("price"));
                    return item;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar item: {}", id, e);
        }

        // fallback JSON
        try {
            List<Item> resourceItems = loadItemsFromResource();
            if (resourceItems != null) {
                for (Item it : resourceItems) {
                    if (it != null && id.equals(it.getId())) {
                        return it;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Fallback JSON failed for item {}: {}", id, e.getMessage());
        }
        return null;
    }

    /** Carga items desde el recurso /items.json (cached) */
    private List<Item> loadItemsFromResource() {
        if (resourceItemsCache != null) return resourceItemsCache;
        synchronized (ItemService.class) {
            if (resourceItemsCache != null) return resourceItemsCache;
            try (InputStream is = ItemService.class.getResourceAsStream("/items.json")) {
                if (is == null) {
                    logger.warn("Resource items.json not found on classpath");
                    return null;
                }
                InputStreamReader reader = new InputStreamReader(is);
                Type listType = new TypeToken<List<Item>>() {}.getType();
                List<Item> items = gson.fromJson(reader, listType);
                resourceItemsCache = items;
                logger.debug("Loaded {} items from resource items.json",
                        items != null ? items.size() : 0);
                return items;
            } catch (Exception e) {
                logger.error("Error loading items from resource", e);
                return null;
            }
        }
    }

    /** Verifica si un item existe */
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM items WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error al verificar existencia de item: {}", id, e);
        }

        try {
            List<Item> resourceItems = loadItemsFromResource();
            if (resourceItems != null)
                for (Item it : resourceItems)
                    if (it != null && id.equals(it.getId())) return true;
        } catch (Exception e) { /* ignore */ }

        return false;
    }

    /** Agrega un nuevo item */
    public void add(Item item) {
        String sql = "INSERT INTO items (id, name, description, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getName());
            pstmt.setString(3, item.getDescription());
            pstmt.setString(4, item.getPrice());
            pstmt.executeUpdate();
            logger.info("Item creado: {} ({})", item.getName(), item.getId());
        } catch (SQLException e) {
            logger.error("Error al crear item: {}", item.getId(), e);
            throw new RuntimeException("Error al crear item: " + e.getMessage());
        }
    }

    /** Actualiza un item existente */
    public void update(String id, Item item) {
        String sql = "UPDATE items SET name = ?, description = ?, price = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setString(3, item.getPrice());
            pstmt.setString(4, id);
            pstmt.executeUpdate();
            logger.info("Item actualizado: {}", id);
        } catch (SQLException e) {
            logger.error("Error al actualizar item: {}", id, e);
            throw new RuntimeException("Error al actualizar item: " + e.getMessage());
        }
    }

    /** Elimina un item */
    public void delete(String id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            logger.info("Item eliminado: {}", id);
        } catch (SQLException e) {
            logger.error("Error al eliminar item: {}", id, e);
            throw new RuntimeException("Error al eliminar item: " + e.getMessage());
        }
    }

    /** Busca items por nombre */
    public Collection<Item> searchByName(String query) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT id, name, description, price FROM items WHERE LOWER(name) LIKE ? ORDER BY name";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getString("id"));
                    item.setName(rs.getString("name"));
                    item.setDescription(rs.getString("description"));
                    item.setPrice(rs.getString("price"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            logger.error("❌ Error al buscar items", e);
        }
        return items;
    }

    /** Filtro principal combinado (nombre + rango de precio) */
    public Collection<Item> getFiltered(String q, Double minPrice, Double maxPrice) {
        Collection<Item> all = getAll();
        List<Item> out = new ArrayList<>();
        String query = q != null ? q.trim().toLowerCase() : null;

        for (Item it : all) {
            if (it == null) continue;
            boolean match = true;

            // Filtro por nombre / descripción
            if (query != null && !query.isEmpty()) {
                String name = it.getName() != null ? it.getName().toLowerCase() : "";
                String desc = it.getDescription() != null ? it.getDescription().toLowerCase() : "";
                if (!name.contains(query) && !desc.contains(query)) {
                    match = false;
                }
            }

            // Filtro por precio (solo si el nombre coincide)
            if (match && (minPrice != null || maxPrice != null)) {
                Double priceVal = parsePriceToDouble(it.getPrice());
                if (priceVal == null) {
                    match = false;
                } else {
                    if (minPrice != null && priceVal < minPrice) {
                        match = false;
                    }
                    if (maxPrice != null && priceVal > maxPrice) {
                        match = false;
                    }
                }
            }

            if (match) {
                out.add(it);
            }
        }
        return out;
    }

    /** Convierte "$621.34 USD" o similares a 621.34 */
    private Double parsePriceToDouble(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) return null;
        try {
            String cleaned = priceStr.replace(",", "")
                    .replaceAll("[^0-9.]", "")
                    .trim();
            if (cleaned.isEmpty()) return null;
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            logger.error("Error parseando precio '{}': {}", priceStr, e.getMessage());
            return null;
        }
    }

    /** Actualiza solo el precio del item. Si no existe en DB pero existe en recursos, lo inserta. */
    public void updatePrice(String id, String newPrice) {
        String updateSql = "UPDATE items SET price = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, newPrice);
            ps.setString(2, id);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                // No estaba en DB. Intentar insertarlo desde recurso si existe.
                Item it = get(id); // get() hace fallback a recursos
                if (it != null) {
                    it.setPrice(newPrice);
                    String insertSql = "INSERT INTO items (id, name, description, price) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                        ins.setString(1, it.getId());
                        ins.setString(2, it.getName());
                        ins.setString(3, it.getDescription());
                        ins.setString(4, it.getPrice());
                        ins.executeUpdate();
                        logger.info("Precio insertado para item ausente en DB: {} -> {}", id, newPrice);
                    }
                } else {
                    logger.warn("updatePrice: item {} no existe (ni en DB ni en recursos)", id);
                }
            } else {
                logger.info("Precio actualizado en DB: {} -> {}", id, newPrice);
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar precio del item {}", id, e);
            throw new RuntimeException("Error al actualizar precio: " + e.getMessage());
        }

        // Sincronizar cache en memoria si fue cargado desde recursos
        try {
            if (resourceItemsCache != null) {
                for (Item it : resourceItemsCache) {
                    if (it != null && id.equals(it.getId())) {
                        it.setPrice(newPrice);
                        break;
                    }
                }
            }
        } catch (Exception ignore) {}
    }

    /** Test local */
    public static void main(String[] args) {
        ItemService s = new ItemService();
        Collection<Item> filtered = s.getFiltered(null, 500.0, 700.0);
        System.out.println("---- RESULTADO ----");
        for (Item it : filtered) {
            System.out.println(it.getName() + " - " + it.getPrice());
        }
    }
}
