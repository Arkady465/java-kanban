package server;

import managers.InMemoryTaskManager;
import managers.Manager;
import org.junit.jupiter.api.*;
import task.Task;
import task.Status;

import java.io.IOException;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTasksTest extends HttpServerManagerTest{

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
        Task task = new Task("Test Task", "Description",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        String json = gson.toJson(task);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/tasks", json);

        assertEquals(201, response.statusCode(), "Должен вернуться статус 201 CREATED");

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getName());
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Task", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addTask(task);

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/tasks");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task"));
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("ToDelete", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addTask(task);

        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/tasks?");

        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());
    }
}

