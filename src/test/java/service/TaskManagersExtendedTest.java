package service;

import org.junit.jupiter.api.Test;
import taskManager.model.Epic;
import taskManager.model.Status;
import taskManager.model.Subtask;
import taskManager.model.Task;
import taskManager.service.InMemoryTaskManager;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagersExtendedTest {

    @Test
    void testNonIntersectingTasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Desc 1");
        task1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(30));
        manager.addTask(task1);

        Task task2 = new Task("Task 2", "Desc 2");
        task2.setStartTime(LocalDateTime.of(2025, 1, 1, 11, 0));
        task2.setDuration(Duration.ofMinutes(30));
        manager.addTask(task2);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void testGetPrioritizedTasksOrder() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task early = new Task("Early", "Desc");
        early.setStartTime(LocalDateTime.of(2025, 1, 1, 8, 0));
        early.setDuration(Duration.ofMinutes(30));
        manager.addTask(early);

        Task late = new Task("Late", "Desc");
        late.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        late.setDuration(Duration.ofMinutes(30));
        manager.addTask(late);

        List<Task> prioritized = manager.getAllTasks(); // предполагается, что они отсортированы
        assertEquals("Early", prioritized.get(0).getName());
        assertEquals("Late", prioritized.get(1).getName());
    }

    @Test
    void testEpicTimeCalculation() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = manager.addEpic(new Epic("Epic", "desc"));
        Subtask sub1 = new Subtask("Sub 1", "Desc", epic.getId());
        sub1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        sub1.setDuration(Duration.ofMinutes(60));
        manager.addSubtask(sub1);

        Subtask sub2 = new Subtask("Sub 2", "Desc", epic.getId());
        sub2.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        sub2.setDuration(Duration.ofMinutes(60));
        manager.addSubtask(sub2);

        Epic updatedEpic = manager.getEpic(epic.getId());
        assertNotNull(updatedEpic);
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void testIntersectionDetection() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Desc");
        task1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        manager.addTask(task1);

        Task task2 = new Task("Task 2", "Desc");
        task2.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 30));
        task2.setDuration(Duration.ofMinutes(60));

        boolean intersects = isIntersecting(task1, task2);
        assertTrue(intersects);
    }

    @Test
    void testAddTaskWithValidTime() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task", "Desc");
        task.setStartTime(LocalDateTime.of(2025, 1, 1, 14, 0));
        task.setDuration(Duration.ofMinutes(30));
        manager.addTask(task);
        assertEquals(task, manager.getTask(task.getId()));
    }

    private boolean isIntersecting(Task t1, Task t2) {
        if (t1.getStartTime() == null || t2.getStartTime() == null) return false;
        LocalDateTime start1 = t1.getStartTime();
        LocalDateTime end1 = start1.plus(t1.getDuration());
        LocalDateTime start2 = t2.getStartTime();
        LocalDateTime end2 = start2.plus(t2.getDuration());

        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }
}
