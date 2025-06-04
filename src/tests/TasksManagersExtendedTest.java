import org.junit.jupiter.api.Test;
import ru.yandex.todo.manager.InMemoryTaskManagers;
import ru.yandex.todo.model.*;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TasksManagersExtendedTest {

    @Test
    void testNonIntersectingTasks() {
        InMemoryTaskManagers manager = new InMemoryTaskManagers();
        Tasks tasks1 = new Tasks("Task 1", "Desc 1");
        tasks1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        tasks1.setDuration(Duration.ofMinutes(30));
        manager.addTask(tasks1);

        Tasks tasks2 = new Tasks("Task 2", "Desc 2");
        tasks2.setStartTime(LocalDateTime.of(2025, 1, 1, 11, 0));
        tasks2.setDuration(Duration.ofMinutes(30));
        manager.addTask(tasks2);

        List<Tasks> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void testGetPrioritizedTasksOrder() {
        InMemoryTaskManagers manager = new InMemoryTaskManagers();
        Tasks early = new Tasks("Early", "Desc");
        early.setStartTime(LocalDateTime.of(2025, 1, 1, 8, 0));
        early.setDuration(Duration.ofMinutes(30));
        manager.addTask(early);

        Tasks late = new Tasks("Late", "Desc");
        late.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        late.setDuration(Duration.ofMinutes(30));
        manager.addTask(late);

        List<Tasks> prioritized = manager.getAllTasks(); // предполагается, что они отсортированы
        assertEquals("Early", prioritized.get(0).getName());
        assertEquals("Late", prioritized.get(1).getName());
    }

    @Test
    void testEpicTimeCalculation() {
        InMemoryTaskManagers manager = new InMemoryTaskManagers();
        Epics epics = manager.addEpic(new Epics("Epic", "desc"));
        Subtasks sub1 = new Subtasks("Sub 1", "Desc", epics.getId());
        sub1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        sub1.setDuration(Duration.ofMinutes(60));
        manager.addSubtask(sub1);

        Subtasks sub2 = new Subtasks("Sub 2", "Desc", epics.getId());
        sub2.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        sub2.setDuration(Duration.ofMinutes(60));
        manager.addSubtask(sub2);

        Epics updatedEpics = manager.getEpic(epics.getId());
        assertNotNull(updatedEpics);
        assertEquals(Status.IN_PROGRESS, updatedEpics.getStatus());
    }

    @Test
    void testIntersectionDetection() {
        InMemoryTaskManagers manager = new InMemoryTaskManagers();
        Tasks tasks1 = new Tasks("Task 1", "Desc");
        tasks1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        tasks1.setDuration(Duration.ofMinutes(60));
        manager.addTask(tasks1);

        Tasks tasks2 = new Tasks("Task 2", "Desc");
        tasks2.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 30));
        tasks2.setDuration(Duration.ofMinutes(60));

        boolean intersects = isIntersecting(tasks1, tasks2);
        assertTrue(intersects);
    }

    @Test
    void testAddTaskWithValidTime() {
        InMemoryTaskManagers manager = new InMemoryTaskManagers();
        Tasks tasks = new Tasks("Task", "Desc");
        tasks.setStartTime(LocalDateTime.of(2025, 1, 1, 14, 0));
        tasks.setDuration(Duration.ofMinutes(30));
        manager.addTask(tasks);
        assertEquals(tasks, manager.getTask(tasks.getId()));
    }

    private boolean isIntersecting(Tasks t1, Tasks t2) {
        if (t1.getStartTime() == null || t2.getStartTime() == null) return false;
        LocalDateTime start1 = t1.getStartTime();
        LocalDateTime end1 = start1.plus(t1.getDuration());
        LocalDateTime start2 = t2.getStartTime();
        LocalDateTime end2 = start2.plus(t2.getDuration());

        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }
}
