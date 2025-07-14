package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    // Константы статусов
    protected static final int STATUS_OK = 200;
    protected static final int STATUS_CREATED = 201;
    protected static final int STATUS_BAD_REQUEST = 400;
    protected static final int STATUS_TASK_NOT_FOUND = 404;
    protected static final int STATUS_METHOD_NOT_FOUND = 405;
    protected static final int STATUS_CONFLICT = 409;
    protected static final int STATUS_INTERNAL_ERROR = 500;

    // Константы для заголовков
    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String APPLICATION_JSON = "application/json;charset=utf-8";

    // Единый экземпляр Gson для всех обработчиков
    protected static final Gson gson = createGson();

    // Статический метод для инициализации Gson
    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    protected void sendText(HttpExchange exchange, String text, int responseCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON);
        exchange.sendResponseHeaders(responseCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected static Optional<Integer> parseId(String query) {
        if (query == null || query.isEmpty()) {
            return Optional.empty();
        }

        try {
            // Обрабатываем параметры вида "id=123"
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("id=")) {
                    String idValue = param.split("=")[1];
                    return Optional.of(Integer.parseInt(idValue));
                }
            }
            return Optional.empty();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }
}