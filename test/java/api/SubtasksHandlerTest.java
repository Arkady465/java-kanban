package api;

import org.junit.jupiter.api.Test;
import taskmanager.model.Epic;
import taskmanager.model.Status;
import taskmanager.model.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtasksHandlerTest extends HttpTaskManagerTest {
    @Test
    void testCreateSubtask() throws IOException, InterruptedException {
        Epic epic = manager.addEpic(new Epic("Epic", "Description"));
        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());
        String subtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = manager.addEpic(new Epic("Epic", "Description"));
        Subtask subtask = manager.addSubtask(new Subtask("Subtask", "Description", epic.getId()));
        subtask.setStatus(Status.IN_PROGRESS);
        String subtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        assertEquals(Status.IN_PROGRESS, manager.getSubtask(subtask.getId()).getStatus());
    }
}
