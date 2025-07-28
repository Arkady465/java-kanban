package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    @BeforeEach
    void clearAllTasks() {
        if (manager != null) {
            manager.deleteAllTasks();
        }
    }

    @Test
    void generateId() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        manager.addSubtask(subTask);

        assertNotEquals(task.getId(), epic.getId());
        assertNotEquals(task.getId(), subTask.getId());
        assertNotEquals(epic.getId(), subTask.getId());
    }

    @Test
    void addTask() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);

        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);

        assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        manager.addSubtask(subTask);

        assertEquals(1, manager.getAllSubTasks().size());
    }

    @Test
    void deleteAllTasks() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        manager.addSubtask(subTask);

        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks(); // Исправлено здесь

        assertEquals(0, manager.getAllTasks().size());
        assertEquals(0, manager.getAllEpics().size());
        assertEquals(0, manager.getAllSubTasks().size());
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);
        manager.deleteTaskById(task);

        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        manager.deleteEpicById(epic);

        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        manager.addSubtask(subTask);

        manager.deleteSubtaskById(subTask);

        assertEquals(0, manager.getAllSubTasks().size());
    }

    @Test
    void deleteAllSubTasksOfEpic() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        Subtask subTask2 = new Subtask("Задача 4", "Описание задачи 4", epic.getId());
        manager.addSubtask(subTask);
        manager.addSubtask(subTask2);

        manager.deleteAllSubTasksOfEpic(epic);

        assertEquals(0, manager.getAllSubTasks().size());
    }

    @Test
    void getTaskById() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);
        Task task2 = manager.getTaskById(task.getId());

        assertEquals(task.getId(), task2.getId());
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Epic epic2 = manager.getEpicById(epic.getId());

        assertEquals(epic.getId(), epic2.getId());
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        manager.addSubtask(subTask);
        Subtask subTask2 = manager.getSubtaskById(subTask.getId());

        assertEquals(subTask.getId(), subTask2.getId());
    }

    @Test
    void getAllTasks() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);
        Task epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addTask(epic);
        Task subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        manager.addTask(subTask);

        assertEquals(3, manager.getAllTasks().size());
    }

    @Test
    void getAllEpics() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Epic epic2 = new Epic("Задача 3", "Описание задачи 3");
        manager.addEpic(epic2);

        assertEquals(2, manager.getAllEpics().size());
    }

    @Test
    void getAllSubTasks() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        Subtask subTask2 = new Subtask("Задача 4", "Описание задачи 4", epic.getId());
        manager.addSubtask(subTask);
        manager.addSubtask(subTask2);

        assertEquals(2, manager.getAllSubTasks().size());
        assertEquals(subTask, manager.getAllSubTasks().get(0));
        assertEquals(subTask2, manager.getAllSubTasks().get(1));
    }

    @Test
    void getSubTasksForEpic() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        Subtask subTask2 = new Subtask("Задача 4", "Описание задачи 4", epic.getId());
        manager.addSubtask(subTask);
        manager.addSubtask(subTask2);

        assertEquals(2, manager.getSubTasksForEpic(epic.getId()).size());
        assertEquals(subTask, manager.getSubTasksForEpic(epic.getId()).get(0));
        assertEquals(subTask2, manager.getSubTasksForEpic(epic.getId()).get(1));
    }

    @Test
    void getHistory() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        manager.addSubtask(subTask);

        List<Task> taskList = new ArrayList<>();
        taskList.add(manager.getTaskById(task.getId()));
        taskList.add(manager.getEpicById(epic.getId()));
        taskList.add(manager.getSubtaskById(subTask.getId()));

        assertEquals(taskList.get(0), manager.getHistory().get(0));
        assertEquals(taskList.get(1), manager.getHistory().get(1));
        assertEquals(taskList.get(2), manager.getHistory().get(2));
    }

    @Test
    void getPrioritizedTasks() {
        Task task = new Task("Задача 1", "Описание задачи 1", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addTask(task);
        Epic epic = new Epic("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(22), Duration.ofMinutes(30), epic.getId());
        manager.addSubtask(subTask);

        assertEquals(2, manager.getPrioritizedTasks().size());
        assertEquals(task.getTaskType(), manager.getPrioritizedTasks().get(0).getTaskType());
        assertEquals(subTask.getTaskType(), manager.getPrioritizedTasks().get(1).getTaskType());
    }

    @Test
    void updateTask() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        manager.updateTask(task, task2);

        assertEquals(task.getId(), manager.getTaskById(task2.getId()).getId());
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Epic epic2 = new Epic("Задача 3", "Описание задачи 3");
        manager.updateEpic(epic, epic2);

        assertEquals(epic.getId(), manager.getEpicById(epic2.getId()).getId());
    }

    @Test
    void updateSubTask() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        manager.addSubtask(subTask);
        Subtask subTask2 = new Subtask("Задача 4", "Описание задачи 4", epic.getId());
        manager.updateSubTask(subTask, subTask2);

        assertEquals(subTask.getId(), manager.getSubtaskById(subTask2.getId()).getId());
    }

    @Test
    void updateStatus() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Status statusOld = epic.getStatus();
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", Status.IN_PROGRESS, epic.getId());
        manager.addSubtask(subTask);
        manager.updateStatus(epic);

        assertNotEquals(statusOld, manager.getEpicById(epic.getId()).getStatus());
    }
}