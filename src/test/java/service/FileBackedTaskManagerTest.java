package service;

import org.junit.jupiter.api.Test;
import taskmanager.model.Task;
import taskmanager.service.FileBackedTaskManager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    void shouldSaveTaskToFile() {
        File file = new File("test-save.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = new Task("Test Task", "Test Description");
        manager.addTask(task);
        assertTrue(file.exists(), "Файл должен быть создан после сохранения задачи");
    }

    @Test
    void shouldHandleEmptyFile() {
        File file = new File("empty.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        assertDoesNotThrow(manager::save);
    }

    @Test
    void shouldLoadSingleTaskFromFile() {
        File file = new File("test-save.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.loadFromFile(file);
        assertEquals(1, manager.getAllTasks().size());
    }
}
