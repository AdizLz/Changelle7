/**
 * Modelo que representa un artículo en el sistema de subastas.
 *
 * Responsabilidades:
 * - Almacenar información del artículo (id, nombre, descripción, precio)
 * - Ser la entidad central del sistema de subastas
 * - Servir como referencia para ofertas y transacciones
 *
 * Propiedades:
 * - id: Identificador único del artículo
 * - name: Nombre descriptivo del artículo
 * - description: Detalles y características del artículo
 * - price: Precio actual (en formato String para soportar múltiples formatos)
 *
 * @see OfferService
 * @see ItemService
 */
package org.example.model;

public class Item {
    /** Identificador único del artículo (UUID) */
    private String id;

    /** Nombre descriptivo del artículo */
    private String name;

    /** Descripción detallada del artículo y sus características */
    private String description;

    /** Precio actual del artículo (formato: "$X.XX USD") */
    private String price;

    /**
     * Constructor sin argumentos para deserialización JSON.
     */
    public Item() { }

    /**
     * Constructor con todos los parámetros del artículo.
     *
     * @param id Identificador único del artículo (UUID)
     * @param name Nombre del artículo
     * @param description Descripción detallada
     * @param price Precio actual en formato String
     */
    public Item(String id, String name, String description, String price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    /**
     * Obtiene el identificador único del artículo.
     *
     * @return ID del artículo (UUID)
     */
    public String getId() { return id; }

    /**
     * Establece el identificador del artículo.
     *
     * @param id Identificador único a asignar
     */
    public void setId(String id) { this.id = id; }

    /**
     * Obtiene el nombre del artículo.
     *
     * @return Nombre descriptivo del artículo
     */
    public String getName() { return name; }

    /**
     * Establece el nombre del artículo.
     *
     * @param name Nombre a asignar
     */
    public void setName(String name) { this.name = name; }

    /**
     * Obtiene la descripción del artículo.
     *
     * @return Descripción detallada con características
     */
    public String getDescription() { return description; }

    /**
     * Establece la descripción del artículo.
     *
     * @param description Descripción a asignar
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Obtiene el precio actual del artículo.
     *
     * Formato esperado: "$X.XX USD" o valor numérico sin formato
     *
     * @return Precio actual como String
     */
    public String getPrice() { return price; }

    /**
     * Establece el precio actual del artículo.
     *
     * Este método se usa cuando hay una nueva oferta que aumenta el precio.
     * El precio debe actualizarse antes de notificar a través de WebSocket.
     *
     * @param price Nuevo precio (puede incluir formato: "$X.XX USD")
     */
    public void setPrice(String price) { this.price = price; }
}
