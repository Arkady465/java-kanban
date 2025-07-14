package server;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import managers.TaskManager;
import task.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
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
                        sendText(exchange, gson.toJson(manager.getAllEpics()), STATUS_OK);
                    } else {
                        Optional<Integer> id = parseId(query);
                        if (id.isPresent()) {
                            Epic epic = manager.getEpicById(id.get());
                            sendText(exchange, gson.toJson(epic), STATUS_OK);
                        } else {
                            sendText(exchange, "Ошибка при обработке запроса", STATUS_BAD_REQUEST);
                        }
                    }
                    break;
                case "POST":
                    InputStream body = exchange.getRequestBody();
                    String json = new String(body.readAllBytes(), StandardCharsets.UTF_8);
                    Epic newEpic = gson.fromJson(json, Epic.class);
                    if (query == null) {
                        if (manager.isOverlapTask(newEpic)) {
                            sendText(exchange, "Задача пересекается с существующей", STATUS_CONFLICT);
                        } else {
                            manager.addEpic(newEpic);
                            sendText(exchange, "Эпик создан", STATUS_CREATED);
                        }
                    } else {
                        Optional<Integer> id = parseId(query);
                        if (id.isPresent()) {
                            if (manager.isOverlapTask(newEpic)) {
                                sendText(exchange, "Задача пересекается с существующей", STATUS_CONFLICT);
                            } else {
                                Epic oldEpic = manager.getEpicById(id.get());
                                manager.updateEpic(oldEpic, newEpic);
                                sendText(exchange, "Эпик обновлён", STATUS_CREATED);
                            }
                        } else {
                            sendText(exchange, "Ошибка при обработке запроса", STATUS_BAD_REQUEST);
                        }
                    }
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.deleteAllEpics();
                        sendText(exchange, "Все эпики удалены", STATUS_OK);
                    } else {
                        Optional<Integer> id = parseId(query);
                        if (id.isPresent()) {
                            Epic epic = manager.getEpicById(id.get());
                            manager.deleteEpicById(epic);
                            sendText(exchange, "Эпик удалён", STATUS_OK);
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