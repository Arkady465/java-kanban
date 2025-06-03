package ru.yandex.todo.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.todo.manager.TaskManager;
import ru.yandex.todo.manager.Managers;
import ru.yandex.todo.util.DurationAdapter;
import ru.yandex.todo.util.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;

/**
 * Основной класс HTTP‐сервера.
 *
 * - Конструктор принимает TaskManager (InMemory или FileBacked).
 * - Метод start() запускает HttpServer на порту 8080, привязывает все хэндлеры.
 * - Метод stop() останавливает сервер.
 */
public class HttpTaskServer {
    public static final int PORT = 8080;

    private final TaskManager manager;
    private HttpServer server;

    // Единый объект Gson для всех хэндлеров, со спрингами для Duration и LocalDateTime
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    /**
     * Возвращает общий экземпляр Gson для всех хэндлеров
     */
    public static Gson getGson() {
        return gson;
    }

    /**
     * Запускает HttpServer на порту 8080 и создаёт контексты:
     *   /tasks       → TasksHandler
     *   /subtasks    → SubtasksHandler
     *   /epics       → EpicsHandler
     *   /history     → HistoryHandler
     *   /prioritized → PrioritizedHandler
     */
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Создаём контексты и их хэндлеры
            server.createContext("/tasks", new TasksHandler(manager, gson));
            server.createContext("/subtasks", new SubtasksHandler(manager, gson));
            server.createContext("/epics", new EpicsHandler(manager, gson));
            server.createContext("/history", new HistoryHandler(manager, gson));
            server.createContext("/prioritized", new PrioritizedHandler(manager, gson));

            // Настраиваем пул потоков
            server.setExecutor(Executors.newFixedThreadPool(4));
            server.start();
            System.out.println("HTTP Task Server started on port " + PORT);
        } catch (IOException e) {
            System.err.println("Не удалось запустить HTTP‐сервер: " + e.getMessage());
        }
    }

    /**
     * Останавливает сервер (остановка немедленно, 0 миллисекунд ожидания).
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("HTTP Task Server stopped.");
        }
    }

    /**
     * Точка входа, если запускаем из main.
     * По умолчанию берём менеджер из Managers.getDefault().
     */
    public static void main(String[] args) {
        TaskManager defaultManager = Managers.getDefault();
        HttpTaskServer httpServer = new HttpTaskServer(defaultManager);
        httpServer.start();
        System.out.println("Press Ctrl+C to stop the server.");
    }
}
