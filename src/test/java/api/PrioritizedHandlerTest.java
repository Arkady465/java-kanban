package api;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import taskManager.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest extends HttpTaskManagerTest {
    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Description");
        task1.setStartTime(LocalDateTime.now());
        manager.addTask(task1);

        Task task2 = new Task("Task2", "Description");
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        manager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> prioritized = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, prioritized.size());
        assertEquals(task1.getId(), prioritized.get(0).getId());
    }
}
