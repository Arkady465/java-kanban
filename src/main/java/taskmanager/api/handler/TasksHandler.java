package taskmanager.api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.model.Task;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends AbstractTaskHandler<Task> {

    public TasksHandler(TaskManager manager, Gson gson) {
        super(manager, gson, Task.class);
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
        List<Task> tasks = manager.getAllTasks();
        sendText(exchange, gson.toJson(tasks));
    }

    @Override
    protected void handleGetById(HttpExchange exchange, int id) throws IOException {
        Task task = manager.getTask(id);
        if (task == null) {
            sendNotFound(exchange);
        } else {
            sendText(exchange, gson.toJson(task));
        }
    }

    @Override
    protected void handleTaskOperation(HttpExchange exchange, Task task) throws IOException {
        if (task.getId() == 0) {
            manager.addTask(task);
        } else {
            manager.updateTask(task);
        }
        sendCreated(exchange);
    }

    @Override
    protected void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.clearAllTasks();
        sendText(exchange, "{}");
    }

    @Override
    protected void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        manager.deleteTask(id);
        sendText(exchange, "{}");
    }
}