package server;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import managers.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
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
                        sendText(exchange, gson.toJson(manager.getAllSubTasks()), STATUS_OK);
                    } else {
                        Optional<Integer> id = parseId(query);
                        if (id.isPresent()) {
                            Subtask subtask = manager.getSubtaskById(id.get());
                            if (subtask != null) {
                                sendText(exchange, gson.toJson(subtask), STATUS_OK);
                            } else {
                                sendText(exchange, "Подзадача не найдена", STATUS_TASK_NOT_FOUND);
                            }
                        } else {
                            sendText(exchange, "Ошибка при обработке запроса", STATUS_BAD_REQUEST);
                        }
                    }
                    break;
                case "POST":
                    InputStream body = exchange.getRequestBody();
                    String json = new String(body.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask newSubtask = gson.fromJson(json, Subtask.class);
                    if (query == null) {
                        if (manager.isOverlapTask(newSubtask)) {
                            sendText(exchange, "Задача пересекается с существующей", STATUS_CONFLICT);
                        } else {
                            manager.addSubtask(newSubtask);
                            sendText(exchange, "Подзадача создана", STATUS_CREATED);
                        }
                    } else {
                        Optional<Integer> id = parseId(query);
                        if (id.isPresent()) {
                            if (manager.isOverlapTask(newSubtask)) {
                                sendText(exchange, "Задача пересекается с существующей", STATUS_CONFLICT);
                            } else {
                                Subtask oldSubtask = manager.getSubtaskById(id.get());
                                manager.updateSubTask(oldSubtask, newSubtask);
                                sendText(exchange, "Подзадача обновлёна", STATUS_CREATED);
                            }
                        } else {
                            sendText(exchange, "Ошибка при обработке запроса", STATUS_BAD_REQUEST);
                        }
                    }
                    break;
                case "DELETE":
                    if (query == null) {
                        // Используем исправленное имя метода
                        manager.deleteAllSubtasks();
                        sendText(exchange, "Все подзадачи удалены", STATUS_OK);
                    } else {
                        Optional<Integer> id = parseId(query);
                        if (id.isPresent()) {
                            Subtask subtask = manager.getSubtaskById(id.get());
                            manager.deleteSubtaskById(subtask);
                            sendText(exchange, "Подзадача удалёна", STATUS_OK);
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
            sendText(exchange, "Ошибка при обработке запроса: " + e.getMessage(), STATUS_INTERNAL_ERROR);
        }
    }
}