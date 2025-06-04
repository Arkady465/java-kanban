package yandex.ru.yandex.todo.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Базовый хэндлер: содержит методы для отправки ответа:
 *   - sendText          (200 OK + JSON)
 *   - sendNotFound      (404 Not Found)
 *   - sendConflict      (406 Not Acceptable)
 *   - sendServerError   (500 Internal Server Error)
 */
public class BaseHttpHandler {
    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "{\"error\":\"Not Found\"}";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendConflict(HttpExchange exchange) throws IOException {
        String response = "{\"error\":\"Conflict: task times overlap or bad input\"}";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        String escapedMsg = message == null ? "" : message.replace("\"", "\\\"");
        String response = "{\"error\":\"Internal Server Error: " + escapedMsg + "\"}";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(500, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }
}
