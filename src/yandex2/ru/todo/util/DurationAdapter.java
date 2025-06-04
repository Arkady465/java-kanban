package yandex.ru.yandex.todo.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.format.DateTimeParseException;

/**
 * Адаптер для (де)сериализации java.time.Duration через Gson.
 * Сохраняет Duration в формате ISO, например "PT15M" (15 минут).
 */
public class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
    @Override
    public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
        // Сохраняем Duration как строку: "PT15M", "PT1H30M" и т.д.
        return new JsonPrimitive(src.toString());
    }

    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return Duration.parse(json.getAsString());
        } catch (DateTimeParseException e) {
            throw new JsonParseException("Невозможно распарсить Duration: " + json.getAsString());
        }
    }
}
