# 🎯 Proyecto Reto6 - Sistema de Subastas con Autenticación y Ofertas en Tiempo Real

## 📋 Descripción General

Aplicación web de subastas/tienda desarrollada en **Java** (Spark, PostgreSQL) con sistema de autenticación de usuarios, gestión de items y ofertas en tiempo real mediante WebSockets.

**Stack técnico:**
- Backend: Java 17, Spark (framework web)
- BD: PostgreSQL (usuarios, items, ofertas)
- Frontend: Mustache (templates HTML)
- Testing: JUnit 5, Mockito, JaCoCo (cobertura)
- CI/CD: Maven

---

## 🔐 Nuevas Funcionalidades Implementadas

### 1. Sistema de Login/Registro
- **Funcionalidad:** Crear cuenta nueva y autenticarse.
- **Ubicación:** `/register`, `/login`, `/logout`
- **Hashing:** SHA-256 + Base64 (sin dependencias externas como BCrypt).
- **Sesiones:** Gestionadas por Spark (cookies de sesión seguras).

### 2. Validación de Ofertas
- **Regla:** Una nueva oferta debe ser **estrictamente mayor** que el precio actual del item (o la oferta más alta existente).
- **Validación:** En el handler POST `/api/offers` de `Main.java`.
- **Rechazo:** Si la oferta es ≤ baseline, devuelve HTTP 400 con mensaje de error.

### 3. Actualización en Tiempo Real
- **WebSocket:** `/ws/prices` para notificaciones de cambio de precio.
- **Cliente:** `script.js` abre conexión WebSocket y actualiza UI automáticamente.
- **Servidor:** `PriceUpdateWebSocket.java` envía mensajes a todos los clientes conectados.

### 4. Persistencia en Base de Datos
- **Tabla `users`:** ID (UUID), nombre, email, password hasheado, timestamp.
- **Tabla `items`:** ID, nombre, descripción, precio (se actualiza con cada oferta).
- **Tabla `offers`:** ID autogenerado, datos de oferta, item_id (FK), monto, timestamp.

---

## 🧪 Pruebas Unitarias

### Cobertura de Código
- **Objetivo:** ≥ 70% de cobertura (configurado en `pom.xml`).
- **Herramienta:** JaCoCo (reporte en `target/site/jacoco/index.html`).

### Clases Testeadas

#### 1. **AuthServiceTest.java**
Pruebas para login/registro:
- ✅ Registro exitoso con validación de datos únicos.
- ✅ Rechazo de ID duplicado.
- ✅ Rechazo de email duplicado.
- ✅ Login con credenciales válidas.
- ✅ Rechazo de email no existente.
- ✅ Rechazo de password incorrecto.
- ✅ Manejo de usuario sin password.

#### 2. **OfferServiceTest.java**
Pruebas para gestión de ofertas:
- ✅ Creación de oferta válida.
- ✅ Obtención de oferta más alta por item.
- ✅ Ordenamiento descendente de ofertas.
- ✅ Conteo de ofertas por item.
- ✅ Validación: oferta > precio actual.
- ✅ Validación: oferta = precio es inválida.
- ✅ Validación: oferta < precio es inválida.
- ✅ Formato correcto de precio ($XXX.XX USD).
- ✅ Manejo de lista vacía.
- ✅ Rechazo de montos ≤ 0.

#### 3. **ItemServiceIntegrationTest.java**
Pruebas de integración con H2 (BD en memoria):
- ✅ Insertar item y consultar.
- ✅ Actualizar precio de item.
- ✅ Contar items en tabla.
- ✅ Eliminar item correctamente.

### Ejecución de Pruebas

```bash
# Limpiar y ejecutar tests
mvn clean test

# Generar reporte JaCoCo
mvn verify

# Ver reporte (abrir en navegador)
target/site/jacoco/index.html
```

**Resultado esperado:** 
- Todas las pruebas pasan (verde).
- Cobertura ≥ 70%.

---

## 📊 Problemas Encontrados y Soluciones

| Problema | Causa | Solución |
|----------|-------|----------|
| **Compilación fallaba** | Dependencia `jbcrypt` no se descargaba del repositorio | Reemplazar con SHA-256 nativo de Java |
| **Tabla `users` con tipo incorrecto** | Campo `id` era `integer` en lugar de `varchar` | Detectar automáticamente en `DatabaseManager` y recrear tabla |
| **Rutas de auth no encontradas (404)** | JAR antiguo sin rutas compiladas | Recompilar y reiniciar servidor |
| **Items hardcodeados innecesarios** | Datos duplicados en JSON y BD | Eliminar queries de inserción y usar solo datos de BD |
| **Ofertas sin validación de monto** | Cualquier oferta se aceptaba | Validar que oferta > baseline antes de guardar |

---

## 📈 User Stories

### Sprint 1: Autenticación
```gherkin
Scenario: Registrar nuevo usuario
  Given el usuario accede a /register
  When completa nombre, email y contraseña
  And hace clic en "Crear cuenta"
  Then se crea el usuario en BD
  And se redirige a /items con sesión iniciada

Scenario: Iniciar sesión
  Given el usuario existe en BD
  When accede a /login
  And ingresa email y contraseña correcta
  Then se inicia sesión
  And se redirige a /items

Scenario: Cerrar sesión
  Given el usuario está autenticado
  When hace clic en /logout
  Then se elimina la sesión
  And se redirige a /items sin autenticación
```

### Sprint 2: Ofertas y Validación
```gherkin
Scenario: Crear oferta válida
  Given el usuario ve detalles de item1 ($621.34)
  When completa formulario con oferta $750.00
  And hace clic "Enviar Oferta"
  Then se guarda en tabla offers
  And el precio en BD actualiza a $750.00 USD
  And otros clientes ven el nuevo precio (WebSocket)

Scenario: Rechazar oferta inválida
  Given el precio actual es $621.34
  When intenta crear oferta $500.00
  Then recibe error 400
  And mensaje: "La oferta debe ser mayor que el precio actual (621.34)."
  And la oferta NO se guarda
```

### Sprint 3: Testing y QA
```gherkin
Scenario: Cobertura de código >= 70%
  Given se ejecutan todas las pruebas
  When se genera reporte JaCoCo
  Then cobertura es >= 70%
  And todas las pruebas pasan
```

---

## 🚀 Pipeline de Despliegue a Producción

### Fase 1: Local (Desarrollo)
```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar con BD local
$env:DB_URL='jdbc:postgresql://localhost:5432/auction_store'
$env:DB_USER='postgres'
$env:DB_PASSWORD='12345'
$env:PORT=8080
java -jar target\Reto6-1.0-SNAPSHOT-shaded.jar
```

### Fase 2: Staging (Pre-producción)
- Desplegar JAR en servidor staging.
- Ejecutar pruebas de integración contra BD staging.
- Validar WebSocket y actualización de precios en tiempo real.
- Pruebas de carga (100+ usuarios concurrentes).

### Fase 3: Producción
- Hacer backup de BD: `pg_dump -h prod-host -U user -d auction_store > backup.sql`
- Desplegar JAR en servidor producción.
- Verificar rutas y salud: `curl http://prod:8080/health`
- Monitorear logs: `tail -f /var/log/app/reto6.log`
- Rollback si es necesario: restore desde backup.

### CI/CD (GitHub Actions - ejemplo)
```yaml
name: Build & Test
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build and Test
        run: mvn -B verify
      - name: Upload Coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./target/site/jacoco/jacoco.xml
      - name: Build Artifact
        run: mvn -B package -DskipTests
      - name: Upload JAR
        uses: actions/upload-artifact@v3
        with:
          name: reto6-jar
          path: target/Reto6-*-shaded.jar
```

---

## 📦 Datos en Base de Datos

### Verificar Estado
```sql
-- Contar usuarios registrados
SELECT COUNT(*) as usuarios FROM users;

-- Listar items actuales con precios
SELECT id, name, price FROM items ORDER BY id;

-- Ver ofertas recientes (últimas 10)
SELECT id, item_id, amount, name, email, created_at 
FROM offers 
ORDER BY created_at DESC LIMIT 10;

-- Oferta más alta por item
SELECT item_id, MAX(amount) as max_amount 
FROM offers 
GROUP BY item_id 
ORDER BY max_amount DESC;
```

### Ejemplos de Cambios Persistidos
```sql
-- Cambio 1: Nuevo usuario
INSERT INTO users (id, name, email, password) 
VALUES ('uuid-123', 'Juan Pérez', 'juan@example.com', 'hash...');

-- Cambio 2: Nueva oferta y precio actualizado
INSERT INTO offers (name, email, item_id, amount) 
VALUES ('Pedro', 'pedro@example.com', 'item1', 999.99);
UPDATE items SET price = '$999.99 USD' WHERE id = 'item1';
```

---

## 🔗 Endpoints Principales

| Ruta | Método | Descripción |
|------|--------|-------------|
| `/register` | GET / POST | Registro de usuarios |
| `/login` | GET / POST | Login de usuarios |
| `/logout` | GET | Cerrar sesión |
| `/items` | GET | Lista de items (vista HTML) |
| `/items/:id` | GET | Detalles y formulario de oferta |
| `/api/items` | GET | Items en JSON (con filtros) |
| `/api/offers` | POST | Crear nueva oferta (validada) |
| `/ws/prices` | WebSocket | Actualizaciones de precio en tiempo real |
| `/health` | GET | Health check de la app |

---

## 📝 Cómo Ejecutar Localmente

1. **Clonar/descargar** el proyecto.
2. **Asegurar** que PostgreSQL esté corriendo (`psql -U postgres -c "SELECT 1"`).
3. **Compilar:**
   ```bash
   mvn clean package -DskipTests
   ```
4. **Ejecutar:**
   ```bash
   $env:PORT=8080; java -jar target\Reto6-1.0-SNAPSHOT-shaded.jar
   ```
5. **Acceder:** `http://localhost:8080/items`
6. **Registrarse** y hacer pruebas de ofertas.

---

## 📚 Archivos Relevantes

- `src/main/java/org/example/Main.java` — rutas y handlers HTTP.
- `src/main/java/org/example/service/AuthService.java` — lógica de login/registro.
- `src/main/java/org/example/service/OfferService.java` — gestión de ofertas.
- `src/main/java/org/example/DatabaseManager.java` — inicialización y migración de BD.
- `src/test/java/` — pruebas unitarias e integración.
- `pom.xml` — dependencias y configuración de Maven (JUnit, Mockito, JaCoCo).
- `target/site/jacoco/index.html` — reporte de cobertura (generar con `mvn verify`).

---

## ✅ Checklist Final

- [x] Sistema de login/registro implementado.
- [x] Validación de ofertas (debe ser mayor que baseline).
- [x] WebSocket para actualizaciones en tiempo real.
- [x] Datos persistidos en PostgreSQL (users, items, offers).
- [x] Pruebas unitarias (AuthService, OfferService).
- [x] Pruebas de integración (ItemService con H2).
- [x] Reporte JaCoCo generado (>= 70% cobertura).
- [x] Documentación de user stories.
- [x] Plan de despliegue a producción.

---

**Última actualización:** 2025-11-07  
**Versión:** 1.0-SNAPSHOT  
**Maintainer:** Equipo de Desarrollo

