package ru_yandex_todo.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.todo.manager.ManagerSaveException;
import ru_yandex_todo.manager.TaskManager;
import ru_yandex_todo.model.Epic;
import ru_yandex_todo.model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Обработчик для эндпоинтов:
 *   GET    /epics                  → manager.getEpics()
 *   GET    /epics/{id}             → manager.getEpicById(id)
 *   GET    /epics/{id}/subtasks    → manager.getEpicSubtasks(id)
 *   POST   /epics                  → manager.createEpic(...) или manager.updateEpic(...)
 *   DELETE /epics/{id}             → manager.deleteEpic(id)
 */
public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            // Возможные варианты:
            //   "/epics"
            //   "/epics/5"
            //   "/epics/5/subtasks"
            String[] parts = path.split("/");

            // ===== 1) GET /epics =====
            if ("GET".equalsIgnoreCase(method) && parts.length == 2 && parts[1].equals("epics")) {
                List<Epic> epics = manager.getEpics();
                String json = gson.toJson(epics);
                sendText(exchange, json);
                return;
            }

            // ===== 2) GET /epics/{id} =====
            if ("GET".equalsIgnoreCase(method) && parts.length == 3 && parts[1].equals("epics")) {
                try {
                    int id = Integer.parseInt(parts[2]);
                    Epic epic = manager.getEpicById(id);
                    if (epic == null) {
                        sendNotFound(exchange);
                    } else {
                        String json = gson.toJson(epic);
                        sendText(exchange, json);
                    }
                } catch (NumberFormatException e) {
                    sendNotFound(exchange);
                }
                return;
            }

            // ===== 3) GET /epics/{id}/subtasks =====
            if ("GET".equalsIgnoreCase(method) && parts.length == 4
                    && parts[1].equals("epics") && parts[3].equals("subtasks")) {
                try {
                    int epicId = Integer.parseInt(parts[2]);
                    List<Subtask> subtasks = manager.getEpicSubtasks(epicId);
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

            // ===== 4) POST /epics =====
            if ("POST".equalsIgnoreCase(method) && parts.length == 2 && parts[1].equals("epics")) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Epic epicFromJson = gson.fromJson(body, Epic.class);
                try {
                    if (epicFromJson.getId() == 0) {
                        manager.createEpic(epicFromJson);
                    } else {
                        manager.updateEpic(epicFromJson);
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

            // ===== 5) DELETE /epics/{id} =====
            if ("DELETE".equalsIgnoreCase(method) && parts.length == 3 && parts[1].equals("epics")) {
                try {
                    int id = Integer.parseInt(parts[2]);
                    manager.deleteEpic(id);
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
