package ru.yandex.todo.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.todo.manager.TaskManager;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.manager.ManagerSaveException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
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
                    List<Epic> list = manager.getEpics();
                    sendText(exchange, gson.toJson(list));
                } else {
                    Map<String, String> params = parseQuery(query);
                    if (params.containsKey("id")) {
                        int id = Integer.parseInt(params.get("id"));
                        Epic ep = manager.getEpicById(id);
                        if (ep == null) sendNotFound(exchange);
                        else sendText(exchange, gson.toJson(ep));
                    } else sendNotFound(exchange);
                }
                return;
            }
            if ("POST".equalsIgnoreCase(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic ep = gson.fromJson(body, Epic.class);
                try {
                    if (ep.getId() == 0) manager.createEpic(ep);
                    else manager.updateEpic(ep);
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
                    manager.deleteEpics();
                    sendText(exchange, "{}");
                } else {
                    Map<String, String> params = parseQuery(query);
                    if (params.containsKey("id")) {
                        manager.deleteEpicById(Integer.parseInt(params.get("id")));
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
