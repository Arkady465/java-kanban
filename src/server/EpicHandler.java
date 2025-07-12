package server;

import com.sun.net.httpserver.HttpExchange;
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
        switch (method) {
            case "GET": {
                Optional<Integer> idOpt = parseId(exchange.getRequestURI().getQuery());
                if (idOpt.isPresent()) {
                    Epic epic = manager.getEpicById(idOpt.get());
                    sendText(exchange, gson.toJson(epic), STATUS_OK);
                } else {
                    sendText(exchange, gson.toJson(manager.getAllEpics()), STATUS_OK);
                }
                break;
            }
            case "POST": {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Epic e = gson.fromJson(body, Epic.class);
                if (e.getId() == 0) {
                    manager.addEpic(e);
                    sendText(exchange, gson.toJson(e), STATUS_CREATED);
                } else {
                    Epic old = manager.getEpicById(e.getId());
                    manager.updateEpic(old, e);
                    sendText(exchange, gson.toJson(e), STATUS_OK);
                }
                break;
            }
            case "DELETE": {
                Optional<Integer> delId = parseId(exchange.getRequestURI().getQuery());
                if (delId.isPresent()) {
                    Epic old = manager.getEpicById(delId.get());
                    manager.deleteEpicById(old);
                } else {
                    manager.deleteAllEpics();
                }
                sendText(exchange, "", STATUS_OK);
                break;
            }
            default:
                sendText(exchange, "Метод не поддерживается", STATUS_METHOD_NOT_FOUND);
        }
    }
}
