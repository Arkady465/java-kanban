// src/main/java/server/TaskHandler.java
package server;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import managers.TaskManager;
import task.Task;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // ... ваша существующая логика GET/POST/DELETE для /tasks/task ...
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                // пример: GET /tasks/task?id=1 или GET /tasks/task
                Optional<Integer> idOpt = parseId(exchange.getRequestURI().getQuery());
                if (idOpt.isPresent()) {
                    Task task = manager.getTask(idOpt.get());
                    sendText(exchange, gson.toJson(task), STATUS_OK);
                } else {
                    sendText(exchange, gson.toJson(manager.getTasks()), STATUS_OK);
                }
                break;
            case "POST":
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Task t = gson.fromJson(body, Task.class);
                if (t.getId() == 0) {
                    manager.createTask(t);
                    sendText(exchange, gson.toJson(t), STATUS_CREATED);
                } else {
                    manager.updateTask(t);
                    sendText(exchange, gson.toJson(t), STATUS_OK);
                }
                break;
            case "DELETE":
                Optional<Integer> delId = parseId(exchange.getRequestURI().getQuery());
                if (delId.isPresent()) {
                    manager.deleteTask(delId.get());
                } else {
                    manager.deleteAllTasks();
                }
                sendText(exchange, "", STATUS_OK);
                break;
            default:
                sendText(exchange, "Метод не поддерживается", STATUS_METHOD_NOT_FOUND);
        }
    }
}

