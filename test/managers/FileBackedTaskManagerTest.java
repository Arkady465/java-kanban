package managers;

import exception.ManagerLoadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    private File getTempFile() throws IOException {
        tempFile = File.createTempFile("test_tasks", ".csv");
        tempFile.deleteOnExit();
        return tempFile;
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        manager = new FileBackedTaskManager(Manager.getDefaultHistory(), getTempFile());
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(Manager.getDefaultHistory(), tempFile);
        assertTrue(loaded.getAllTasks().isEmpty());
    }

    @Test
    void save() {
        Task task = new Task(
                "Задача 1",
                "Описание задачи 1",
                Status.NEW,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                Duration.ofMinutes(60));
        manager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(Manager.getDefaultHistory(), tempFile);
        Task loaded = loadedManager.getTaskById(task.getId());

        assertNotNull(loaded);
        assertEquals(task.getName(), loaded.getName());
        assertEquals(task.getStartTime(), loaded.getStartTime());
    }

    @Test
    void fromString() {
        String csv = "1,TASK,Задача 2,NEW,Описание задачи 2,null,60,\n";
        Task task = FileBackedTaskManager.fromString(csv);
        assertNotNull(task);
        assertEquals("Задача 2", task.getName());
        assertEquals("Описание задачи 2", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    void fromHistoryString() {
        for (int i = 0; i < 3; i++) {
            Task taskOrigin = new Task("Задача " + i, "Описание " + i);
            manager.addTask(taskOrigin);
        }
        List<Task> addTaskListOfManager = manager.getAllTasks();

        for (Task task : addTaskListOfManager) {
            manager.getTaskById(task.getId());
        }
        List<Task> taskList = manager.getHistory();

        String historyLine = "1,2,3";
        List<Task> history = FileBackedTaskManager.fromHistoryString(historyLine, manager);

        assertEquals(taskList, history);
    }

    @Test
    void loadFromBadFile() {
        File file = new File("bad.csv");

        assertDoesNotThrow(() -> {
            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(new InMemoryHistoryManager(), file);
            assertNotNull(manager);
            assertTrue(manager.getAllTasks().isEmpty());
        });
    }
}