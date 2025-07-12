package server;

import com.sun.net.httpserver.HttpExchange;
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
        switch (method) {
            case "GET": {
                Optional<Integer> idOpt = parseId(exchange.getRequestURI().getQuery());
                if (idOpt.isPresent()) {
                    Subtask sub = manager.getSubtaskById(idOpt.get());
                    sendText(exchange, gson.toJson(sub), STATUS_OK);
                } else {
                    sendText(exchange, gson.toJson(manager.getAllSubTasks()), STATUS_OK);
                }
                break;
            }
            case "POST": {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Subtask s = gson.fromJson(body, Subtask.class);
                if (s.getId() == 0) {
                    manager.addSubtask(s);
                    sendText(exchange, gson.toJson(s), STATUS_CREATED);
                } else {
                    Subtask old = manager.getSubtaskById(s.getId());
                    manager.updateSubTask(old, s);
                    sendText(exchange, gson.toJson(s), STATUS_OK);
                }
                break;
            }
            case "DELETE": {
                Optional<Integer> delId = parseId(exchange.getRequestURI().getQuery());
                if (delId.isPresent()) {
                    Subtask old = manager.getSubtaskById(delId.get());
                    manager.deleteSubtaskById(old);
                } else {
                    manager.deleteAllESubtask();
                }
                sendText(exchange, "", STATUS_OK);
                break;
            }
            default:
                sendText(exchange, "Метод не поддерживается", STATUS_METHOD_NOT_FOUND);
        }
    }
}
