import model.Task;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    void shouldSaveAndLoadSingleTask() {
        File file = new File("test.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = new Task("Test", "Description");
        manager.addTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        List<Task> loadedTasks = loaded.getAllTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("Test", loadedTasks.get(0).getName());
    }

    @Test
    void shouldHandleEmptyFile() {
        File file = new File("empty.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loaded.getAllTasks().isEmpty());
    }
}
