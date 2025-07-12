// src/main/java/managers/Manager.java
package managers;

import java.io.File;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Duration;
import java.time.LocalDateTime;
import server.adapter.DurationTypeAdapter;
import server.adapter.LocalDateTimeTypeAdapter;

public class Manager {
    public static TaskManager getDefault() {
        File directory = new File("./data");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, "tasks.csv");
        return FileBackedTaskManager.loadFromFile(getDefaultHistory(), file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    // =====================================================================
    // HTTP-константы (вынесли из BaseHttpHandler)
    // =====================================================================
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String MIME_JSON_UTF8     = "application/json;charset=utf-8";

    /** Собирает и возвращает единый Gson с нужными адаптерами */
    public static Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    }
}
