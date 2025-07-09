package ru.yandex.todo.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.todo.manager.TaskManager;
import ru.yandex.todo.model.Task;
import todo.manager.ManagerSaveException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        try {
            if ("GET".equalsIgnoreCase(method)) {
                if (query == null) {
                    List<Task> tasks = manager.getTasks();
                    sendText(exchange, gson.toJson(tasks));
                } else {
                    Map<String, String> params = parseQuery(query);
                    if (params.containsKey("id")) {
                        int id = Integer.parseInt(params.get("id"));
                        Task task = manager.getTaskById(id);
                        if (task == null) {
                            sendNotFound(exchange);
                        } else {
                            sendText(exchange, gson.toJson(task));
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                }
                return;
            }

            if ("POST".equalsIgnoreCase(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);
                try {
                    if (task.getId() == 0) {
                        manager.createTask(task);
                    } else {
                        manager.updateTask(task);
                    }
                    sendCreated(exchange);
                } catch (IllegalArgumentException e) {
                    sendConflict(exchange);
                } catch (ManagerSaveException e) {
                    sendServerError(exchange, e.getMessage());
                }
                return;
            }

            if ("DELETE".equalsIgnoreCase(method)) {
                if (query == null) {
                    manager.deleteTasks();
                    sendText(exchange, "{}");
                } else {
                    Map<String, String> params = parseQuery(query);
                    if (params.containsKey("id")) {
                        int id = Integer.parseInt(params.get("id"));
                        manager.deleteTaskById(id);
                        sendText(exchange, "{}");
                    } else {
                        sendNotFound(exchange);
                    }
                }
                return;
            }

            sendServerError(exchange, "Unsupported method: " + method);
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }
}
