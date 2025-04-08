package service;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Comparator;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int idCounter = 1;

    // Храним задачи с заданным startTime в отсортированном порядке.
    // Используем интерфейс SortedSet и статические методы Comparator для создания компаратора,
    // который сравнивает задачи по startTime с учетом null-значений (nullsLast),
    // а при равенстве - по id.
    protected final SortedSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
                    .thenComparing(Task::getId)
    );

    private int generateId() {
        return idCounter++;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            task = epics.get(id);
        }
        if (task == null) {
            task = subtasks.get(id);
        }
        if (task != null) {
            historyManager.add(task);
        }
        return task;
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
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> all = new ArrayList<>();
        all.addAll(tasks.values());
        all.addAll(epics.values());
        all.addAll(subtasks.values());
        return all;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task addTask(Task task) {
        task.setId(generateId());
        checkIntersection(task);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        if (epic.getStartTime() != null) {
            prioritizedTasks.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(generateId());
        checkIntersection(subtask);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
        }
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        return subtask;
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            tasks.remove(id);
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearAllTasks() {
        for (Integer id : new ArrayList<>(tasks.keySet())) {
            historyManager.remove(id);
        }
        tasks.clear();
        prioritizedTasks.removeIf(t -> t.getType() == TaskType.TASK);
    }

    @Override
    public void clearAllEpics() {
        for (Integer id : new ArrayList<>(epics.keySet())) {
            historyManager.remove(id);
        }
        epics.clear();
        clearAllSubtasks();
        prioritizedTasks.removeIf(t -> t.getType() == TaskType.EPIC);
    }

    @Override
    public void clearAllSubtasks() {
        for (Integer id : new ArrayList<>(subtasks.keySet())) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
        prioritizedTasks.removeIf(t -> t.getType() == TaskType.SUBTASK);
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Subtask subtask : epic.getSubtaskList()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
            epic.clearSubtasks();
            epics.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(epic);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicID());
            if (epic != null) {
                epic.removeSubtask(subtask.getId());
            }
            subtasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubtaskList().stream().toList();
        }
        return new ArrayList<>();
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            prioritizedTasks.removeIf(t -> t.getId() == task.getId());
            if (task.getStartTime() != null) {
                checkIntersection(task);
                prioritizedTasks.add(task);
            }
            return task;
        }
        return null;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicID());
            if (epic != null) {
                updateEpicStatus(epic);
            }
            prioritizedTasks.removeIf(t -> t.getId() == subtask.getId());
            if (subtask.getStartTime() != null) {
                checkIntersection(subtask);
                prioritizedTasks.add(subtask);
            }
            return subtask;
        }
        return null;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
            prioritizedTasks.removeIf(t -> t.getId() == epic.getId());
            if (epic.getStartTime() != null) {
                prioritizedTasks.add(epic);
            }
            return epic;
        }
        return null;
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int epicId) {
        return getSubtasksOfEpic(epicId);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Новый метод: получение списка задач по приоритету (отсортирован по startTime)
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // Проверка пересечения двух задач (если у обеих заданы startTime и duration)
    public boolean tasksIntersect(Task t1, Task t2) {
        if (t1.getStartTime() == null || t1.getDuration() == null ||
                t2.getStartTime() == null || t2.getDuration() == null) {
            return false;
        }
        LocalDateTime start1 = t1.getStartTime();
        LocalDateTime end1 = t1.getEndTime();
        LocalDateTime start2 = t2.getStartTime();
        LocalDateTime end2 = t2.getEndTime();
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    // При добавлении или обновлении проверяем, пересекается ли задача с уже существующими
    public void checkIntersection(Task task) {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return;
        }
        boolean intersect = getPrioritizedTasks().stream()
                .filter(t -> t.getId() != task.getId())
                .anyMatch(t -> tasksIntersect(task, t));
        if (intersect) {
            throw new RuntimeException("Task time intersects with another task: " + task);
        }
    }

    // Пересчёт статуса и временных полей эпика на основе его подзадач
    protected void updateEpicStatus(Epic epic) {
        List<Subtask> subtaskList = epic.getSubtaskList();
        if (subtaskList.isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        LocalDateTime minStart = null;
        LocalDateTime maxEnd = null;
        Duration totalDuration = Duration.ZERO;
        for (Subtask subtask : subtaskList) {
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                LocalDateTime subStart = subtask.getStartTime();
                LocalDateTime subEnd = subtask.getEndTime();
                if (minStart == null || subStart.isBefore(minStart)) {
                    minStart = subStart;
                }
                if (maxEnd == null || subEnd.isAfter(maxEnd)) {
                    maxEnd = subEnd;
                }
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }
        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        epic.setStartTime(minStart);
        epic.setDuration(totalDuration);
        epic.setEndTime(maxEnd);
    }
}
