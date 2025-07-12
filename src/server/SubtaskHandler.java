// src/main/java/server/SubtaskHandler.java
package server;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import managers.TaskManager;
import task.Subtask;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // ... логика GET/POST/DELETE для /tasks/subtask ...
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                Optional<Integer> idOpt = parseId(exchange.getRequestURI().getQuery());
                if (idOpt.isPresent()) {
                    Subtask sub = (Subtask) manager.getSubtask(idOpt.get());
                    sendText(exchange, gson.toJson(sub), STATUS_OK);
                } else {
                    sendText(exchange, gson.toJson(manager.getSubtasks()), STATUS_OK);
                }
                break;
            case "POST":
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Subtask s = gson.fromJson(body, Subtask.class);
                if (s.getId() == 0) {
                    manager.createSubtask(s);
                    sendText(exchange, gson.toJson(s), STATUS_CREATED);
                } else {
                    manager.updateSubtask(s);
                    sendText(exchange, gson.toJson(s), STATUS_OK);
                }
                break;
            case "DELETE":
                Optional<Integer> delId = parseId(exchange.getRequestURI().getQuery());
                if (delId.isPresent()) {
                    manager.deleteSubtask(delId.get());
                } else {
                    manager.deleteAllSubtasks();
                }
                sendText(exchange, "", STATUS_OK);
                break;
            default:
                sendText(exchange, "Метод не поддерживается", STATUS_METHOD_NOT_FOUND);
        }
    }
}

