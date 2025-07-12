package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            sendText(exchange, gson.toJson(manager.getPrioritizedTasks()), STATUS_OK);
        } else {
            sendText(exchange, "Метод не поддерживается", STATUS_METHOD_NOT_FOUND);
        }
        exchange.close();
    }
}
