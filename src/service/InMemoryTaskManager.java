package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Реализация TaskManager “InMemory”:
 *  - Хранит Task, Epic, Subtask в HashMap<Integer, …>;
 *  - Генерирует id = 1,2,3… при добавлении с помощью idCounter++;
 *  - Поддерживает историю просмотров через InMemoryHistoryManager;
 *  - Проверяет пересечения по времени (startTime + duration) при добавлении/обновлении;
 *  - Хранит приоритет (List<Task>), упорядочивая по startTime (раньше – впереди).
 */
public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private final TreeSet<Task> prioritizedSet = new TreeSet<>(
            Comparator.comparing((Task t) -> {
                LocalDateTime st = t.getStartTime();
                return (st == null) ? LocalDateTime.MAX : st;
            })
    );
    private int idCounter = 1;

    // ===== Task =====
    @Override
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getId() == 0) {
            task.setId(idCounter++);
        } else {
            if (tasks.containsKey(task.getId())) {
                prioritizedSet.remove(tasks.get(task.getId()));
            }
        }
        validateTaskTime(task);
        tasks.put(task.getId(), task);
        prioritizedSet.add(task);
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void deleteTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            prioritizedSet.remove(removed);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        for (Task t : tasks.values()) {
            historyManager.add(t);
        }
        return new ArrayList<>(tasks.values());
    }

    // ===== Epic =====
    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }
        if (epic.getId() == 0) {
            epic.setId(idCounter++);
        } else {
            if (epics.containsKey(epic.getId())) {
                Epic old = epics.get(epic.getId());
                for (Subtask s : old.getSubtaskList()) {
                    epic.addSubtask(s);
                }
            }
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void deleteEpic(int id) {
        Epic removed = epics.remove(id);
        if (removed != null) {
            for (Subtask s : removed.getSubtaskList()) {
                subtasks.remove(s.getId());
                prioritizedSet.remove(s);
                historyManager.remove(s.getId());
            }
            removed.clearSubtasks();
            historyManager.remove(id);
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        for (Epic e : epics.values()) {
            historyManager.add(e);
        }
        return new ArrayList<>(epics.values());
    }

    // ===== Subtask =====
    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }
        Epic parentEpic = epics.get(subtask.getEpicID());
        if (parentEpic == null) {
            throw new IllegalArgumentException("Epic with id=" + subtask.getEpicID() + " not found");
        }
        if (subtask.getId() == 0) {
            subtask.setId(idCounter++);
        } else {
            if (subtasks.containsKey(subtask.getId())) {
                prioritizedSet.remove(subtasks.get(subtask.getId()));
                parentEpic.removeSubtask(subtask.getId());
            }
        }
        validateTaskTime(subtask);
        subtasks.put(subtask.getId(), subtask);
        prioritizedSet.add(subtask);
        parentEpic.addSubtask(subtask);
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask sub = subtasks.get(id);
        if (sub != null) {
            historyManager.add(sub);
        }
        return sub;
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed != null) {
            prioritizedSet.remove(removed);
            historyManager.remove(id);
            Epic parentEpic = epics.get(removed.getEpicID());
            if (parentEpic != null) {
                parentEpic.removeSubtask(id);
            }
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        for (Subtask s : subtasks.values()) {
            historyManager.add(s);
        }
        return new ArrayList<>(subtasks.values());
    }

    // ===== History & Prioritized =====
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedSet);
    }

    // ===== Вспомогательные методы =====

    private void validateTaskTime(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return;
        }
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        for (Task existing : prioritizedSet) {
            if (existing.getStartTime() == null || existing.getDuration() == null) {
                continue;
            }
            LocalDateTime existStart = existing.getStartTime();
            LocalDateTime existEnd = existing.getEndTime();
            boolean overlap = newStart.isBefore(existEnd) && existStart.isBefore(newEnd);
            if (overlap && existing.getId() != newTask.getId()) {
                throw new RuntimeException("Duplicate time interval with task id=" + existing.getId());
            }
        }
    }
}
