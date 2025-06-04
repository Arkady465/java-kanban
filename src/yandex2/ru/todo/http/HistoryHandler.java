package yandex.ru.yandex.todo.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import yandex.ru.yandex.todo.manager.TaskManager;
import yandex.ru.yandex.todo.model.Task;

import java.io.IOException;
import java.util.List;

/**
 * Обработчик для эндпоинта:
 *   GET /history   → manager.getHistory()
 */
public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath(); // "/history"
            String[] parts = path.split("/");

            if ("GET".equalsIgnoreCase(method) && parts.length == 2 && parts[1].equals("history")) {
                List<Task> history = manager.getHistory();
                String json = gson.toJson(history);
                sendText(exchange, json);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }
}
