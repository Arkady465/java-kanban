package server;

import com.sun.net.httpserver.HttpServer;
import managers.Manager;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // 1) Приоритетный список — GET /tasks
        server.createContext("/tasks",       new PrioritizedHandler(manager));
        // 2) CRUD задач — POST/GET/DELETE /tasks/task
        server.createContext("/tasks/task",     new TaskHandler(manager));
        // 3) CRUD эпиков — POST/GET/DELETE /tasks/epic
        server.createContext("/tasks/epic",     new EpicHandler(manager));
        // 4) CRUD подзадач — POST/GET/DELETE /tasks/subtask
        server.createContext("/tasks/subtask",  new SubtaskHandler(manager));
        // 5) История просмотров — GET /tasks/history
        server.createContext("/tasks/history",  new HistoryHandler(manager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer(Manager.getDefault()).start();
    }
}
