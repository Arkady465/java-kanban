package api;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import taskmanager.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class HistoryHandlerTest extends HttpTaskManagerTest {
    @Test
    void testGetHistory() throws IOException, InterruptedException {
        Task task = manager.addTask(new Task("Task", "Description"));
        manager.getTask(task.getId()); // Добавляем в историю

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        assertEquals(1, history.size());
        assertEquals(task.getId(), history.get(0).getId());
    }
}
