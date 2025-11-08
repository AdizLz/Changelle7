# 🎯 Challenge6 Project - Auction System with Authentication and Real-Time Offers

##  General Description

Web auction/store application developed in **Java** (Spark, PostgreSQL) with user authentication system, item management and real-time offers via WebSockets.

**Technical Stack:**
- Backend: Java 17, Spark (web framework)
- Database: PostgreSQL (users, items, offers)
- Frontend: Mustache (HTML templates)
- Testing: JUnit 5, Mockito, JaCoCo (coverage)
- CI/CD: Maven

---

##  New Features Implemented

### 1. Login/Registration System
- **Functionality:** Create new account and authenticate.
- **Location:** `/register`, `/login`, `/logout`
- **Hashing:** SHA-256 + Base64 (no external dependencies like BCrypt).
- **Sessions:** Managed by Spark (secure session cookies).

### 2. Offer Validation
- **Rule:** A new offer must be **strictly greater** than the current item price (or highest existing bid).
- **Validation:** In the POST handler `/api/offers` in `Main.java`.
- **Rejection:** If offer is ≤ baseline, returns HTTP 400 with error message.

### 3. Real-Time Updates
- **WebSocket:** `/ws/prices` for price change notifications.
- **Client:** `script.js` opens WebSocket connection and updates UI automatically.
- **Server:** `PriceUpdateWebSocket.java` sends messages to all connected clients.

### 4. Database Persistence
- **Table `users`:** ID (UUID), name, email, hashed password, timestamp.
- **Table `items`:** ID, name, description, price (updates with each offer).
- **Table `offers`:** Auto-generated ID, offer data, item_id (FK), amount, timestamp.

---

##  Unit Tests

### Code Coverage
- **Goal:** ≥ 70% coverage (configured in `pom.xml`).
- **Tool:** JaCoCo (report in `target/site/jacoco/index.html`).

### Tested Classes

#### 1. **AuthServiceTest.java**
Tests for login/registration:
- ✅ Successful registration with unique data validation.
- ✅ Rejection of duplicate ID.
- ✅ Rejection of duplicate email.
- ✅ Login with valid credentials.
- ✅ Rejection of non-existent email.
- ✅ Rejection of incorrect password.
- ✅ Handling of user without password.

#### 2. **OfferServiceTest.java**
Tests for offer management:
- ✅ Creation of valid offer.
- ✅ Getting highest offer per item.
- ✅ Descending order sorting of offers.
- ✅ Count of offers per item.
- ✅ Validation: offer > current price.
- ✅ Validation: offer = price is invalid.
- ✅ Validation: offer < price is invalid.
- ✅ Correct price format ($XXX.XX USD).
- ✅ Handling of empty list.
- ✅ Rejection of amounts ≤ 0.

#### 3. **ItemServiceIntegrationTest.java**
Integration tests (in-memory database):
- ✅ Insert item and query.
- ✅ Update item price.
- ✅ Count items in table.
- ✅ Delete item correctly.

### Running Tests

```bash
# Clean and run tests
mvn clean test

# Generate JaCoCo report
mvn verify

# View report (open in browser)
target/site/jacoco/index.html
```

**Expected Result:** 
- All tests pass (green).
- Coverage ≥ 70%.

---

## Issues Found and Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| **Compilation failed** | `jbcrypt` dependency not downloading from repository | Replace with native Java SHA-256 |
| **Table `users` with incorrect type** | `id` field was `integer` instead of `varchar` | Auto-detect in `DatabaseManager` and recreate table |
| **Auth routes not found (404)** | Old JAR without compiled routes | Recompile and restart server |
| **Unnecessary hardcoded items** | Duplicate data in JSON and database | Remove insert queries and use only database data |
| **Offers without amount validation** | Any offer was accepted | Validate offer > baseline before saving |

---

##  Deployment Pipeline to Production

### Phase 1: Local (Development)
```bash
# Compile
mvn clean package -DskipTests

# Run with local database
$env:DB_URL='jdbc:postgresql://localhost:5432/auction_store'
$env:DB_USER='postgres'
$env:DB_PASSWORD='12345'
$env:PORT=8080
java -jar target\Reto6-1.0-SNAPSHOT-shaded.jar
```

### Phase 2: Staging (Pre-Production)
- Deploy JAR on staging server.
- Run integration tests against staging database.
- Validate WebSocket and real-time price updates.
- Load testing (100+ concurrent users).

### Phase 3: Production
- Backup database: `pg_dump -h prod-host -U user -d auction_store > backup.sql`
- Deploy JAR on production server.
- Verify routes and health: `curl http://prod:8080/health`
- Monitor logs: `tail -f /var/log/app/reto6.log`
- Rollback if necessary: restore from backup.

### CI/CD (GitHub Actions - example)
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
          distribution: 'adopt'
      - name: Run tests
        run: mvn clean test
      - name: Generate coverage report
        run: mvn jacoco:report
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
```

---

## 📝 How to Contribute

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Make changes and run tests: `mvn clean verify`
4. Commit: `git commit -am 'Add new feature'`
5. Push: `git push origin feature/new-feature`
6. Create Pull Request

---

**Last Updated:** 2025-11-07  
**Version:** 1.0-SNAPSHOT
