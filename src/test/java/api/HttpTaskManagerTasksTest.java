package api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import taskManager.api.HttpTaskServer;
import taskManager.model.Task;
import taskManager.model.Status;
import taskManager.service.InMemoryTaskManager;
import taskManager.service.TaskManager;
import com.google.gson.Gson;

public class HttpTaskManagerTasksTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private HttpClient client;
    private final Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();
        clearAllData();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    private void clearAllData() {
        manager.clearAllTasks();
        manager.clearAllSubtasks();
        manager.clearAllEpics();
    }

    @Test
    @DisplayName("Добавление новой задачи через HTTP endpoint")
    public void testAddTask() throws IOException, InterruptedException {
        // Создаем задачу с минимальными параметрами
        Task task = new Task("Test 2", "Testing task 2");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.now());

        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус код при создании задачи");

        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

        Task createdTask = tasksFromManager.get(0);
        assertEquals("Test 2", createdTask.getName(), "Некорректное имя задачи");
        assertEquals("Testing task 2", createdTask.getDescription(), "Некорректное описание задачи");
        assertEquals(Status.NEW, createdTask.getStatus(), "Некорректный статус задачи");
        assertEquals(Duration.ofMinutes(5), createdTask.getDuration(), "Некорректная продолжительность");
    }

    @Test
    @DisplayName("Создание задачи без указания времени")
    public void testAddTaskWithoutTime() throws IOException, InterruptedException {
        Task task = new Task("Simple Task", "No time specified");
        task.setStatus(Status.NEW);

        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task createdTask = manager.getAllTasks().get(0);
        assertNull(createdTask.getStartTime(), "StartTime должен быть null");
        assertNull(createdTask.getDuration(), "Duration должен быть null");
    }

    @Test
    @DisplayName("Попытка создать задачу с неверными данными")
    public void testAddInvalidTask() throws IOException, InterruptedException {
        // Неполные данные - нет имени
        String invalidJson = "{\"description\":\"Invalid task\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Должна быть ошибка 400 при невалидных данных");
    }

    @Test
    @DisplayName("Обновление существующей задачи")
    public void testUpdateTask() throws IOException, InterruptedException {
        // Создаем начальную задачу
        Task task = new Task("Original", "Original desc");
        manager.addTask(task);

        // Подготавливаем обновленные данные
        Task updatedTask = new Task("Updated", "Updated desc");
        updatedTask.setId(task.getId());
        updatedTask.setStatus(Status.IN_PROGRESS);
        updatedTask.setDuration(Duration.ofMinutes(30));
        updatedTask.setStartTime(LocalDateTime.now().plusDays(1));

        String taskJson = gson.toJson(updatedTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task taskFromManager = manager.getTask(task.getId());
        assertEquals("Updated", taskFromManager.getName(), "Имя не обновилось");
        assertEquals(Status.IN_PROGRESS, taskFromManager.getStatus(), "Статус не обновился");
        assertEquals(Duration.ofMinutes(30), taskFromManager.getDuration(), "Duration не обновился");
    }
}