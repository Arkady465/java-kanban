package yandex.ru.yandex.todo.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import yandex.ru.yandex.todo.manager.TaskManager;
import yandex.ru.yandex.todo.model.Task;

import java.io.IOException;
import java.util.Set;

/**
 * Обработчик для эндпоинта:
 *   GET /prioritized   → manager.getPrioritizedTasks()
 */
public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath(); // "/prioritized"
            String[] parts = path.split("/");

            if ("GET".equalsIgnoreCase(method) && parts.length == 2 && parts[1].equals("prioritized")) {
                Set<Task> prioritizedTasks = manager.getPrioritizedTasks();
                String json = gson.toJson(prioritizedTasks);
                sendText(exchange, json);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }
}
