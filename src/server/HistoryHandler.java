package server;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            if ("GET".equals(method)) {
                String json = gson.toJson(manager.getHistory());
                sendText(exchange, json, STATUS_OK);
            } else {
                sendText(exchange, "Метод не поддерживается", STATUS_METHOD_NOT_FOUND);
            }
        } catch (Exception e) {
            sendText(exchange, "Ошибка при обработке запроса", STATUS_INTERNAL_ERROR);
        }
    }
}