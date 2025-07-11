package service;

import org.junit.jupiter.api.Test;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagersTest {

    @Test
    void shouldAddAndRetrieveTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Test", "Desc");
        manager.addTask(task);
        assertEquals(task, manager.getTask(task.getId()));
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1", "Epic description");
        manager.addEpic(epic);
        assertEquals(epic, manager.getEpic(epic.getId()));
    }

    @Test
    void shouldAddAndRetrieveSubtask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = manager.addEpic(new Epic("Epic", "Epic desc"));
        Subtask subtask = new Subtask("Sub", "Sub desc", epic.getId());
        manager.addSubtask(subtask);
        assertEquals(subtask, manager.getSubtask(subtask.getId()));
    }

    @Test
    void shouldReturnHistoryOfTasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = manager.addTask(new Task("1", "d1"));
        Task task2 = manager.addTask(new Task("2", "d2"));
        Task task3 = manager.addTask(new Task("3", "d3"));
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        assertEquals(3, manager.getHistory().size());
    }
}
