package managers;

import task.Task;
import task.Epic;
import task.Subtask;

import java.util.List;

public interface TaskManager {

    int generateId();

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void deleteTaskById(Task task);

    void deleteEpicById(Epic epic);

    void deleteSubtaskById(Subtask subtask);

    void deleteAllSubTasksOfEpic(Epic epic);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubTasks();

    List<Subtask> getSubTasksForEpic(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    void updateTask(Task oldTask, Task newTask);

    void updateEpic(Epic oldEpic, Epic newEpic);

    void updateSubTask(Subtask oldSubtaskTask, Subtask newSubtaskTask);

    void updateStatus(Epic epic);

    boolean isOverlapTask(Task newTask);

    @Override
    String toString();
}