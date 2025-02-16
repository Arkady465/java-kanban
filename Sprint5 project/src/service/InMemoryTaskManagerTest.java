package yandex.service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldAddAndRetrieveTask() {
        Task task = new Task("Task 1", "Description 1");
        Task savedTask = taskManager.addTask(task);

        assertNotNull(savedTask, "Task should be saved and not null");
        assertEquals(task.getName(), savedTask.getName(), "Task names should match");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Task descriptions should match");
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        Epic savedEpic = taskManager.addEpic(epic);

        assertNotNull(savedEpic, "Epic should be saved and not null");
        assertEquals(epic.getName(), savedEpic.getName(), "Epic names should match");
        assertEquals(epic.getDescription(), savedEpic.getDescription(), "Epic descriptions should match");
    }

    @Test
    void shouldAddAndRetrieveSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask savedSubtask = taskManager.addSubtask(subtask);

        assertNotNull(savedSubtask, "Subtask should be saved and not null");
        assertEquals(subtask.getEpicID(), savedSubtask.getEpicID(), "Subtask's epic ID should match");
    }

    @Test
    void shouldUpdateEpicStatusBasedOnSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status should be IN_PROGRESS when not all subtasks are done");

        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus(), "Epic status should be DONE when all subtasks are done");
    }

    @Test
    void shouldReturnHistoryOfTasks() {
        Task task1 = taskManager.addTask(new Task("Task 1", "Description 1"));
        Epic epic = taskManager.addEpic(new Epic("Epic 1", "Description 1"));
        Subtask subtask = taskManager.addSubtask(new Subtask("Subtask 1", "Description 1", epic.getId()));

        taskManager.getTask(task1.getId());
        taskManager.getTask(epic.getId());
        taskManager.getTask(subtask.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(3, history.size(), "History should contain 3 tasks");
        assertEquals(task1, history.get(0), "First task in history should match");
        assertEquals(epic, history.get(1), "Second task in history should match");
        assertEquals(subtask, history.get(2), "Third task in history should match");
    }

    @Test
    void historyShouldNotExceedLimit() {
        for (int i = 1; i <= 12; i++) {
            taskManager.addTask(new Task("Task " + i, "Description " + i));
        }

        for (int i = 1; i <= 12; i++) {
            taskManager.getTask(i);
        }

        List<Task> history = taskManager.getHistory();

        assertEquals(10, history.size(), "History should only contain the last 10 tasks");
        assertEquals(3, history.get(0).getId(), "First task in history should match the 3rd task added");
        assertEquals(12, history.get(9).getId(), "Last task in history should match the last task added");
    }
}
