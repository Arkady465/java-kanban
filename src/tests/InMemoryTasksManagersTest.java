import org.junit.jupiter.api.Test;
import ru.yandex.todo.manager.InMemoryTaskManagers;
import ru.yandex.todo.model.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTasksManagersTest {

    @Test
    void shouldAddAndRetrieveTask() {
        InMemoryTaskManagers manager = new InMemoryTaskManagers();
        Tasks tasks = new Tasks("Test", "Desc");
        manager.addTask(tasks);
        assertEquals(tasks, manager.getTask(tasks.getId()));
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        InMemoryTaskManagers manager = new InMemoryTaskManagers();
        Epics epics = new Epics("Epic 1", "Epic description");
        manager.addEpic(epics);
        assertEquals(epics, manager.getEpic(epics.getId()));
    }

    @Test
    void shouldAddAndRetrieveSubtask() {
        InMemoryTaskManagers manager = new InMemoryTaskManagers();
        Epics epics = manager.addEpic(new Epics("Epic", "Epic desc"));
        Subtasks subtasks = new Subtasks("Sub", "Sub desc", epics.getId());
        manager.addSubtask(subtasks);
        assertEquals(subtasks, manager.getSubtask(subtasks.getId()));
    }

    @Test
    void shouldReturnHistoryOfTasks() {
        InMemoryTaskManagers manager = new InMemoryTaskManagers();
        Tasks tasks1 = manager.addTask(new Tasks("1", "d1"));
        Tasks tasks2 = manager.addTask(new Tasks("2", "d2"));
        Tasks tasks3 = manager.addTask(new Tasks("3", "d3"));
        manager.getTask(tasks1.getId());
        manager.getTask(tasks2.getId());
        manager.getTask(tasks3.getId());
        assertEquals(3, manager.getHistory().size());
    }
}
