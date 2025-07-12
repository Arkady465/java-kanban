// src/main/java/server/HistoryHandler.java
package server;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equals(method)) {
            // используем унаследованный gson
            sendText(exchange, gson.toJson(manager.getHistory()), STATUS_OK);
        } else {
            sendText(exchange, "Метод не поддерживается", STATUS_METHOD_NOT_FOUND);
        }
    }
}
