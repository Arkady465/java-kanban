package server;

import managers.InMemoryTaskManager;
import managers.Manager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpServerEpicsTest extends HttpServerManagerTest {

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
    public void testAddTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Description",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        String json = gson.toJson(epic);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/epics", json);

        assertEquals(201, response.statusCode(), "Должен вернуться статус 201 CREATED");

        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals("Test Epic", epics.get(0).getName());
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addEpic(epic);

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic"));
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Epic epic = new Epic("ToDelete", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addEpic(epic);

        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/epics?");

        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllEpics().isEmpty());
    }
}
