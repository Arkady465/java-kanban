import model.Epic;
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
        Task task = taskManager.addTask(new Task("Test Task", "Test Description"));
        Task retrieved = taskManager.getTask(task.getId());
        assertEquals(task, retrieved);
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        Epic epic = taskManager.addEpic(new Epic("Epic 1", "Epic Description"));
        Epic retrieved = taskManager.getEpic(epic.getId());
        assertEquals(epic, retrieved);
    }

    @Test
    void shouldAddAndRetrieveSubtask() {
        Epic epic = taskManager.addEpic(new Epic("Epic 1", "Epic Desc"));
        Task subtask = taskManager.addSubtask(new model.Subtask("Subtask 1", "Subtask Desc", epic.getId()));
        model.Subtask retrieved = taskManager.getSubtask(subtask.getId());
        assertEquals(subtask, retrieved);
    }

    @Test
    void shouldUpdateEpicStatusBasedOnSubtasks() {
        Epic epic = taskManager.addEpic(new Epic("Epic", "Desc"));
        model.Subtask sub1 = taskManager.addSubtask(new model.Subtask("Sub1", "Desc", epic.getId()));
        model.Subtask sub2 = taskManager.addSubtask(new model.Subtask("Sub2", "Desc", epic.getId()));

        sub1.setStatus(model.Status.DONE);
        sub2.setStatus(model.Status.NEW);
        taskManager.updateSubtask(sub1);
        taskManager.updateSubtask(sub2);

        Epic updatedEpic = taskManager.getEpic(epic.getId());
        assertEquals(model.Status.IN_PROGRESS, updatedEpic.getStatus(), "Epic status should be IN_PROGRESS when not all subtasks are done");
    }

    @Test
    void historyShouldNotExceedLimit() {
        for (int i = 0; i < 12; i++) {
            Task task = taskManager.addTask(new Task("Task " + i, "Description " + i));
            taskManager.getTask(task.getId());
        }
        List<Task> history = taskManager.getHistory();
        assertEquals(10, history.size());
        assertEquals("Task 2", history.get(0).getName());
        assertEquals("Task 11", history.get(9).getName());
    }

    @Test
    void shouldReturnHistoryOfTasks() {
        // Используем возвращаемые объекты, чтобы быть уверенными в корректном присвоении id
        Task task1 = taskManager.addTask(new Task("Task 1", "Description 1"));
        Task task2 = taskManager.addTask(new Task("Task 2", "Description 2"));
        Epic epic = taskManager.addEpic(new Epic("Epic", "Epic desc"));

        // Добавляем задачи в историю через вызовы get
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
