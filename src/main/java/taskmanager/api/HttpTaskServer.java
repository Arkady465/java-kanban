package taskmanager.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import taskmanager.api.handler.*;
import taskmanager.service.Managers;
import taskmanager.service.TaskManager;
import taskmanager.util.DurationAdapter;
import taskmanager.util.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;

public class HttpTaskServer {
    private final TaskManager manager;
    private HttpServer server;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public static Gson getGson() {
        return gson;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new TasksHandler(manager, gson));
            server.createContext("/subtasks", new SubtasksHandler(manager, gson));
            server.createContext("/epics", new EpicsHandler(manager, gson));
            server.createContext("/history", new HistoryHandler(manager, gson));
            server.createContext("/prioritized", new PrioritizedHandler(manager, gson));
            server.setExecutor(Executors.newFixedThreadPool(4));
            server.start();
            System.out.println("HTTP Task Server started on port 8080");
        } catch (IOException e) {
            System.err.println("Failed to start HTTP server: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("HTTP Task Server stopped.");
        }
    }

    public static void main(String[] args) {
        TaskManager defaultManager = Managers.getDefault();
        HttpTaskServer httpServer = new HttpTaskServer(defaultManager);
        httpServer.start();
        System.out.println("Press Ctrl+C to stop the server.");
    }
}