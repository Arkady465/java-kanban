// src/main/java/server/HttpTaskServer.java
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

        // 1) CRUD задач
        server.createContext("/tasks/task",      new TaskHandler(manager));
        // 2) CRUD эпиков
        server.createContext("/tasks/epic",      new EpicHandler(manager));
        // 3) CRUD подзадач
        server.createContext("/tasks/subtask",   new SubtaskHandler(manager));
        // 4) Историю просмотров
        server.createContext("/tasks/history",   new HistoryHandler(manager));
        // 5) Приоритетный список — просто корень /tasks
        server.createContext("/tasks",           new PrioritizedHandler(manager));
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
