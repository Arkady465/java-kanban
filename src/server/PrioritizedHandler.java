package server;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            sendText(exchange, gson.toJson(manager.getPrioritizedTasks()), STATUS_OK);
        } else {
            sendText(exchange, "Метод не поддерживается", STATUS_METHOD_NOT_FOUND);
        }
    }
}
