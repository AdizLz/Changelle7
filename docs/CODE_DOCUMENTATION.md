# Documentación Completa del Código - Reto6

## 📌 Introducción

Este documento proporciona pautas exhaustivas para documentar y mantener el código del proyecto Reto6, asegurando claridad, consistencia y facilidad de mantenimiento para todo el equipo Digital NAO.

---

## 1️⃣ Estándar de Documentación: Javadoc

### Formato Básico

Todo código público en Java debe incluir comentarios Javadoc siguiendo este formato:

```java
/**
 * Descripción breve de la clase/método (máximo una línea).
 *
 * Descripción detallada (puede ocupar múltiples líneas).
 * Explicar:
 * - Qué hace
 * - Por qué existe
 * - Cuándo se usa
 * - Casos especiales
 *
 * @param nombreParámetro Descripción del parámetro
 * @return Descripción del valor retornado
 * @throws TipoExcepción Cuándo se lanza esta excepción
 * @see ClaseRelacionada
 * @deprecated Motivo de deprecación (si aplica)
 */
public ReturnType metodo(Type parametro) {
    // Implementación
}
```

### Reglas Importantes

- ✅ **Obligatorio para:** Clases públicas, interfaces, métodos públicos
- ✅ **Recomendado para:** Métodos privados complejos, campos privados importantes
- ✅ **Lenguaje:** Español (consistencia con el proyecto)
- ✅ **Primer párrafo:** Resumen de una línea
- ✅ **Párrafos siguientes:** Detalles, ejemplos, notas

---

## 2️⃣ Documentación de Clases

### Plantilla para Modelos (Entidades)

```java
/**
 * Modelo que representa [entidad] en el sistema [nombre sistema].
 *
 * Responsabilidades:
 * - [Responsabilidad 1]
 * - [Responsabilidad 2]
 * - [Responsabilidad 3]
 *
 * Propiedades:
 * - [propiedad1]: [descripción]
 * - [propiedad2]: [descripción]
 *
 * @see RelatedService
 * @see RelatedModel
 */
public class MyModel {
    // Campos y métodos documentados
}
```

**Ejemplo Real: User.java**

```java
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
public class User {
    /** Identificador único del usuario (UUID) */
    private String id;
    
    // Resto de campos y métodos...
}
```

### Plantilla para Servicios

```java
/**
 * Servicio [nombre] del sistema.
 *
 * Proporciona operaciones [tipo] para [entidad/funcionalidad].
 *
 * Responsabilidades:
 * - [Responsabilidad 1]
 * - [Responsabilidad 2]
 *
 * Ejemplos de uso:
 * {@code
 * MyService service = new MyService();
 * ResultType result = service.doSomething(param);
 * }
 *
 * @see DependencyClass
 * @see Model
 */
public class MyService {
    // Métodos documentados
}
```

**Ejemplo Real: AuthService.java**

```java
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
    // Implementación...
}
```

---

## 3️⃣ Documentación de Métodos

### Métodos Getter/Setter Simples

```java
/**
 * Obtiene el [nombre del campo].
 *
 * @return [Descripción de qué se retorna]
 */
public Type getField() {
    return field;
}

/**
 * Establece el [nombre del campo].
 *
 * @param field [Descripción del parámetro]
 */
public void setField(Type field) {
    this.field = field;
}
```

### Métodos Complejos

```java
/**
 * [Verbo+acción] [objeto] bajo [condiciones].
 *
 * Proceso:
 * 1. Paso 1
 * 2. Paso 2
 * 3. Paso 3
 *
 * Validaciones:
 * - Validación 1
 * - Validación 2
 *
 * @param param1 Descripción del parámetro 1
 * @param param2 Descripción del parámetro 2
 * @return Descripción detallada del retorno
 * @throws ExceptionType Situación que causa esta excepción
 *
 * @see RelatedMethod
 */
public ReturnType complexMethod(Type param1, Type param2) throws ExceptionType {
    // Implementación
}
```

**Ejemplo Real: AuthService.login()**

```java
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
    // ... validación de contraseña
    return u;
}
```

---

## 4️⃣ Comentarios Inline (En el código)

### Cuándo Usar

- ✅ Lógica compleja o no obvia
- ✅ Decisiones arquitectónicas importantes
- ✅ Bugs conocidos o workarounds
- ❌ No para código autoexplicativo

### Ejemplos Correctos

```java
// ✅ BIEN: Explica por qué, no qué
// Se usa SHA-256 en lugar de bcrypt para reducir dependencias externas
// En producción, cambiar a bcrypt para mayor seguridad
String hashed = hashWithSHA256(password);

// ✅ BIEN: Explica una decisión
// Usar HashMap en lugar de TreeMap porque no necesitamos orden
Map<String, Item> items = new HashMap<>();

// ❌ MAL: Obvio, no necesita comentario
// Incrementar contador
counter++;
```

### Bloques de Comentarios para Secciones

```java
public class Main {
    // ===============================
    // ⚙️ CONFIGURACIÓN DEL PUERTO
    // ===============================
    int port = 55603;

    // ===============================
    // 🗄️ INICIALIZACIÓN BASE DE DATOS
    // ===============================
    DatabaseManager.init();

    // ===============================
    // 🧩 SERVICIOS
    // ===============================
    UserService userService = new UserService();
}
```

---

## 5️⃣ Documentación de Excepciones

### Lanzar Excepciones Significativas

```java
/**
 * Registra un nuevo usuario en el sistema.
 *
 * @param id Identificador único del usuario
 * @param email Email del usuario
 * @return Usuario registrado
 * @throws IllegalArgumentException Si el ID ya existe o el email está registrado
 */
public User register(String id, String email) {
    if (userService.exists(id)) {
        throw new IllegalArgumentException("User already exists");
    }
    if (userService.findByEmail(email) != null) {
        throw new IllegalArgumentException("Email already registered");
    }
    // Lógica de registro...
    return user;
}
```

---

## 6️⃣ Documentación de Campos (Fields)

```java
public class Item {
    /** Identificador único del artículo (UUID) */
    private String id;

    /** Nombre descriptivo del artículo */
    private String name;

    /** Precio actual en formato "$X.XX USD" */
    private String price;

    /** Fecha de creación en timestamp Unix */
    private long createdAt;
}
```

---

## 7️⃣ Clases Completamente Documentadas

### AuthService (Ejemplo Completo)

```java
/**
 * Servicio de autenticación y registro de usuarios.
 *
 * Responsabilidades:
 * - Validar credenciales de usuario (email y contraseña)
 * - Registrar nuevos usuarios con validaciones
 * - Hashear y verificar contraseñas usando SHA-256 + Base64
 * - Gestionar sesiones de usuario a través de UserService
 *
 * Nota: Las contraseñas se hashean usando SHA-256 + Base64.
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
            throw new RuntimeException("Error al hashear contraseña: " + e.getMessage());
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param id Identificador único del usuario (UUID)
     * @param name Nombre completo del usuario
     * @param email Email del usuario (debe ser único)
     * @param plainPassword Contraseña en texto plano
     * @return Usuario registrado con contraseña hasheada
     * @throws IllegalArgumentException Si el ID existe o email ya está registrado
     */
    public User register(String id, String name, String email, String plainPassword) {
        if (userService.exists(id)) {
            throw new IllegalArgumentException("User already exists");
        }
        if (userService.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        String hashed = hashPassword(plainPassword);
        User u = new User(id, name, email);
        u.setPassword(hashed);
        userService.add(u);
        logger.info("Usuario registrado: {} ({})", name, id);
        return u;
    }

    /**
     * Intenta iniciar sesión con credenciales proporcionadas.
     *
     * @param email Email del usuario
     * @param plainPassword Contraseña en texto plano
     * @return Usuario autenticado o null si falla
     */
    public User login(String email, String plainPassword) {
        User u = userService.findByEmail(email);
        if (u == null) return null;
        if (verifyPassword(plainPassword, u.getPassword())) {
            return u;
        }
        return null;
    }
}
```

---

## 8️⃣ Generador de Javadoc HTML

### Generar Documentación en HTML

```bash
# Generar Javadoc para todo el proyecto
mvn javadoc:javadoc

# Generar y abrir en navegador (Windows)
mvn javadoc:javadoc && start target\site\apidocs\index.html

# Generar solo para módulo específico
mvn javadoc:javadoc -DexcludePackageNames="org.example.test"
```

Los archivos HTML se generarán en:
```
target/site/apidocs/
```

---

## 9️⃣ Checklist de Documentación

Antes de hacer commit, verificar:

- ✅ **Clases públicas:** Tienen Javadoc completo
- ✅ **Métodos públicos:** Documentados con @param, @return, @throws
- ✅ **Campos importantes:** Tienen comentarios JavaDoc
- ✅ **Excepciones:** Están documentadas
- ✅ **Lógica compleja:** Tiene comentarios inline
- ✅ **Referencias:** Usa @see para relacionar clases
- ✅ **Ejemplos:** Incluye ejemplos de uso cuando sea relevante
- ✅ **Formato:** Sigue estructura de párrafo + detalles

### Comando para Verificar

```bash
# Ver clases sin Javadoc
mvn checkstyle:check -Dcheckstyle.config.location=google_checks.xml
```

---

## 🔟 Resumen de Clases Documentadas en Reto6

| Clase | Ubicación | Estado | Detalles |
|---|---|---|---|
| **User** | model/ | ✅ Documentada | 8 métodos + campos |
| **Item** | model/ | ✅ Documentada | 8 métodos + campos |
| **Offer** | model/ | ✅ Documentada | 10 métodos + campos |
| **AuthService** | service/ | ✅ Documentada | Login, registro, hash |
| **UserService** | service/ | ✅ Documentada | CRUD + búsqueda |
| **ItemService** | service/ | ✅ Documentada | CRUD + filtrado |
| **OfferService** | service/ | ✅ Documentada | CRUD + validación |
| **SessionManager** | service/ | ✅ Documentada | Gestión de sesiones |
| **DatabaseManager** | root/ | ✅ Documentada | Conexiones a BD |
| **PriceUpdateWebSocket** | controller/ | ✅ Documentada | WebSocket |
| **Main** | root/ | ✅ Comentada | Rutas y configuración |

---

## 🎯 Mejores Prácticas

### ✅ Código Bien Documentado

```java
/**
 * Valida y guarda una nueva oferta para un artículo.
 *
 * Validaciones realizadas:
 * 1. El artículo existe en el sistema
 * 2. La oferta es mayor que el precio actual
 * 3. La oferta es mayor que cualquier oferta anterior
 *
 * Si todas las validaciones pasan:
 * - Guarda la oferta en la base de datos
 * - Actualiza el precio del artículo
 * - Notifica a través de WebSocket
 *
 * @param offer Oferta a procesar
 * @return Respuesta con éxito, nuevo precio y oferta guardada
 * @throws IllegalArgumentException Si la oferta es inválida
 */
public Map<String, Object> processOffer(Offer offer) {
    // Implementación validada y documentada
}
```

### ❌ Código Mal Documentado

```java
// Procesar oferta
public void processOffer(Offer offer) {
    // ... sin documentación
}
```

---

## 📚 Recursos Adicionales

- [Javadoc Official Guide](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Javadoc Tags Reference](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html#CHDJGIJB)

---

## 📋 Conclusión

La documentación es **parte integral del código**, no una tarea adicional. Un código bien documentado:

- Facilita el mantenimiento futuro
- Ayuda a nuevos desarrolladores
- Reduce bugs y conflictos
- Mejora la calidad del proyecto
- Facilita la revisión de código

**Recuerda:** El código que escribes hoy, alguien más (o tú) lo leerá mañana. Documenta pensando en esa persona.

---

**Última actualización:** Sprint 3, 2024
