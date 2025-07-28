package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void initTest() {
        super.manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void isOverlapTask() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS, LocalDateTime.of(2025,6, 19, 14,0), Duration.ofMinutes(10));
        manager.addTask(task2);

        Epic epic = new Epic("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        manager.addEpic(epic);
        Epic epic2 = new Epic("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        manager.addEpic(epic2);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", Status.IN_PROGRESS, LocalDateTime.of(2025,6, 19, 14,11), Duration.ofMinutes(30), epic2.getId());
        manager.addSubtask(subTask);

        assertTrue(manager.isOverlapTask(task));
        assertTrue(manager.isOverlapTask(epic));

        assertEquals(2, manager.getPrioritizedTasks().size());
    }
}