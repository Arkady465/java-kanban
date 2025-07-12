package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @BeforeEach
    public void initTest() {
        super.manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void add() {
        for (int i = 0; i < 10; i++) {
            Task taskOrigin = new Task("Задача " + i, "Описание " + i);
            manager.addTask(taskOrigin);
        }
        List<Task> addTaskListOfManager = manager.getAllTasks();

        for (Task task : addTaskListOfManager) {
            manager.getTaskById(task.getId());
        }
        List<Task> addTaskListOfHistory = manager.getHistory();

        assertEquals(addTaskListOfManager, addTaskListOfHistory);
    }

    @Test
    void remove() {
        for (int i = 0; i < 10; i++) {
            Task taskOrigin = new Task("Задача " + i, "Описание " + i);
            manager.addTask(taskOrigin);
        }
        List<Task> addTaskListOfManager = manager.getAllTasks();

        for (Task task : addTaskListOfManager) {
            manager.getTaskById(task.getId());
        }

        for (int i = 2; i < addTaskListOfManager.size() ; i = i + 3) {
            Task task = addTaskListOfManager.get(i);
            manager.deleteTaskById(task);
        }

        List<Task> removeTaskListOfManager = manager.getAllTasks();
        List<Task> removeTaskListOfHistory = manager.getHistory();

        assertEquals(removeTaskListOfManager, removeTaskListOfHistory);
    }
}