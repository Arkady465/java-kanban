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

        // Эндпоинты, под которые писаны HTTP-тесты:
        server.createContext("/tasks",       new TaskHandler(manager));
        server.createContext("/epics",       new EpicHandler(manager));
        server.createContext("/subtasks",    new SubtaskHandler(manager));
        server.createContext("/history",     new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
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
