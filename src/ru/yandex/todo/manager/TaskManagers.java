package ru.yandex.todo.manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс менеджера задач.
 */
public interface TaskManagers {

    // ===== Методы для Task =====

    List<Task> getAllTasks();

    Task getTaskById(int id);

    Task createTask(Task task) throws ManagerSaveExceptions;

    Task updateTask(Task task) throws ManagerSaveExceptions;

    void deleteTask(int id);


    // ===== Методы для Subtask =====

    List<Subtask> getAllSubtasks();

    Subtask getSubtaskById(int id);

    Subtask createSubtask(Subtask subtask) throws ManagerSaveExceptions;

    Subtask updateSubtask(Subtask subtask) throws ManagerSaveExceptions;

    void deleteSubtask(int id);


    // ===== Методы для Epic =====

    List<Epic> getAllEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic) throws ManagerSaveExceptions;

    Epic updateEpic(Epic epic) throws ManagerSaveExceptions;

    void deleteEpic(int id);

    List<Subtask> getEpicSubtasks(int epicId);


    // ===== История просмотров и приоритетность =====

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
