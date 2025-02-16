package service;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.List;

public interface TaskManager {

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Task getTask(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getSubtasksOfEpic(int epicID);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    void deleteTask(int id);

    void clearAllTasks();

    void clearAllEpics();

    void clearAllSubtasks();

    List<Task> getHistory();
}
