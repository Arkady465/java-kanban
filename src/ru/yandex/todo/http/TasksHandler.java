package ru.yandex.todo.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import todo.manager.ManagerSaveException;
import ru.yandex.todo.manager.TaskManager;
import ru.yandex.todo.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Обработчик для эндпоинтов:
 *   GET    /tasks         → manager.getTasks()
 *   GET    /tasks/{id}    → manager.getTaskById(id)
 *   POST   /tasks         → manager.createTask(...) или manager.updateTask(...)
 *   DELETE /tasks/{id}    → manager.deleteTask(id)
 */
public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath(); // "/tasks" или "/tasks/5"
            String[] parts = path.split("/");

            // ===== 1) GET /tasks =====
            if ("GET".equalsIgnoreCase(method) && parts.length == 2 && parts[1].equals("tasks")) {
                List<Task> tasks = manager.getTasks();
                String json = gson.toJson(tasks);
                sendText(exchange, json);
                return;
            }

            // ===== 2) GET /tasks/{id} =====
            if ("GET".equalsIgnoreCase(method) && parts.length == 3 && parts[1].equals("tasks")) {
                try {
                    int id = Integer.parseInt(parts[2]);
                    Task task = manager.getTaskById(id);
                    if (task == null) {
                        sendNotFound(exchange);
                    } else {
                        String json = gson.toJson(task);
                        sendText(exchange, json);
                    }
                } catch (NumberFormatException e) {
                    sendNotFound(exchange);
                }
                return;
            }

            // ===== 3) POST /tasks =====
            if ("POST".equalsIgnoreCase(method) && parts.length == 2 && parts[1].equals("tasks")) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Task taskFromJson = gson.fromJson(body, Task.class);
                try {
                    if (taskFromJson.getId() == 0) {
                        manager.createTask(taskFromJson);
                    } else {
                        manager.updateTask(taskFromJson);
                    }
                    String resp = "{\"result\":\"ok\"}";
                    sendText(exchange, resp);
                } catch (ManagerSaveException | IllegalArgumentException conflict) {
                    sendConflict(exchange);
                } catch (Exception e) {
                    sendServerError(exchange, e.getMessage());
                }
                return;
            }

            // ===== 4) DELETE /tasks/{id} =====
            if ("DELETE".equalsIgnoreCase(method) && parts.length == 3 && parts[1].equals("tasks")) {
                try {
                    int id = Integer.parseInt(parts[2]);
                    manager.deleteTask(id);
                    String resp = "{\"result\":\"deleted\"}";
                    sendText(exchange, resp);
                } catch (NumberFormatException e) {
                    sendNotFound(exchange);
                } catch (Exception e) {
                    sendServerError(exchange, e.getMessage());
                }
                return;
            }

            // Всё остальное → 404
            sendNotFound(exchange);
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }
}
