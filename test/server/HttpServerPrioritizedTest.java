package server;

import managers.InMemoryTaskManager;
import managers.Manager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpServerPrioritizedTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager(Manager.getDefaultHistory());
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGet() throws IOException, InterruptedException {
        Task t1 = new Task("T1", "First", Status.NEW,
                LocalDateTime.of(2025, 7, 1, 10, 0), Duration.ofMinutes(30));
        Task t2 = new Task("T2", "Second", Status.NEW,
                LocalDateTime.of(2025, 7, 1, 12, 0), Duration.ofMinutes(30));

        manager.addTask(t2);
        manager.addTask(t1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        String body = response.body();

        assertTrue(body.indexOf("T1") < body.indexOf("T2"));
    }

}
