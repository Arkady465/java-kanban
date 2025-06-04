package ru.yandex.todo.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import todo.manager.ManagerSaveException;
import ru.yandex.todo.manager.TaskManagers;
import ru.yandex.todo.model.Subtasks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Обработчик для эндпоинтов:
 *   GET    /subtasks         → manager.getSubtasks()
 *   GET    /subtasks/{id}    → manager.getSubtaskById(id)
 *   POST   /subtasks         → manager.createSubtask(...) или manager.updateSubtask(...)
 *   DELETE /subtasks/{id}    → manager.deleteSubtask(id)
 */
public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManagers manager;
    private final Gson gson;

    public SubtasksHandler(TaskManagers manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath(); // "/subtasks" или "/subtasks/7"
            String[] parts = path.split("/");

            // ===== 1) GET /subtasks =====
            if ("GET".equalsIgnoreCase(method) && parts.length == 2 && parts[1].equals("subtasks")) {
                List<Subtasks> subtasks = manager.getSubtasks();
                String json = gson.toJson(subtasks);
                sendText(exchange, json);
                return;
            }

            // ===== 2) GET /subtasks/{id} =====
            if ("GET".equalsIgnoreCase(method) && parts.length == 3 && parts[1].equals("subtasks")) {
                try {
                    int id = Integer.parseInt(parts[2]);
                    Subtasks subtasks = manager.getSubtaskById(id);
                    if (subtasks == null) {
                        sendNotFound(exchange);
                    } else {
                        String json = gson.toJson(subtasks);
                        sendText(exchange, json);
                    }
                } catch (NumberFormatException e) {
                    sendNotFound(exchange);
                }
                return;
            }

            // ===== 3) POST /subtasks =====
            if ("POST".equalsIgnoreCase(method) && parts.length == 2 && parts[1].equals("subtasks")) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Subtasks subtasksFromJson = gson.fromJson(body, Subtasks.class);
                try {
                    if (subtasksFromJson.getId() == 0) {
                        manager.createSubtask(subtasksFromJson);
                    } else {
                        manager.updateSubtask(subtasksFromJson);
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

            // ===== 4) DELETE /subtasks/{id} =====
            if ("DELETE".equalsIgnoreCase(method) && parts.length == 3 && parts[1].equals("subtasks")) {
                try {
                    int id = Integer.parseInt(parts[2]);
                    manager.deleteSubtask(id);
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
