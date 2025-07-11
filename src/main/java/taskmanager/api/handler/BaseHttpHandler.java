package taskmanager.api.handler;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BaseHttpHandler {
    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(201, -1);
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
        exchange.sendResponseHeaders(409, resp.length); // Changed from 406 to 409
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        String msg = message == null ? "" : message.replace("\"", "\\\"");
        String response = "{\"error\":\"Bad Request: " + msg + "\"}";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(400, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, -1);
        exchange.close();
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        String msg = message == null ? "" : message.replace("\"", "\\\"");
        String response = "{\"error\":\"Internal Server Error: " + msg + "\"}";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(500, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected Map<String, String> parseQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return result;
        }
        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            String key = URLDecoder.decode(pair[0], "UTF-8");
            String value = pair.length > 1 ? URLDecoder.decode(pair[1], "UTF-8") : "";
            result.put(key, value);
        }
        return result;
    }

    protected int parseId(String idStr) throws NumberFormatException {
        return Integer.parseInt(idStr);
    }
}