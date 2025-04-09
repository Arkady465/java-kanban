import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerExtendedTest {
    private InMemoryTaskManager manager;

    @BeforeEach
    public void setup() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void testAddTaskWithValidTime() {
        Task task = new Task("Task 1", "Description 1");
        task.setStartTime(LocalDateTime.of(2025, 4, 6, 10, 0));
        task.setDuration(Duration.ofMinutes(60));
        manager.addTask(task);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(1, prioritized.size());
        assertEquals(task, prioritized.get(0));
    }

    @Test
    public void testIntersectionDetection() {
        Task task1 = new Task("Task 1", "Desc 1");
        task1.setStartTime(LocalDateTime.of(2025, 4, 6, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        manager.addTask(task1);

        Task task2 = new Task("Task 2", "Desc 2");
        task2.setStartTime(LocalDateTime.of(2025, 4, 6, 10, 30));
        task2.setDuration(Duration.ofMinutes(60));

        Exception exception = assertThrows(RuntimeException.class, () -> manager.addTask(task2));
        assertTrue(exception.getMessage().contains("intersects"));
    }

    @Test
    public void testNonIntersectingTasks() {
        Task task1 = new Task("Task 1", "Desc 1");
        task1.setStartTime(LocalDateTime.of(2025, 4, 6, 8, 0));
        task1.setDuration(Duration.ofMinutes(60));
        manager.addTask(task1);

        Task task2 = new Task("Task 2", "Desc 2");
        task2.setStartTime(LocalDateTime.of(2025, 4, 6, 9, 30));
        task2.setDuration(Duration.ofMinutes(60));

        assertDoesNotThrow(() -> manager.addTask(task2));
        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
    }

    @Test
    public void testEpicTimeCalculation() {
        Epic epic = new Epic("Epic 1", "Epic desc");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Subtask 1", "Subtask desc 1", epic.getId());
        sub1.setStartTime(LocalDateTime.of(2025, 4, 6, 10, 0));
        sub1.setDuration(Duration.ofMinutes(30));

        Subtask sub2 = new Subtask("Subtask 2", "Subtask desc 2", epic.getId());
        sub2.setStartTime(LocalDateTime.of(2025, 4, 6, 11, 0));
        sub2.setDuration(Duration.ofMinutes(45));

        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        Epic updatedEpic = manager.getEpic(epic.getId());
        assertEquals(LocalDateTime.of(2025, 4, 6, 10, 0), updatedEpic.getStartTime());
        assertEquals(Duration.ofMinutes(75), updatedEpic.getDuration());
        assertEquals(LocalDateTime.of(2025, 4, 6, 11, 45), updatedEpic.getEndTime());
    }

    @Test
    public void testGetPrioritizedTasksOrder() {
        Task task1 = new Task("Task 1", "Desc 1");
        task1.setStartTime(LocalDateTime.of(2025, 4, 6, 9, 0));
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Task 2", "Desc 2");
        task2.setStartTime(LocalDateTime.of(2025, 4, 6, 8, 0));
        task2.setDuration(Duration.ofMinutes(45));

        manager.addTask(task1);
        manager.addTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        // Ожидается, что задача с более ранним startTime (task2) будет первой
        assertEquals(task2, prioritized.get(0));
        assertEquals(task1, prioritized.get(1));
    }
}
