import org.junit.jupiter.api.Test;
import ru.yandex.todoo.manager.InMemoryHistoryManager;
import ru.yandex.todo.model.Task;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    @Test
    void shouldAddTasksToHistory() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();
        Task task = new Task("Test", "Desc");
        task.setId(1);
        manager.add(task);
        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void shouldNotExceedHistoryLimit() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();
        for (int i = 0; i < 15; i++) {
            Task task = new Task("Task " + i, "Desc");
            task.setId(i);
            manager.add(task);
        }
        assertEquals(10, manager.getHistory().size(), "History should only contain the last 10 tasks");
    }
}
