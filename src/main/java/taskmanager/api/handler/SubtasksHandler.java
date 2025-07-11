package taskmanager.api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.model.Subtask;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends AbstractTaskHandler<Subtask> {

    public SubtasksHandler(TaskManager manager, Gson gson) {
        super(manager, gson, Subtask.class);
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
        List<Subtask> subtasks = manager.getAllSubtasks();
        sendText(exchange, gson.toJson(subtasks));
    }

    @Override
    protected void handleGetById(HttpExchange exchange, int id) throws IOException {
        Subtask subtask = manager.getSubtask(id);
        if (subtask == null) {
            sendNotFound(exchange);
        } else {
            sendText(exchange, gson.toJson(subtask));
        }
    }

    @Override
    protected void handleTaskOperation(HttpExchange exchange, Subtask subtask) throws IOException {
        if (subtask.getId() == 0) {
            manager.addSubtask(subtask);
        } else {
            manager.updateSubtask(subtask);
        }
        sendCreated(exchange);
    }

    @Override
    protected void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.clearAllSubtasks();
        sendText(exchange, "{}");
    }

    @Override
    protected void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        manager.deleteSubtask(id);
        sendText(exchange, "{}");
    }
}