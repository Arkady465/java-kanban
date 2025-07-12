// src/main/java/managers/Manager.java
package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import server.DurationTypeAdapter;
import server.LocalDateTimeTypeAdapter;

import managers.TaskManager;
import managers.FileBackedTasksManager;
import managers.HistoryManager;
import managers.InMemoryHistoryManager;

/**
 * Utility class for creating TaskManager and HistoryManager instances,
 * holding HTTP constants, and providing a shared Gson configuration.
 */
public class Manager {
    // HTTP header constants
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String MIME_JSON_UTF8     = "application/json;charset=utf-8";

    /**
     * Returns a FileBackedTasksManager storing data in "./data/tasks.csv"
     * with an in-memory history manager.
     */
    public static TaskManager getDefault() {
        File dir = new File("./data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "tasks.csv");
        return FileBackedTasksManager.loadFromFile(getDefaultHistory(), file);
    }

    /**
     * Returns a simple in-memory HistoryManager.
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    /**
     * Builds and returns a Gson instance preconfigured with type adapters
     * for java.time.Duration and java.time.LocalDateTime.
     */
    public static Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    }
}
