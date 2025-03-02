package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import service.HistoryManager;
import service.Managers;
import java.util.List;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldAddTasksToHistory() {
        Task task = new Task("Task 1", "Description 1");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "History should contain 1 task");
        assertEquals(task, history.get(0), "First task in history should match");
    }

    @Test
    void shouldNotExceedHistoryLimit() {
        for (int i = 1; i <= 12; i++) {
            historyManager.add(new Task("Task " + i, "Description " + i));
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "History should only contain the last 10 tasks");
        assertEquals("Task 3", history.get(0).getName(), "First task in history should match the 3rd task added");
        assertEquals("Task 12", history.get(9).getName(), "Last task in history should match the last task added");
    }
}
