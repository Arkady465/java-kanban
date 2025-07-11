package taskManager.api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskManager.model.Task;
import taskManager.service.TaskManager;

import java.io.IOException;
import java.util.List;

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
            // Only GET is allowed
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendServerError(exchange, "Unsupported method: " + exchange.getRequestMethod());
                return;
            }
            // Retrieve tasks in priority order
            List<Task> prioritized = manager.getPrioritizedTasks();
            sendText(exchange, gson.toJson(prioritized));
        } catch (Exception e) {
            // Any unexpected error
            sendServerError(exchange, e.getMessage());
        }
    }
}
