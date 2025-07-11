package taskManager.service;

import taskManager.model.*;

import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void deleteTask(int id);

    void clearAllTasks();

    void clearAllEpics();

    void clearAllSubtasks();

    void deleteEpic(int id);

    void deleteSubtask(int id);

    List<Subtask> getSubtasksOfEpic(int epicId);

    Task updateTask(Task task);

    Subtask updateSubtask(Subtask subtask);

    Epic updateEpic(Epic epic);

    List<Subtask> getSubtasksForEpic(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}

