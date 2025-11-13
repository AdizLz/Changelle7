package org.example.model;

/**
 * Modelo que representa una oferta (bid) en una subasta.
 *
 * Responsabilidades:
 * - Almacenar información de la oferta: monto, usuario, artículo
 * - Registrar detalles del oferente (nombre y email)
 * - Relacionar la oferta con el artículo correspondiente
 *
 * Propiedades:
 * - dbId: ID autogenerado en la base de datos (opcional en JSON)
 * - id: ID del artículo al que pertenece la oferta (campo "item_id")
 * - name: Nombre del oferente
 * - email: Correo del oferente
 * - amount: Monto de la oferta en USD
 *
 * Validaciones:
 * - El monto debe ser mayor que el precio actual del artículo
 * - El monto debe ser mayor que cualquier oferta anterior
 * - Email debe ser válido
 *
 * @see OfferService
 * @see ItemService
 */
public class Offer {
    /** ID autogenerado en la tabla 'offers' (opcional en JSON) */
    private Long dbId;

    /** ID del artículo asociado a esta oferta (referencia a item_id) */
    private String id;

    /** Nombre del oferente */
    private String name;

    /** Correo electrónico del oferente */
    private String email;

    /** Monto de la oferta en USD */
    private double amount;

    /**
     * Constructor sin argumentos para deserialización JSON.
     */
    public Offer() { }

    /**
     * Constructor con parámetros principales.
     *
     * @param id ID del artículo a ofertar
     * @param name Nombre del oferente
     * @param email Email del oferente
     * @param amount Monto de la oferta en USD
     */
    public Offer(String id, String name, String email, double amount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.amount = amount;
    }

    /**
     * Obtiene el ID autogenerado de la oferta en la base de datos.
     *
     * @return ID de la base de datos (null si no se ha persistido aún)
     */
    public Long getDbId() { return dbId; }

    /**
     * Establece el ID autogenerado de la oferta.
     *
     * Este método se llama después de insertar en la BD cuando se genera el ID.
     *
     * @param dbId ID autogenerado a asignar
     */
    public void setDbId(Long dbId) { this.dbId = dbId; }

    /**
     * Obtiene el ID del artículo asociado a esta oferta.
     *
     * Nota: El campo se llama "id" por compatibilidad con la API,
     * pero representa el "item_id" en la base de datos.
     *
     * @return ID del artículo a ofertar
     */
    public String getId() { return id; }

    /**
     * Establece el ID del artículo para esta oferta.
     *
     * @param id ID del artículo a ofertar
     */
    public void setId(String id) { this.id = id; }

    /**
     * Obtiene el nombre del oferente.
     *
     * @return Nombre de quien realiza la oferta
     */
    public String getName() { return name; }

    /**
     * Establece el nombre del oferente.
     *
     * @param name Nombre a asignar
     */
    public void setName(String name) { this.name = name; }

    /**
     * Obtiene el correo electrónico del oferente.
     *
     * @return Email de contacto del oferente
     */
    public String getEmail() { return email; }

    /**
     * Establece el correo electrónico del oferente.
     *
     * @param email Email a asignar
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Obtiene el monto de la oferta.
     *
     * @return Monto en USD de la oferta
     */
    public double getAmount() { return amount; }

    /**
     * Establece el monto de la oferta.
     *
     * Precaución: Antes de guardar, validar que el monto sea mayor que:
     * 1. El precio actual del artículo
     * 2. Cualquier oferta anterior (ver OfferService.getHighestOffer)
     *
     * @param amount Monto en USD a asignar
     */
    public void setAmount(double amount) { this.amount = amount; }
}
