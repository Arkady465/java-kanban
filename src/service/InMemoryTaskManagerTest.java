package service;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    void shouldAddAndRetrieveTask() {
        Task task = new Task("Test Task", "Test Description");
        manager.addTask(task);
        Task retrieved = manager.getTask(task.getId());
        assertEquals(task, retrieved);
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        manager.addEpic(epic);
        Epic retrieved = manager.getEpic(epic.getId());
        assertEquals(epic, retrieved);
    }

    @Test
    void shouldAddAndRetrieveSubtask() {
        Epic epic = new Epic("Test Epic", "Epic Desc");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epic.getId());
        manager.addSubtask(subtask);
        Subtask retrieved = manager.getSubtask(subtask.getId());
        assertEquals(subtask, retrieved);
    }

    @Test
    void shouldReturnHistoryOfTasks() {
        Task task = new Task("Task", "desc");
        Epic epic = new Epic("Epic", "desc");
        Subtask subtask = new Subtask("Subtask", "desc", 0);

        task = manager.addTask(task);
        epic = manager.addEpic(epic);
        subtask = new Subtask("Subtask", "desc", epic.getId());
        subtask = manager.addSubtask(subtask);

        manager.getTask(task.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());

        List<Task> history = manager.getHistory();
        assertEquals(3, history.size(), "History should contain 3 tasks");
        assertEquals(task, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subtask, history.get(2));
    }

    @Test
    void historyShouldNotExceedLimit() {
        for (int i = 0; i < 12; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            manager.addTask(task);
            manager.getTask(task.getId());
        }
        List<Task> history = manager.getHistory();
        assertEquals(10, history.size());
        assertEquals("Task 2", history.get(0).getName());
        assertEquals("Task 11", history.get(9).getName());
    }

    @Test
    void shouldUpdateEpicStatusBasedOnSubtasks() {
        Epic epic = new Epic("Epic", "desc");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("S1", "desc", epic.getId());
        Subtask sub2 = new Subtask("S2", "desc", epic.getId());

        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        sub1.setStatus(Status.DONE);
        manager.updateSubtask(sub1);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }
}
