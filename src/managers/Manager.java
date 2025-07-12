package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import server.DurationTypeAdapter;
import server.LocalDateTimeTypeAdapter;

/**
 * Утильный класс для получения менеджеров задач, общих констант и
 * единого настроенного экземпляра Gson.
 */
public class Manager {
    // HTTP-константы
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String MIME_JSON_UTF8     = "application/json;charset=utf-8";

    /**
     * Возвращает файловый менеджер задач, сохраняющийся в "./data/tasks.csv",
     * с историей в памяти.
     */
    public static TaskManager getDefault() {
        File dir = new File("./data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "tasks.csv");
        return FileBackedTaskManager.loadFromFile(getDefaultHistory(), file);
    }

    /**
     * Возвращает простой in-memory HistoryManager.
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    /**
     * Строит и возвращает единый экземпляр Gson с нужными адаптерами.
     */
    public static Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    }
}
