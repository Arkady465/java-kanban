package ru.yandex.todo.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.todo.manager.TaskManager;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.manager.ManagerSaveException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager, Gson gson) {
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
                    List<Subtask> list = manager.getSubtasks();
                    sendText(exchange, gson.toJson(list));
                } else {
                    Map<String, String> params = parseQuery(query);
                    if (params.containsKey("id")) {
                        int id = Integer.parseInt(params.get("id"));
                        Subtask st = manager.getSubtaskById(id);
                        if (st == null) sendNotFound(exchange);
                        else sendText(exchange, gson.toJson(st));
                    } else sendNotFound(exchange);
                }
                return;
            }
            if ("POST".equalsIgnoreCase(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Subtask st = gson.fromJson(body, Subtask.class);
                try {
                    if (st.getId() == 0) manager.createSubtask(st);
                    else manager.updateSubtask(st);
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
                    manager.deleteSubtasks();
                    sendText(exchange, "{}");
                } else {
                    Map<String, String> params = parseQuery(query);
                    if (params.containsKey("id")) {
                        manager.deleteSubtaskById(Integer.parseInt(params.get("id")));
                        sendText(exchange, "{}");
                    } else sendNotFound(exchange);
                }
                return;
            }
            sendServerError(exchange, "Unsupported method: " + method);
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }
}
