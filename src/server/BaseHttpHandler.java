package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import managers.Manager;


public abstract class BaseHttpHandler implements HttpHandler {
    protected static final int STATUS_OK               = 200;
    protected static final int STATUS_CREATED          = 201;
    protected static final int STATUS_NOT_FOUND        = 404;
    protected static final int STATUS_METHOD_NOT_FOUND = 405;
    protected static final int STATUS_CONFLICT         = 406;
    protected static final int STATUS_BAD_REQUEST      = 500;

    protected static final Gson gson = Manager.createGson();

   
    protected void sendText(HttpExchange exchange,
                            String text,
                            int responseCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add(
            Manager.HEADER_CONTENT_TYPE,
            Manager.MIME_JSON_UTF8
        );
        exchange.sendResponseHeaders(responseCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
        exchange.close();
    }

    protected static Optional<Integer> parseId(String query) {
        try {
            if (query == null || !query.startsWith("id=")) {
                return Optional.empty();
            }
            return Optional.of(Integer.parseInt(query.substring(3)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
