package ru.yandex.todo.manager;

import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс менеджера задач.
 */
public interface TaskManager {

    // ===== Методы для Task =====

    List<Task> getTasks();

    Task getTaskById(int id);

    Task createTask(Task task) throws ManagerSaveException;

    Task updateTask(Task task) throws ManagerSaveException;

    void deleteTask(int id);


    // ===== Методы для Subtask =====

    List<Subtask> getSubtasks();

    Subtask getSubtaskById(int id);

    Subtask createSubtask(Subtask subtask) throws ManagerSaveException;

    Subtask updateSubtask(Subtask subtask) throws ManagerSaveException;

    void deleteSubtask(int id);


    // ===== Методы для Epic =====

    List<Epic> getEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic) throws ManagerSaveException;

    Epic updateEpic(Epic epic) throws ManagerSaveException;

    void deleteEpic(int id);

    List<Subtask> getEpicSubtasks(int epicId);


    // ===== History & Prioritized =====

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
