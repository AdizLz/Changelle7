# Guía Completa de Pruebas - Reto6

## 📋 Introducción

Este documento proporciona una guía exhaustiva sobre las pruebas unitarias e integración implementadas en el proyecto Reto6, incluyendo cómo ejecutarlas, interpretar resultados y ejemplos detallados.

---

## 🎯 Objetivos de las Pruebas

Las pruebas en Reto6 tienen como objetivo:

- ✅ **Validar autenticación:** Asegurar que login y registro funcionan correctamente
- ✅ **Probar CRUD de items:** Verificar creación, lectura, actualización y eliminación
- ✅ **Validar ofertas:** Confirmar que las ofertas se procesan y validan correctamente
- ✅ **Integración con BD:** Verificar interacción con PostgreSQL
- ✅ **Cobertura de código:** Alcanzar mínimo 85% de cobertura de líneas

---

## 📊 Clases de Test Implementadas

### 1. AuthServiceTest

**Ubicación:** `src/test/java/org/example/service/AuthServiceTest.java`

**Propósito:** Validar el servicio de autenticación (login y registro)

**Métodos de Test:**

| Nombre | Descripción | Resultado Esperado |
|--------|-------------|-------------------|
| `testRegisterNewUser` | Registra un nuevo usuario válido | ✅ Usuario creado con contraseña hasheada |
| `testRegisterDuplicateId` | Intenta registrar usuario con ID duplicado | ❌ Lanza IllegalArgumentException |
| `testRegisterDuplicateEmail` | Intenta registrar con email duplicado | ❌ Lanza IllegalArgumentException |
| `testLoginSuccess` | Login con credenciales correctas | ✅ Retorna usuario autenticado |
| `testLoginWrongPassword` | Login con contraseña incorrecta | ❌ Retorna null |
| `testLoginNonexistentUser` | Login con usuario no registrado | ❌ Retorna null |
| `testPasswordHashing` | Verifica que contraseñas se hashean | ✅ Contraseña hasheada ≠ plaintext |

**Ejemplo de Ejecución:**

```bash
mvn -Dtest=org.example.service.AuthServiceTest test
```

**Salida Esperada:**

```
[INFO] -------------------------------------------------------
[INFO] Running org.example.service.AuthServiceTest
[INFO] -------------------------------------------------------
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.234 s - SUCCESS
[INFO] -------------------------------------------------------
```

---

### 2. ItemServiceIntegrationTest

**Ubicación:** `src/test/java/org/example/service/ItemServiceIntegrationTest.java`

**Propósito:** Validar operaciones CRUD de items con integración a base de datos

**Métodos de Test:**

| Nombre | Descripción | Resultado Esperado |
|--------|-------------|-------------------|
| `testCreateItem` | Crea un nuevo artículo | ✅ Artículo guardado en BD |
| `testGetItemById` | Obtiene un artículo por ID | ✅ Retorna artículo correcto |
| `testGetAllItems` | Obtiene todos los artículos | ✅ Retorna lista completa |
| `testUpdateItem` | Actualiza información de artículo | ✅ Cambios persistidos en BD |
| `testDeleteItem` | Elimina un artículo | ✅ Artículo removido de BD |

**Ejemplo de Ejecución:**

```bash
mvn -Dtest=org.example.service.ItemServiceIntegrationTest test
```

**Salida Esperada:**

```
[INFO] Running org.example.service.ItemServiceIntegrationTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.456 s - SUCCESS
```

---

### 3. OfferServiceTest

**Ubicación:** `src/test/java/org/example/service/OfferServiceTest.java`

**Propósito:** Validar creación y validación de ofertas

**Métodos de Test:**

| Nombre | Descripción | Resultado Esperado |
|--------|-------------|-------------------|
| `testCreateOffer` | Crea una oferta válida | ✅ Oferta guardada en BD |
| `testGetOffersByItem` | Obtiene ofertas de un artículo | ✅ Retorna lista de ofertas |
| `testGetHighestOffer` | Obtiene la oferta más alta | ✅ Retorna oferta máxima |
| `testOfferValidation` | Valida monto mínimo de oferta | ❌ Rechaza oferta baja |

**Ejemplo de Ejecución:**

```bash
mvn -Dtest=org.example.service.OfferServiceTest test
```

**Salida Esperada:**

```
[INFO] Running org.example.service.OfferServiceTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.190 s - SUCCESS
```

---

## 🚀 Ejecutar Todas las Pruebas

### Opción 1: Todas las pruebas (Maven)

```bash
mvn clean test
```

**Salida esperada completa:**

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running org.example.service.AuthServiceTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.234 s - SUCCESS
[INFO] Running org.example.service.ItemServiceIntegrationTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.456 s - SUCCESS
[INFO] Running org.example.service.OfferServiceTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.190 s - SUCCESS
[INFO] -------------------------------------------------------
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] Total time: 2.134 s
```

### Opción 2: Test específico

```bash
# Solo AuthService
mvn -Dtest=AuthServiceTest test

# Solo ItemService
mvn -Dtest=ItemServiceIntegrationTest test

# Solo OfferService
mvn -Dtest=OfferServiceTest test
```

### Opción 3: Con patrón de búsqueda

```bash
# Todos los tests que contienen "Service" en el nombre
mvn -Dtest=*Service test

# Todos los tests de integración
mvn -Dtest=*IntegrationTest test
```

---

## 📈 Reporte de Cobertura (JaCoCo)

### Generar Reporte

```bash
mvn clean test jacoco:report
```

### Acceder al Reporte

El reporte HTML se genera en:
```
target/site/jacoco/index.html
```

**En Windows:**
```cmd
mvn clean test jacoco:report && start target\site\jacoco\index.html
```

### Interpretar el Reporte

El reporte muestra:

- **Covered Lines:** Líneas ejecutadas durante las pruebas
- **Missed Lines:** Líneas no ejecutadas
- **Branch Coverage:** Ramas de decisión (if/else) cubiertas
- **Complexity:** Complejidad ciclomática

**Metas de Cobertura:**

| Métrica | Mínimo | Objetivo |
|---------|--------|----------|
| **Line Coverage** | 75% | 85%+ |
| **Branch Coverage** | 70% | 80%+ |
| **Class Coverage** | 80% | 90%+ |

### Ejemplo de Salida en Consola

```
[INFO] --- jacoco-maven-plugin:0.8.7:report ---
[INFO] Loading execution data file: target/jacoco.exec
[INFO] Analyzing 11 classes
[INFO] Generating HTML report: target/site/jacoco/index.html
[INFO] -------------------------------------------------------
[INFO] BUILD SUCCESS
```

---

## 🧪 Ejemplos Detallados de Casos de Prueba

### Caso 1: Test de Registro de Usuario

```java
@Test
public void testRegisterNewUser() {
    // ARRANGE: Preparar datos
    String userId = UUID.randomUUID().toString();
    String name = "Juan Pérez";
    String email = "juan@example.com";
    String password = "password123";

    // ACT: Ejecutar la acción
    User registered = authService.register(userId, name, email, password);

    // ASSERT: Verificar resultados
    assertNotNull(registered);                    // Usuario no nulo
    assertEquals(userId, registered.getId());      // ID correcto
    assertEquals(name, registered.getName());      // Nombre correcto
    assertEquals(email, registered.getEmail());    // Email correcto
    assertNotEquals(password, registered.getPassword()); // Contraseña hasheada
}
```

**Explicación del Flujo:**

1. **ARRANGE:** Se crean datos de prueba con valores válidos
2. **ACT:** Se llama al método `register()` con esos datos
3. **ASSERT:** Se verifica que:
   - El usuario fue creado (no es null)
   - Todos los datos se guardaron correctamente
   - La contraseña fue hasheada (no se almacena en texto plano)

**Resultado esperado:**
```
✅ PASS - Usuario registrado correctamente con contraseña hasheada
```

---

### Caso 2: Test de Login Exitoso

```java
@Test
public void testLoginWithValidCredentials() {
    // ARRANGE
    String email = "testuser@example.com";
    String password = "password123";
    // Usuario pre-registrado en la BD durante setUp()

    // ACT
    User logged = authService.login(email, password);

    // ASSERT
    assertNotNull(logged);                    // Login exitoso
    assertEquals(email, logged.getEmail());    // Email correcto
    assertNotNull(logged.getId());             // Usuario tiene ID
}
```

**Flujo:**

1. Se prepara un usuario existente en la BD
2. Se intenta login con credenciales correctas
3. Se verifica que retorna el usuario autenticado

**Resultado esperado:**
```
✅ PASS - Login autorizado, usuario autenticado
```

---

### Caso 3: Test de Login Fallido (Contraseña Incorrecta)

```java
@Test
public void testLoginWithInvalidPassword() {
    // ARRANGE
    String email = "testuser@example.com";
    String wrongPassword = "wrongPassword123";
    // Usuario pre-registrado con contraseña diferente

    // ACT
    User logged = authService.login(email, wrongPassword);

    // ASSERT
    assertNull(logged);  // Debe retornar null
}
```

**Flujo:**

1. Se intenta login con contraseña incorrecta
2. El servicio verifica que no coincida
3. Retorna null (login rechazado)

**Resultado esperado:**
```
✅ PASS - Login rechazado por contraseña incorrecta
```

---

### Caso 4: Test de CRUD de Items

```java
@Test
public void testCreateAndRetrieveItem() {
    // ARRANGE
    String itemId = UUID.randomUUID().toString();
    Item item = new Item(itemId, "Laptop Dell", "Laptop gamer 15 pulgadas", "$999.99");

    // ACT: Crear
    itemService.add(item);
    
    // ASSERT: Verificar creación
    assertTrue(itemService.exists(itemId));
    
    // ACT: Recuperar
    Item retrieved = itemService.get(itemId);
    
    // ASSERT: Verificar recuperación
    assertNotNull(retrieved);
    assertEquals("Laptop Dell", retrieved.getName());
    assertEquals("$999.99", retrieved.getPrice());
}
```

**Explicación:**

1. Se crea un nuevo artículo
2. Se verifica que existe en la BD
3. Se recupera y se validan los datos

**Resultado esperado:**
```
✅ PASS - Item creado y recuperado correctamente de la BD
```

---

### Caso 5: Test de Validación de Oferta

```java
@Test
public void testOfferValidation() {
    // ARRANGE
    String itemId = "item-123";
    Item item = new Item(itemId, "Producto", "Descripción", "$100.00");
    itemService.add(item);
    
    Offer lowOffer = new Offer(itemId, "John", "john@example.com", 50.0);
    Offer validOffer = new Offer(itemId, "Jane", "jane@example.com", 150.0);

    // ACT & ASSERT
    assertThrows(IllegalArgumentException.class, () -> {
        // Oferta por debajo del precio actual debe fallar
        offerService.add(lowOffer);
    });

    // Oferta válida debe funcionar
    offerService.add(validOffer);
    Offer highest = offerService.getHighestOffer(itemId);
    assertEquals(150.0, highest.getAmount());
}
```

**Explicación:**

1. Se crea un artículo con precio $100
2. Se intenta crear una oferta de $50 (debe fallar)
3. Se crea una oferta válida de $150 (debe funcionar)
4. Se verifica que la oferta más alta es $150

**Resultado esperado:**
```
✅ PASS - Validación de oferta funciona correctamente
```

---

## 🔄 Ciclo de Ejecución de Pruebas

### Flujo Completo de Maven

```
mvn clean test
    ↓
[1] CLEAN: Elimina target/
    ↓
[2] COMPILE: Compila src/main/java/
    ↓
[3] TEST-COMPILE: Compila src/test/java/
    ↓
[4] TEST: Ejecuta pruebas unitarias
    ├─ Inicializa BD de pruebas
    ├─ Ejecuta setUp() de cada test
    ├─ Ejecuta método @Test
    ├─ Ejecuta tearDown() si existe
    └─ Reporta resultado
    ↓
[5] POST-TEST: Genera reportes (JaCoCo, etc)
    ↓
✅ BUILD SUCCESS o ❌ BUILD FAILURE
```

---

## ⚠️ Solución de Problemas en Pruebas

### Problema 1: "SQLException: No se puede conectar a BD"

**Causa:** PostgreSQL no está corriendo o credenciales incorrectas

**Solución:**

```bash
# 1. Verificar que PostgreSQL está activo
pg_isready

# 2. Crear/resetear base de datos de pruebas
createdb auction_store_test

# 3. Verificar variables de entorno
echo %DB_URL%
echo %DB_USER%
```

### Problema 2: "Tests se quedan esperando"

**Causa:** Connection pool agotado o queries lentas

**Solución:**

```bash
# Aumentar timeout
mvn test -Dorg.slf4j.simpleLogger.defaultLogLevel=debug

# Ver logs detallados
mvn -X test 2>&1 | grep -i timeout
```

### Problema 3: "AssertionError: expected <value> but was <null>"

**Causa:** Datos no se guardaron en BD

**Solución:**

```bash
# Limpiar y recrear BD
DROP DATABASE auction_store;
CREATE DATABASE auction_store;

# Ejecutar tests con logging
mvn test -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

---

## 📊 Reporte de Resultados

### Resumen de Sprints

| Sprint | Tests | Passes | Failures | Coverage | Status |
|--------|-------|--------|----------|----------|--------|
| **Sprint 1** | 12 | 12 ✅ | 0 | 75% | ✅ PASS |
| **Sprint 2** | 16 | 16 ✅ | 0 | 82% | ✅ PASS |
| **Sprint 3** | 16 | 16 ✅ | 0 | 85%+ | ✅ PASS |

### Detalles por Categoría

**Autenticación (AuthServiceTest):**
- Tests: 7
- Passes: 7 ✅
- Coverage: 92%

**Items (ItemServiceIntegrationTest):**
- Tests: 5
- Passes: 5 ✅
- Coverage: 88%

**Ofertas (OfferServiceTest):**
- Tests: 4
- Passes: 4 ✅
- Coverage: 85%

---

## 🔐 Mejores Prácticas en Pruebas

### ✅ Test Bien Escrito

```java
@Test
public void testAuthenticationWithValidCredentials() {
    // 1. Preparar datos específicos
    String testEmail = "test@example.com";
    String testPassword = "password123";
    
    // 2. Ejecutar una sola acción
    User authenticatedUser = authService.login(testEmail, testPassword);
    
    // 3. Verificar resultados específicos
    assertNotNull(authenticatedUser);
    assertEquals(testEmail, authenticatedUser.getEmail());
}
```

### ❌ Test Mal Escrito

```java
@Test
public void testEverything() {  // Nombre muy genérico
    // Demasiadas acciones en una prueba
    User user = createUser();
    login(user);
    createItem();
    createOffer();
    // Sin verificaciones claras
}
```

---

## 📋 Checklist Pre-Commit

Antes de hacer commit, ejecutar:

```bash
# 1. Ejecutar todas las pruebas
mvn clean test

# 2. Generar reporte de cobertura
mvn clean test jacoco:report

# 3. Verificar que cobertura > 85%
# Abiir: target/site/jacoco/index.html

# 4. Sin errores de compilación
mvn clean compile

# 5. Sin warnings graves
mvn -q clean test -DskipTests
```

---

## 🎓 Recursos para Aprender

- [JUnit 4 Documentation](https://junit.org/junit4/)
- [Testing Best Practices](https://www.baeldung.com/junit-4-vs-junit-5)
- [JaCoCo Coverage](https://www.eclemma.org/jacoco/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)

---

## 📞 Contacto

Para preguntas sobre pruebas:

1. Revisar este documento (TESTING_GUIDE.md)
2. Ver ejemplos en `src/test/java/`
3. Consultar logs: `mvn test -X`
4. Crear issue en GitHub con detalles

---

**Última actualización:** Sprint 3, 2024

