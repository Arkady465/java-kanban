package task;

import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManagerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void initTest() {
        super.manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void getId() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);

        assertNotNull(manager.getTaskById(task.getId()));
    }

    @Test
    void testEqualsId(){
        Task task = new Task("Задача 2", "Описание задачи 2");
        Task task2 = new Task("Задача 3", "Описание задачи 3");
        task.setId(1);
        task2.setId(1);

        assertEquals(task, task2);
    }

    @Test
    void testNotEqualsId(){
        Task task = new Task("Задача 2", "Описание задачи 2");
        Task task2 = new Epic("Задача 3", "Описание задачи 3");
        task.setId(1);
        task2.setId(2);

        assertNotEquals(task, task2);
    }

    @Test
    void setId() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(taskOrigin);
        int idOrigin = manager.getTaskById(taskOrigin.getId()).getId();

        Task taskNew = manager.getTaskById(idOrigin);
        taskNew.setId(taskNew.getId() + 1000);

        assertNotEquals(idOrigin, taskNew.getId());
    }

    @Test
    void getName() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(taskOrigin);
        String nameOrigin = manager.getTaskById(taskOrigin.getId()).getName();

        assertNotNull(nameOrigin);
    }

    @Test
    void setName() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(taskOrigin);
        String nameOrigin = manager.getTaskById(taskOrigin.getId()).getName();

        Task taskNew = manager.getTaskById(taskOrigin.getId());
        taskNew.setName("Задача 2");

        assertNotEquals(nameOrigin, taskNew.getName());
    }

    @Test
    void getDescription() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(taskOrigin);
        String descriptionOrigin = manager.getTaskById(taskOrigin.getId()).getDescription();

        assertNotNull(descriptionOrigin);
    }

    @Test
    void setDescription() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(taskOrigin);
        String descriptionOrigin = manager.getTaskById(taskOrigin.getId()).getDescription();

        Task taskNew = manager.getTaskById(taskOrigin.getId());
        taskNew.setDescription("Описание задачи 2");

        assertNotEquals(descriptionOrigin, taskNew.getDescription());
    }

    @Test
    void getStatus() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(taskOrigin);
        Status status = manager.getTaskById(taskOrigin.getId()).getStatus();

        assertNotNull(status);
    }

    @Test
    void setStatus() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(taskOrigin);
        Status status = manager.getTaskById(taskOrigin.getId()).getStatus();

        Task taskNew = manager.getTaskById(taskOrigin.getId());
        taskNew.setStatus(Status.DONE);

        assertNotEquals(status, taskNew.getStatus());
    }

    @Test
    void getTaskType() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(taskOrigin);
        TaskType taskType = manager.getTaskById(taskOrigin.getId()).getTaskType();

        assertNotNull(taskType);
    }

    @Test
    void getStartTime() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(30));
        manager.addTask(taskOrigin);
        LocalDateTime startTime = manager.getTaskById(taskOrigin.getId()).getStartTime();

        assertNotNull(startTime);
    }

    @Test
    void setStartTime() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(30));
        manager.addTask(taskOrigin);
        LocalDateTime startTime = manager.getTaskById(taskOrigin.getId()).getStartTime();

        Task taskNew = manager.getTaskById(taskOrigin.getId());
        taskNew.setStartTime(LocalDateTime.now().plusMinutes(60));

        assertNotEquals(startTime, taskNew.getStartTime());
    }

    @Test
    void getDuration() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(taskOrigin);
        Duration duration = manager.getTaskById(taskOrigin.getId()).getDuration();

        assertNotNull(duration);
    }

    @Test
    void setDuration() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(30));
        manager.addTask(taskOrigin);
        Duration duration = manager.getTaskById(taskOrigin.getId()).getDuration();

        Task taskNew = manager.getTaskById(taskOrigin.getId());
        taskNew.setDuration(Duration.ofMinutes(60));

        assertNotEquals(duration, taskNew.getDuration());
    }

    @Test
    void getEndTime() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(taskOrigin);
        Duration duration = manager.getTaskById(taskOrigin.getId()).getDuration();

        assertNotNull(duration);
    }

    @Test
    void setTaskType() {
        Task taskOrigin = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(taskOrigin);
        TaskType taskType = manager.getTaskById(taskOrigin.getId()).getTaskType();

        Task taskNew = manager.getTaskById(taskOrigin.getId());
        taskNew.setTaskType(TaskType.SUBTASK);

        assertNotEquals(taskType, taskNew.getTaskType());
    }
}