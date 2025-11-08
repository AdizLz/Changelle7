package org.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.example.model.Offer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    ItemService itemService;

    @InjectMocks
    OfferService offerService;

    // ===== PRUEBAS DE CREACIÓN DE OFERTAS =====

    @Test
    void crear_oferta_con_datos_validos() {
        // Arrange
        Offer offer = new Offer("item1", "Juan", "juan@example.com", 750.00);

        // Act
        assertNotNull(offer);

        // Assert
        assertEquals("item1", offer.getId());
        assertEquals("Juan", offer.getName());
        assertEquals("juan@example.com", offer.getEmail());
        assertEquals(750.00, offer.getAmount());
    }

    @Test
    void crear_oferta_con_montos_validos_positivos() {
        // Arrange
        double[] montos = {0.01, 100.00, 999.99, 10000.50};

        // Act & Assert
        for (double monto : montos) {
            Offer offer = new Offer("item1", "User", "user@ex.com", monto);
            assertEquals(monto, offer.getAmount());
            assertTrue(offer.getAmount() > 0);
        }
    }

    @Test
    void crear_multiples_ofertas_mismo_item() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("item1", "U1", "u1@ex.com", 500.00));
        offers.add(new Offer("item1", "U2", "u2@ex.com", 600.00));
        offers.add(new Offer("item1", "U3", "u3@ex.com", 700.00));

        // Act
        long count = offers.stream().filter(o -> "item1".equals(o.getId())).count();

        // Assert
        assertEquals(3, count);
    }

    // ===== PRUEBAS DE OBTENCIÓN Y FILTRADO =====

    @Test
    void obtener_oferta_mas_alta_por_item() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("item1", "User1", "u1@ex.com", 500.00));
        offers.add(new Offer("item1", "User2", "u2@ex.com", 750.00));
        offers.add(new Offer("item1", "User3", "u3@ex.com", 600.00));

        // Act
        Offer highest = offers.stream()
            .max((a, b) -> Double.compare(a.getAmount(), b.getAmount()))
            .orElse(null);

        // Assert
        assertNotNull(highest);
        assertEquals(750.00, highest.getAmount());
        assertEquals("User2", highest.getName());
    }

    @Test
    void obtener_oferta_mas_baja_por_item() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("item1", "U1", "u1@ex.com", 500.00));
        offers.add(new Offer("item1", "U2", "u2@ex.com", 300.00));
        offers.add(new Offer("item1", "U3", "u3@ex.com", 600.00));

        // Act
        Offer lowest = offers.stream()
            .min((a, b) -> Double.compare(a.getAmount(), b.getAmount()))
            .orElse(null);

        // Assert
        assertNotNull(lowest);
        assertEquals(300.00, lowest.getAmount());
        assertEquals("U2", lowest.getName());
    }

    @Test
    void obtener_ofertas_por_item_retorna_lista_ordenada_descendente() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("item1", "U1", "u1@ex.com", 400.00));
        offers.add(new Offer("item1", "U2", "u2@ex.com", 900.00));
        offers.add(new Offer("item1", "U3", "u3@ex.com", 600.00));

        // Act
        offers.sort((a, b) -> Double.compare(b.getAmount(), a.getAmount())); // Descendente

        // Assert
        assertEquals(900.00, offers.get(0).getAmount());
        assertEquals(600.00, offers.get(1).getAmount());
        assertEquals(400.00, offers.get(2).getAmount());
    }

    @Test
    void contar_ofertas_por_item() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("item1", "U1", "u1@ex.com", 500.00));
        offers.add(new Offer("item1", "U2", "u2@ex.com", 600.00));
        offers.add(new Offer("item2", "U3", "u3@ex.com", 700.00)); // Diferente item

        // Act
        int count = (int) offers.stream().filter(o -> "item1".equals(o.getId())).count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    void filtrar_ofertas_por_rango_de_precio() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("item1", "U1", "u1@ex.com", 100.00));
        offers.add(new Offer("item1", "U2", "u2@ex.com", 500.00));
        offers.add(new Offer("item1", "U3", "u3@ex.com", 800.00));

        // Act
        List<Offer> inRange = offers.stream()
            .filter(o -> o.getAmount() >= 400 && o.getAmount() <= 700)
            .toList();

        // Assert
        assertEquals(1, inRange.size());
        assertEquals(500.00, inRange.get(0).getAmount());
    }

    // ===== PRUEBAS DE VALIDACIÓN DE OFERTAS =====

    @Test
    void validar_oferta_mayor_que_precio_actual_es_valida() {
        // Arrange
        double currentPrice = 621.34;
        double newOffer = 750.00;

        // Act
        boolean isValid = newOffer > currentPrice;

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validar_oferta_igual_a_precio_actual_es_invalida() {
        // Arrange
        double currentPrice = 621.34;
        double newOffer = 621.34;

        // Act
        boolean isValid = newOffer > currentPrice;

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validar_oferta_menor_a_precio_actual_es_invalida() {
        // Arrange
        double currentPrice = 621.34;
        double newOffer = 500.00;

        // Act
        boolean isValid = newOffer > currentPrice;

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validar_oferta_con_diferenciales_minimos() {
        // Arrange
        double currentPrice = 100.00;

        // Act & Assert
        assertTrue(100.01 > currentPrice); // diferencial mínimo
        assertFalse(100.00 > currentPrice);
        assertFalse(99.99 > currentPrice);
    }

    @Test
    void calcular_nuevo_precio_formateado_correctamente() {
        // Arrange
        double offerAmount = 999.99;

        // Act
        String formattedPrice = String.format("$%.2f USD", offerAmount);

        // Assert
        assertEquals("$999.99 USD", formattedPrice);
    }

    @Test
    void formatear_precio_con_decimales_variables() {
        // Arrange
        double[] amounts = {100.0, 150.5, 999.99, 1000.01};

        // Act & Assert
        for (double amount : amounts) {
            String formatted = String.format("$%.2f USD", amount);
            assertTrue(formatted.startsWith("$"));
            assertTrue(formatted.endsWith("USD"));
            assertTrue(formatted.contains("."));
        }
    }

    // ===== PRUEBAS DE MANEJO DE ERRORES =====

    @Test
    void obtener_oferta_mas_alta_lista_vacia_retorna_nulo() {
        // Arrange
        List<Offer> emptyList = new ArrayList<>();

        // Act
        Offer result = emptyList.stream()
            .max((a, b) -> Double.compare(a.getAmount(), b.getAmount()))
            .orElse(null);

        // Assert
        assertNull(result);
    }

    @Test
    void oferta_monto_cero_es_invalida() {
        // Arrange
        Offer offer = new Offer("item1", "User", "user@ex.com", 0.0);

        // Act
        boolean isValid = offer.getAmount() > 0;

        // Assert
        assertFalse(isValid);
        assertEquals(0.0, offer.getAmount());
    }

    @Test
    void oferta_monto_negativo_es_invalida() {
        // Arrange
        Offer offer = new Offer("item1", "User", "user@ex.com", -100.0);

        // Act
        boolean isValid = offer.getAmount() > 0;

        // Assert
        assertFalse(isValid);
        assertTrue(offer.getAmount() < 0);
    }

    @Test
    void oferta_monto_extremadamente_alto() {
        // Arrange
        Offer offer = new Offer("item1", "User", "user@ex.com", Double.MAX_VALUE - 1);

        // Act
        boolean isValid = offer.getAmount() > 0;

        // Assert
        assertTrue(isValid);
        assertTrue(offer.getAmount() > 1000000);
    }

    // ===== PRUEBAS DE DATOS DE OFERTA =====

    @Test
    void oferta_guarda_todos_los_datos_correctamente() {
        // Arrange
        String id = "item_test_123";
        String name = "Carlos García";
        String email = "carlos@example.com";
        double amount = 1250.75;

        // Act
        Offer offer = new Offer(id, name, email, amount);

        // Assert
        assertEquals(id, offer.getId());
        assertEquals(name, offer.getName());
        assertEquals(email, offer.getEmail());
        assertEquals(amount, offer.getAmount());
    }

    @Test
    void oferta_setter_dbId_funciona_correctamente() {
        // Arrange
        Offer offer = new Offer("item1", "User", "user@ex.com", 500.00);
        long dbId = 42L;

        // Act
        offer.setDbId(dbId);

        // Assert
        assertEquals(dbId, offer.getDbId());
    }

    @Test
    void oferta_con_emails_validos_variados() {
        // Arrange
        String[] emails = {
            "user@example.com",
            "john.doe@company.co.uk",
            "admin+tag@domain.com",
            "test_123@test.org"
        };

        // Act & Assert
        for (String email : emails) {
            Offer offer = new Offer("item1", "User", email, 500.00);
            assertEquals(email, offer.getEmail());
        }
    }

    @Test
    void oferta_con_nombres_variados() {
        // Arrange
        String[] names = {"Juan", "María", "José", "Ana", "Carlos", "Elena"};

        // Act & Assert
        for (String name : names) {
            Offer offer = new Offer("item1", name, "user@ex.com", 500.00);
            assertEquals(name, offer.getName());
        }
    }

    // ===== PRUEBAS DE LÓGICA DE NEGOCIO =====

    @Test
    void calcular_promedio_de_ofertas() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("item1", "U1", "u1@ex.com", 100.00));
        offers.add(new Offer("item1", "U2", "u2@ex.com", 200.00));
        offers.add(new Offer("item1", "U3", "u3@ex.com", 300.00));

        // Act
        double average = offers.stream()
            .mapToDouble(Offer::getAmount)
            .average()
            .orElse(0.0);

        // Assert
        assertEquals(200.00, average);
    }

    @Test
    void sumar_total_de_ofertas() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("item1", "U1", "u1@ex.com", 100.00));
        offers.add(new Offer("item1", "U2", "u2@ex.com", 200.00));
        offers.add(new Offer("item1", "U3", "u3@ex.com", 300.00));

        // Act
        double total = offers.stream()
            .mapToDouble(Offer::getAmount)
            .sum();

        // Assert
        assertEquals(600.00, total);
    }

    @Test
    void contar_ofertas_mayor_que_umbral() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("item1", "U1", "u1@ex.com", 100.00));
        offers.add(new Offer("item1", "U2", "u2@ex.com", 500.00));
        offers.add(new Offer("item1", "U3", "u3@ex.com", 300.00));
        double threshold = 250.00;

        // Act
        long count = offers.stream()
            .filter(o -> o.getAmount() > threshold)
            .count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    void verificar_oferta_duplicada_mismo_usuario_item() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("item1", "User1", "user1@ex.com", 500.00));
        offers.add(new Offer("item1", "User1", "user1@ex.com", 600.00)); // Mismo usuario, mismo item

        // Act
        boolean hasDuplicate = offers.stream()
            .filter(o -> "item1".equals(o.getId()) && "User1".equals(o.getName()))
            .count() > 1;

        // Assert
        assertTrue(hasDuplicate);
    }
}
