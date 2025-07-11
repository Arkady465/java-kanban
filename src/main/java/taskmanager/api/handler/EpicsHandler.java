package taskmanager.api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.model.Epic;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends AbstractTaskHandler<Epic> {

    public EpicsHandler(TaskManager manager, Gson gson) {
        super(manager, gson, Epic.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        switch (method.toUpperCase()) {
            case "GET":
                handleGetRequest(exchange, query);
                break;
            case "POST":
                handlePostRequest(exchange);
                break;
            case "DELETE":
                handleDeleteRequest(exchange, query);
                break;
            default:
                sendMethodNotAllowed(exchange);
        }
    }

    @Override
    protected void handleGetAll(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getAllEpics();
        sendText(exchange, gson.toJson(epics));
    }

    @Override
    protected void handleGetById(HttpExchange exchange, int id) throws IOException {
        Epic epic = manager.getEpic(id);
        if (epic == null) {
            sendNotFound(exchange);
        } else {
            sendText(exchange, gson.toJson(epic));
        }
    }

    @Override
    protected void handleTaskOperation(HttpExchange exchange, Epic epic) throws IOException {
        if (epic.getId() == 0) {
            manager.addEpic(epic);
        } else {
            manager.updateEpic(epic);
        }
        sendCreated(exchange);
    }

    @Override
    protected void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.clearAllEpics();
        sendText(exchange, "{}");
    }

    @Override
    protected void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        manager.deleteEpic(id);
        sendText(exchange, "{}");
    }
}