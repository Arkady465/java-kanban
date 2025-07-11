package taskmanager.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер для (де)сериализации LocalDateTime через Gson.
 * Формат: ISO_LOCAL_DATE_TIME, например "2025-06-03T15:30:00".
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        // Преобразуем в строку формата "2025-06-03T15:30:00"
        return new JsonPrimitive(src.format(formatter));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return LocalDateTime.parse(json.getAsString(), formatter);
        } catch (Exception e) {
            throw new JsonParseException("Невозможно распарсить LocalDateTime: " + json.getAsString());
        }
    }
}
