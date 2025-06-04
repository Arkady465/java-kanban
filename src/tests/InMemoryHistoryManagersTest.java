import org.junit.jupiter.api.Test;
import ru.yandex.todo.manager.InMemoryHistoryManagers;
import ru.yandex.todo.model.Tasks;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagersTest {

    @Test
    void shouldAddTasksToHistory() {
        InMemoryHistoryManagers manager = new InMemoryHistoryManagers();
        Tasks tasks = new Tasks("Test", "Desc");
        tasks.setId(1);
        manager.add(tasks);
        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void shouldNotExceedHistoryLimit() {
        InMemoryHistoryManagers manager = new InMemoryHistoryManagers();
        for (int i = 0; i < 15; i++) {
            Tasks tasks = new Tasks("Task " + i, "Desc");
            tasks.setId(i);
            manager.add(tasks);
        }
        assertEquals(10, manager.getHistory().size(), "History should only contain the last 10 tasks");
    }
}
