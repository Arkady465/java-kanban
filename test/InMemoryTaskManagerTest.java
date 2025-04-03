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

    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    void shouldAddAndRetrieveTask() {
        Task task = new Task("Test Task", "Test Description");
        Task added = manager.addTask(task);
        Task retrieved = manager.getTask(added.getId());

        assertEquals(added, retrieved);
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        Epic added = manager.addEpic(epic);
        Epic retrieved = manager.getEpic(added.getId());

        assertEquals(added, retrieved);
    }

    @Test
    void shouldAddAndRetrieveSubtask() {
        Epic epic = manager.addEpic(new Epic("Epic 1", "Epic Desc"));
        Subtask subtask = new Subtask("Subtask 1", "Subtask Desc", epic.getId());
        Subtask added = manager.addSubtask(subtask);

        Subtask retrieved = manager.getSubtask(added.getId());
        assertEquals(added, retrieved);
    }

    @Test
    void shouldUpdateEpicStatusBasedOnSubtasks() {
        Epic epic = manager.addEpic(new Epic("Epic", "Desc"));
        Subtask sub1 = manager.addSubtask(new Subtask("Sub1", "Desc", epic.getId()));
        Subtask sub2 = manager.addSubtask(new Subtask("Sub2", "Desc", epic.getId()));

        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.NEW);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);

        Epic updatedEpic = manager.getEpic(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(), "Epic status should be IN_PROGRESS when not all subtasks are done");
    }

    @Test
    void historyShouldNotExceedLimit() {
        for (int i = 0; i < 12; i++) {
            Task task = manager.addTask(new Task("Task " + i, "Description " + i));
            manager.getTask(task.getId());
        }
        List<Task> history = manager.getHistory();
        assertEquals(10, history.size());
        assertEquals("Task 2", history.get(0).getName());
        assertEquals("Task 11", history.get(9).getName());
    }

    @Test
    void shouldReturnHistoryOfTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Epic epic = new Epic("Epic", "Epic desc");

        // Используем корректный экземпляр TaskManager, а не null
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);

        // Добавляем в историю через вызов get
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "History should contain 3 tasks");
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(epic, history.get(2));
    }
}
