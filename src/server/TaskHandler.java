package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import managers.TaskManager;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        try {
            switch (method) {
                case "GET":
                    if (query == null) {
                        sendText(exchange, gson.toJson(manager.getAllTasks()), STATUS_OK);
                    } else {
                        Optional<Integer> id = parseId(query);
                        if (id.isPresent()) {
                            Task task = manager.getTaskById(id.get());
                            sendText(exchange, gson.toJson(task), STATUS_OK);
                        } else {
                            sendText(exchange, "Ошибка при обработке запроса", STATUS_BAD_REQUEST);
                        }
                    }
                    break;
                case "POST":
                    InputStream body = exchange.getRequestBody();
                    String json = new String(body.readAllBytes(), StandardCharsets.UTF_8);
                    Task newTask = gson.fromJson(json, Task.class);
                    if (query == null) {
                        if (manager.isOverlapTask(newTask)) {
                            sendText(exchange, "Задача пересекается с существующей", STATUS_CONFLICT);
                        } else {
                            manager.addTask(newTask);
                            sendText(exchange, "Задача создана", STATUS_CREATED);
                        }
                    } else {
                        Optional<Integer> id = parseId(query);
                        if (id.isPresent()) {
                            if (manager.isOverlapTask(newTask)) {
                                sendText(exchange, "Задача пересекается с существующей", STATUS_CONFLICT);
                            } else {
                                Task oldTask = manager.getTaskById(id.get());
                                manager.updateTask(oldTask, newTask);
                                sendText(exchange, "Задача обновлена", STATUS_CREATED);
                            }
                        } else {
                            sendText(exchange, "Ошибка при обработке запроса", STATUS_BAD_REQUEST);
                        }
                    }
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.deleteAllTasks();
                        sendText(exchange, "Все задачи удалены", STATUS_OK);
                    } else {
                        Optional<Integer> id = parseId(query);
                        if (id.isPresent()) {
                            Task task = manager.getTaskById(id.get());
                            manager.deleteTaskById(task);
                            sendText(exchange, "Задача удалена", STATUS_OK);

                        } else {
                            sendText(exchange, "Ошибка при обработке запроса", STATUS_BAD_REQUEST);
                        }
                    }
                    break;
                default:
                    sendText(exchange, "Метод не поддерживается", STATUS_METHOD_NOT_FOUND);
            }
        } catch (NotFoundException e) {
            sendText(exchange, e.getMessage(), STATUS_TASK_NOT_FOUND);
        } catch (Exception e) {
            sendText(exchange, "Ошибка при обработке запроса: " + e.getMessage(), STATUS_BAD_REQUEST);
        }
    }
}

