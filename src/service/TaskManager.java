package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

/**
 * Интерфейс менеджера задач. Тесты ожидают методы:
 *  - Task addTask(Task task);
 *  - Task getTask(int id);
 *  - void deleteTask(int id);
 *  - List<Task> getAllTasks();
 *
 *  - Epic addEpic(Epic epic);
 *  - Epic getEpic(int id);
 *  - void deleteEpic(int id);
 *  - List<Epic> getAllEpics();
 *
 *  - Subtask addSubtask(Subtask subtask);
 *  - Subtask getSubtask(int id);
 *  - void deleteSubtask(int id);
 *  - List<Subtask> getAllSubtasks();
 *
 *  - void updateTask(Task task);
 *  - void updateEpic(Epic epic);
 *  - void updateSubtask(Subtask subtask);
 *
 *  - List<Task> getHistory();
 *  - List<Task> getPrioritizedTasks();
 */
public interface TaskManager {

    // ===== Task =====
    Task addTask(Task task);

    Task getTask(int id);

    void updateTask(Task task);

    void deleteTask(int id);

    List<Task> getAllTasks();

    // ===== Epic =====
    Epic addEpic(Epic epic);

    Epic getEpic(int id);

    void updateEpic(Epic epic);

    void deleteEpic(int id);

    List<Epic> getAllEpics();

    // ===== Subtask =====
    Subtask addSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int id);

    List<Subtask> getAllSubtasks();

    // ===== History и приоритетность =====
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
