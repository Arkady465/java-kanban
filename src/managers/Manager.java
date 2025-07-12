package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Duration;
import java.time.LocalDateTime;
import server.adapter.DurationTypeAdapter;
import server.adapter.LocalDateTimeTypeAdapter;

public class Managers {
    // HTTP-константы
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String MIME_JSON_UTF8     = "application/json;charset=utf-8";

    /** Возвращает настроенный экземпляр Gson с нужными адаптерами */
    public static Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    }
}
