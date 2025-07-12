package task;

import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManagerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void initTest() {
        super.manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void testEqualsId(){
        Subtask subtask = new Subtask("Задача 2", "Описание задачи 2", 1);
        Subtask subtask2 = new Subtask("Задача 3", "Описание задачи 3", 1);
        subtask.setId(1);
        subtask2.setId(1);

        assertEquals(subtask, subtask2);
    }

    @Test
    void testNotEqualsId(){
        Subtask subtask = new Subtask("Задача 2", "Описание задачи 2", 1);
        Subtask subtask2 = new Subtask("Задача 3", "Описание задачи 3", 1);
        subtask.setId(1);
        subtask2.setId(2);

        assertNotEquals(subtask, subtask2);
    }

    @Test
    void getEpicId() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        manager.addSubtask(subtask);

        int id = manager.getSubtaskById(subtask.getId()).getId();

        assertNotNull(id);

    }
}