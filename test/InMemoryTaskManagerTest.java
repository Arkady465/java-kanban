import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;
import service.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault(); // или new InMemoryTaskManager();
    }

    @Test
    void shouldAddAndRetrieveTask() {
        Task task = new Task("Task 1", "Description 1");
        taskManager.addTask(task);
        Task retrieved = taskManager.getTask(task.getId());
        assertEquals(task, retrieved, "Retrieved task should match the added task");
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.addEpic(epic);
        Epic retrieved = (Epic) taskManager.getTask(epic.getId());
        assertEquals(epic, retrieved);
    }

    @Test
    void shouldAddAndRetrieveSubtask() {
        Epic epic = new Epic("Epic", "Epic desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Sub", "Sub desc", epic.getId());
        taskManager.addSubtask(subtask);

        Subtask retrieved = (Subtask) taskManager.getTask(subtask.getId());
        assertEquals(subtask, retrieved);
    }

    @Test
    void shouldUpdateEpicStatusBasedOnSubtasks() {
        Epic epic = new Epic("Epic", "desc");
        taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub 1", "desc", epic.getId());
        Subtask sub2 = new Subtask("Sub 2", "desc", epic.getId());
        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);

        assertEquals(epic.getStatus(), sub1.getStatus()); // оба должны быть NEW
    }

    @Test
    void historyShouldNotExceedLimit() {
        for (int i = 0; i < 15; i++) {
            Task task = new Task("Task " + i, "Desc " + i);
            taskManager.addTask(task);
            taskManager.getTask(task.getId()); // добавляем в историю
        }
        List<Task> history = taskManager.getHistory();
        assertEquals(10, history.size(), "History should not exceed 10 tasks");
    }

    @Test
    void shouldReturnHistoryOfTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Epic epic = new Epic("Epic", "Epic desc");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);

        // Делаем обращения к задачам, чтобы они попали в историю
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
