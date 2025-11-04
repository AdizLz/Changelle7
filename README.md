Reto6 - Auction Store (Signed Memorabilia)

Overview
--------
This project is a small online auction store for celebrity-autographed items. It provides a REST API, server-side rendered pages (Mustache), and real-time price updates via WebSocket.

Key features implemented (Sprint 3)
----------------------------------
- REST API for users, items and offers
- Filters for items (API + UI) using query parameters: `q`, `minPrice`, `maxPrice`
- PATCH endpoint to update an item price: `PATCH /api/items/:id/price`
- WebSocket server for real-time price updates at `/ws/prices` (broadcasts `price_update` messages)
- Frontend templates updated to receive real-time updates and to apply filters

Tech stack
----------
- Java 17
- Spark Java (web framework)
- Mustache templates
- Gson (JSON)
- HikariCP + PostgreSQL for persistence
- Maven for build

Build and run (Windows, cmd.exe)
--------------------------------
1. Build the project:

```bat
cd C:\Users\Soporte\Downloads\Reto6
mvn -DskipTests package
```

2. Configure database (only if PostgreSQL credentials differ from defaults). By default the app uses:
- JDBC URL: jdbc:postgresql://localhost:5432/auction_store
- DB user: postgres
- DB password: 12345

If you need to override, set environment variables before running:

```bat
set DB_URL=jdbc:postgresql://<host>:5432/auction_store
set DB_USER=<your_db_user>
set DB_PASSWORD=<your_db_password>
set PORT=55603   REM optional, default is 55603
```

3. Run the application (foreground):

```bat
java -jar target\Reto6-1.0-SNAPSHOT-shaded.jar
```

Or run in background and save logs:

```bat
start "Reto6" /B cmd /c "java -jar target\Reto6-1.0-SNAPSHOT-shaded.jar > server.log 2>&1"
type server.log | more
```

If the server cannot connect to the database it will log the error and exit. Make sure PostgreSQL is running and the `auction_store` database exists (or use env vars above to point to a valid DB).

Database quick-create (psql)
----------------------------
If you need to create the DB and a user (run as a superuser):

```sql
CREATE DATABASE auction_store;
CREATE USER myuser WITH PASSWORD 'mypassword';
GRANT ALL PRIVILEGES ON DATABASE auction_store TO myuser;
```

Then set `DB_USER=myuser` and `DB_PASSWORD=mypassword` before running.

API Endpoints (summary)
-----------------------
Users:
- GET /users
- GET /users/:id
- POST /users/:id
- PUT /users/:id
- DELETE /users/:id
- OPTIONS /users/:id

Items (JSON API):
- GET /api/items             -> supports query params `q`, `minPrice`, `maxPrice`
- GET /api/items/:id
- POST /api/items
- PUT /api/items/:id
- DELETE /api/items/:id
- PATCH /api/items/:id/price -> update price and broadcast WebSocket `price_update`

Offers:
- POST /api/offers
- GET /api/offers
- GET /api/offers/item/:itemId

WebSocket
---------
- Endpoint: ws://<host>/ws/prices  (or wss:// when using HTTPS)
- Message produced by server when price changes:
  {
    "type": "price_update",
    "itemId": "item1",
    "newPrice": "$700.00 USD",
    "timestamp": 1234567890
  }

Frontend
--------
- Item list page: http://localhost:55603/items - supports filters in the UI and via query params
- Item detail page: http://localhost:55603/items/:id - updates price in real-time via WebSocket

Testing examples (cmd.exe)
--------------------------
- List items:
```bat
curl http://localhost:55603/api/items
```

- Filter items:
```bat
curl "http://localhost:55603/api/items?minPrice=400&maxPrice=700&q=Guitarra"
```

- Update an item price (PATCH):
```bat
curl -X PATCH -H "Content-Type: application/json" -d "{\"price\":\"$700.00 USD\"}" http://localhost:55603/api/items/item1/price
```

- Health check:
```bat
curl http://localhost:55603/health
```

Troubleshooting
---------------
- If the app exits at startup, check `server.log` or console output for DB connection errors.
- Ensure PostgreSQL is running and credentials/DB name match or set env vars.
- For WebSocket issues, ensure you're not running behind a proxy that blocks WS and use the correct `ws` vs `wss` protocol.

Sprint 3 compliance
-------------------
This codebase includes the Sprint 3 deliverables:
- Filters implemented in the API and UI
- WebSocket server and client logic for real-time price updates
- PATCH endpoint that updates DB and notifies connected clients

Commit & push to GitHub (suggested steps)
-----------------------------------------
```bat
cd C:\Users\Soporte\Downloads\Reto6
git add .
git commit -m "Sprint 3: filters + websocket real-time price updates + README"
git push origin main
```

If you want, I can generate the README commit locally and (with your guidance) help you push to GitHub.

Contact / Next steps
--------------------
If you want I can:
- run the app now and show the startup logs, or
- prepare and run a small test script that exercises filters and price updates automatically, or
- create the Git commit and push (if you're prepared to give repo details).


---
README generated on 2025-11-03

