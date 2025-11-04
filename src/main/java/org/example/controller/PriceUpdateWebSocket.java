package org.example.controller;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class PriceUpdateWebSocket {
    private static final Logger logger = LoggerFactory.getLogger(PriceUpdateWebSocket.class);
    private static final Gson gson = new Gson();
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        sessions.add(session);
        logger.info("🔌 Cliente WebSocket conectado. Total: {}", sessions.size());

        try {
            // Enviar mensaje de bienvenida
            Map<String, Object> welcome = Map.of(
                "type", "connected",
                "message", "Conectado al servidor de actualizaciones de precios",
                "timestamp", System.currentTimeMillis()
            );
            session.getRemote().sendString(gson.toJson(welcome));
        } catch (Exception e) {
            logger.error("Error enviando mensaje de bienvenida", e);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessions.remove(session);
        logger.info("🔌 Cliente WebSocket desconectado. Total: {}. Razón: {}", sessions.size(), reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        logger.error("❌ Error en WebSocket", error);
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        logger.debug("📩 Mensaje recibido: {}", message);
        try {
            Map<String, Object> data = gson.fromJson(message, new TypeToken<Map<String, Object>>(){}.getType());
            if ("price_update".equals(data.get("type"))) {
                broadcastPriceUpdate(data);
            }
        } catch (Exception e) {
            logger.error("Error procesando mensaje WebSocket", e);
        }
    }

    /**
     * Envía actualización de precio para un item específico
     */
    public static void notifyPriceChange(String itemId, String newPrice) {
        if (sessions.isEmpty()) {
            logger.warn("No hay clientes WebSocket conectados para notificar");
            return;
        }

        Map<String, Object> update = Map.of(
            "type", "price_update",
            "itemId", itemId,
            "newPrice", newPrice,
            "timestamp", System.currentTimeMillis()
        );

        String json = gson.toJson(update);
        logger.info("📢 Enviando actualización de precio: {} a {} clientes", json, sessions.size());

        sessions.removeIf(session -> {
            if (session.isOpen()) {
                try {
                    session.getRemote().sendString(json);
                    return false; // mantener en la lista
                } catch (IOException e) {
                    logger.error("Error enviando actualización a cliente", e);
                    return true; // remover de la lista
                }
            }
            return true; // remover sesiones cerradas
        });
    }

    private static void broadcastPriceUpdate(Map<String, Object> update) {
        String json = gson.toJson(update);
        sessions.removeIf(session -> {
            if (session.isOpen()) {
                try {
                    session.getRemote().sendString(json);
                    return false;
                } catch (IOException e) {
                    logger.error("Error enviando broadcast", e);
                    return true;
                }
            }
            return true;
        });
    }
}