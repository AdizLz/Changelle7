/**
 * Modelo que representa un usuario del sistema de subastas.
 *
 * Responsabilidades:
 * - Almacenar información del perfil del usuario (id, nombre, email)
 * - Guardar la contraseña hasheada de forma segura
 * - Proporcionar getters y setters para acceso a datos
 *
 * Seguridad:
 * - La contraseña se almacena hasheada con SHA-256 + Base64
 * - Nunca se transmite contraseña en texto plano
 *
 * @see AuthService
 * @see UserService
 */
package org.example.model;

public class User {
    /** Identificador único del usuario (UUID) */
    private String id;

    /** Nombre completo del usuario */
    private String name;

    /** Correo electrónico único del usuario */
    private String email;

    /** Contraseña hasheada (SHA-256 + Base64) */
    private String password;

    /**
     * Constructor sin argumentos para deserialización JSON.
     */
    public User() { }

    /**
     * Constructor con parámetros básicos.
     *
     * @param id Identificador único (UUID recomendado)
     * @param name Nombre completo del usuario
     * @param email Correo electrónico (debe ser único en el sistema)
     */
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Obtiene el identificador único del usuario.
     *
     * @return ID del usuario (UUID)
     */
    public String getId() { return id; }

    /**
     * Establece el identificador del usuario.
     *
     * @param id Identificador único a asignar
     */
    public void setId(String id) { this.id = id; }

    /**
     * Obtiene el nombre completo del usuario.
     *
     * @return Nombre del usuario
     */
    public String getName() { return name; }

    /**
     * Establece el nombre completo del usuario.
     *
     * @param name Nombre a asignar
     */
    public void setName(String name) { this.name = name; }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return Email único del usuario
     */
    public String getEmail() { return email; }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email Email a asignar (debe validarse que sea único)
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Obtiene la contraseña hasheada del usuario.
     *
     * @return Contraseña en formato SHA-256 + Base64
     */
    public String getPassword() { return password; }

    /**
     * Establece la contraseña del usuario (debe estar hasheada antes).
     *
     * Precaución: Este método debe recibir contraseña ya hasheada.
     * Ver {@link AuthService#hashPassword(String)} para hashear.
     *
     * @param password Contraseña hasheada a almacenar
     */
    public void setPassword(String password) { this.password = password; }
}
