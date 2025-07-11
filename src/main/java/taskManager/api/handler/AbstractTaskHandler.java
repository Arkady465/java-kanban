package taskManager.api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskManager.exception.ManagerSaveException;
import taskManager.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class AbstractTaskHandler<T> extends BaseHttpHandler implements HttpHandler {
    protected final TaskManager manager;
    protected final Gson gson;
    protected final Class<T> type;

    public AbstractTaskHandler(TaskManager manager, Gson gson, Class<T> type) {
        this.manager = manager;
        this.gson = gson;
        this.type = type;
    }

    protected void handleGetRequest(HttpExchange exchange, String query) throws IOException {
        try {
            if (query == null) {
                handleGetAll(exchange);
            } else {
                Map<String, String> params = parseQuery(query);
                if (params.containsKey("id")) {
                    int id = parseId(params.get("id"));
                    handleGetById(exchange, id);
                } else {
                    sendNotFound(exchange);
                }
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid ID format");
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }

    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            T task = gson.fromJson(body, type);
            handleTaskOperation(exchange, task);
        } catch (IllegalArgumentException e) {
            sendConflict(exchange);
        } catch (ManagerSaveException e) {
            sendServerError(exchange, e.getMessage());
        } catch (Exception e) {
            sendBadRequest(exchange, "Invalid task data");
        }
    }

    protected void handleDeleteRequest(HttpExchange exchange, String query) throws IOException {
        try {
            if (query == null) {
                handleDeleteAll(exchange);
            } else {
                Map<String, String> params = parseQuery(query);
                if (params.containsKey("id")) {
                    int id = parseId(params.get("id"));
                    handleDeleteById(exchange, id);
                } else {
                    sendNotFound(exchange);
                }
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid ID format");
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }

    protected abstract void handleGetAll(HttpExchange exchange) throws IOException;
    protected abstract void handleGetById(HttpExchange exchange, int id) throws IOException;
    protected abstract void handleTaskOperation(HttpExchange exchange, T task) throws IOException;
    protected abstract void handleDeleteAll(HttpExchange exchange) throws IOException;
    protected abstract void handleDeleteById(HttpExchange exchange, int id) throws IOException;
}