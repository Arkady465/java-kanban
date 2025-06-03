package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

/**
 * Интерфейс менеджера задач. Тесты ожидают:
 *   Task addTask(Task);
 *   Task getTask(int);
 *   void updateTask(Task);
 *   void deleteTask(int);
 *   List<Task> getAllTasks();
 *
 *   Epic addEpic(Epic);
 *   Epic getEpic(int);
 *   void updateEpic(Epic);
 *   void deleteEpic(int);
 *   List<Epic> getAllEpics();
 *
 *   Subtask addSubtask(Subtask);
 *   Subtask getSubtask(int);
 *   void updateSubtask(Subtask);
 *   void deleteSubtask(int);
 *   List<Subtask> getAllSubtasks();
 *
 *   List<Task> getHistory();
 *   List<Task> getPrioritizedTasks();
 *
 *   // Дополнительно для тестов:
 *   boolean hasIntersection(Task task);
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

    // ===== History and Prioritized =====
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    List<Subtask> getEpicSubtasks(int epicId);

    // ===== Extra method for intersection test =====
    boolean hasIntersection(Task task);
}
