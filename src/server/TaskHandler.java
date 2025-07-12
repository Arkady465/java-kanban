package server;

import com.sun.net.httpserver.HttpExchange;
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
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET": {
                Optional<Integer> idOpt = parseId(exchange.getRequestURI().getQuery());
                if (idOpt.isPresent()) {
                    Task task = manager.getTaskById(idOpt.get());
                    sendText(exchange, gson.toJson(task), STATUS_OK);
                } else {
                    sendText(exchange, gson.toJson(manager.getAllTasks()), STATUS_OK);
                }
                break;
            }
            case "POST": {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Task t = gson.fromJson(body, Task.class);
                if (t.getId() == 0) {
                    manager.addTask(t);
                    sendText(exchange, gson.toJson(t), STATUS_CREATED);
                } else {
                    Task old = manager.getTaskById(t.getId());
                    manager.updateTask(old, t);
                    sendText(exchange, gson.toJson(t), STATUS_OK);
                }
                break;
            }
            case "DELETE": {
                Optional<Integer> delId = parseId(exchange.getRequestURI().getQuery());
                if (delId.isPresent()) {
                    Task old = manager.getTaskById(delId.get());
                    manager.deleteTaskById(old);
                } else {
                    manager.deleteAllTasks();
                }
                sendText(exchange, "", STATUS_OK);
                break;
            }
            default:
                sendText(exchange, "Метод не поддерживается", STATUS_METHOD_NOT_FOUND);
        }
    }
}
