package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

/**
 * Интерфейс менеджера задач. Тесты ожидают методы:
 *  - void addTask(Task task);
 *  - Task getTask(int id);
 *  - void deleteTask(int id);
 *  - List<Task> getAllTasks();
 *
 *  - void addEpic(Epic epic);
 *  - Epic getEpic(int id);
 *  - void deleteEpic(int id);
 *  - List<Epic> getAllEpics();
 *
 *  - void addSubtask(Subtask subtask);
 *  - Subtask getSubtask(int id);
 *  - void deleteSubtask(int id);
 *  - List<Subtask> getAllSubtasks();
 *
 *  - List<Task> getHistory();
 *  - List<Task> getPrioritizedTasks();  (в TestExtended возвращают List + сравнивают индексы)
 */
public interface TaskManager {

    // ===== Task =====
    void addTask(Task task);

    Task getTask(int id);

    void deleteTask(int id);

    List<Task> getAllTasks();

    // ===== Epic =====
    void addEpic(Epic epic);

    Epic getEpic(int id);

    void deleteEpic(int id);

    List<Epic> getAllEpics();

    // ===== Subtask =====
    void addSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    void deleteSubtask(int id);

    List<Subtask> getAllSubtasks();

    // ===== History и приоритетность =====
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
