import model.Task;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    void shouldSaveTaskToFile() {
        File file = new File("test-save.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = new Task("Test", "Description");
        manager.addTask(task);

        assertTrue(file.exists(), "Файл должен быть создан после сохранения задачи");
    }

    @Test
    void shouldLoadSingleTaskFromFile() {
        File file = new File("test-save.csv");
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        List<Task> loadedTasks = loaded.getAllTasks();

        assertEquals(1, loadedTasks.size(), "Должна быть одна загруженная задача");
        assertEquals("Test", loadedTasks.get(0).getName(), "Имя задачи должно совпадать");
    }

    @Test
    void shouldHandleEmptyFile() {
        File file = new File("empty.csv");
        try {
            file.createNewFile(); // создаёт файл, если его нет
        } catch (IOException e) {
            fail("Не удалось создать файл: " + e.getMessage());
        }

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loaded.getAllTasks().isEmpty(), "Задачи должны быть пустыми при загрузке из пустого файла");
    }
}
