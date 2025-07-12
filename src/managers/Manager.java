package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import server.DurationTypeAdapter;
import server.LocalDateTimeTypeAdapter;


public class Manager {
    // HTTP-константы
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String MIME_JSON_UTF8     = "application/json;charset=utf-8";

    /** Возвращает файловый менеджер с историей in-memory */
    public static TaskManager getDefault() {
        File dir = new File("./data");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, "tasks.csv");
        return FileBackedTasksManager.loadFromFile(getDefaultHistory(), file);
    }

    /** HistoryManager — простой in-memory */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    /** Собирает единый Gson с регистрацией всех нужных адаптеров */
    public static Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    }
}
