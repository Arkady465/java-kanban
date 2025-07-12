package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final int STATUS_METHOD_NOT_FOUND = 405;
    protected static final int STATUS_CONFLICT          = 406;
    protected static final int STATUS_BAD_REQUEST       = 500;

    /** Общий Gson, сконфигурированный в Managers */
    protected static final Gson gson = Managers.createGson();

    /**
     * Отправляет JSON-ответ
     */
    protected void sendText(HttpExchange exchange, String text, int responseCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add(
            Managers.HEADER_CONTENT_TYPE,
            Managers.MIME_JSON_UTF8
        );
        exchange.sendResponseHeaders(responseCode, resp.length);
        exchange.getResponseBody().write(resp);
    }
}
