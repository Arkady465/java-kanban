package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * InMemory реализация TaskManager:
 */
public class InMemoryTaskManager implements TaskManager {

    final Map<Integer, Task> tasks = new HashMap<>();
    final Map<Integer, Subtask> subtasks = new HashMap<>();
    final Map<Integer, Epic> epics = new HashMap<>();

    final HistoryManager historyManager = new InMemoryHistoryManager();

    final TreeSet<Task> prioritizedSet = new TreeSet<>(
            Comparator.comparing(
                    (Task t) -> {
                        LocalDateTime st = t.getStartTime();
                        return st == null ? LocalDateTime.MAX : st;
                    }
            )
    );

    private int idCounter = 1;

    @Override
    public Task addTask(Task task) {
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
        return task;
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
    public void updateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        int id = task.getId();
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Task with id=" + id + " not found");
        }
        prioritizedSet.remove(tasks.get(id));
        validateTaskTime(task);
        tasks.put(id, task);
        prioritizedSet.add(task);
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

    @Override
    public Epic addEpic(Epic epic) {
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
                for (Subtask s : old.getSubtaskList()) {
                    prioritizedSet.remove(s);
                }
            }
        }
        epics.put(epic.getId(), epic);
        recalcEpicStatus(epic.getId());
        recalcEpicTime(epic.getId());
        return epic;
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
    public void updateEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }
        int id = epic.getId();
        if (!epics.containsKey(id)) {
            throw new IllegalArgumentException("Epic with id=" + id + " not found");
        }
        Epic old = epics.get(id);
        List<Subtask> oldSubs = new ArrayList<>(old.getSubtaskList());
        for (Subtask s : oldSubs) {
            prioritizedSet.remove(s);
        }
        for (Subtask s : oldSubs) {
            epic.addSubtask(s);
        }
        epics.put(id, epic);
        for (Subtask s : oldSubs) {
            prioritizedSet.add(s);
        }
        recalcEpicStatus(id);
        recalcEpicTime(id);
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

    @Override
    public Subtask addSubtask(Subtask subtask) {
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
                Subtask old = subtasks.get(subtask.getId());
                prioritizedSet.remove(old);
                Epic oldEpic = epics.get(old.getEpicID());
                if (oldEpic != null) {
                    oldEpic.removeSubtask(old.getId());
                }
            }
        }
        validateTaskTime(subtask);
        subtasks.put(subtask.getId(), subtask);
        prioritizedSet.add(subtask);
        parentEpic.addSubtask(subtask);
        recalcEpicStatus(parentEpic.getId());
        recalcEpicTime(parentEpic.getId());
        return subtask;
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
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }
        int id = subtask.getId();
        if (!subtasks.containsKey(id)) {
            throw new IllegalArgumentException("Subtask with id=" + id + " not found");
        }
        Subtask old = subtasks.get(id);
        int oldEpicId = old.getEpicID();
        int newEpicId = subtask.getEpicID();
        if (oldEpicId != newEpicId) {
            Epic oldEpic = epics.get(oldEpicId);
            if (oldEpic != null) {
                oldEpic.removeSubtask(id);
            }
            Epic newEpic = epics.get(newEpicId);
            if (newEpic == null) {
                throw new IllegalArgumentException("Epic with id=" + newEpicId + " not found");
            }
            newEpic.addSubtask(subtask);
        } else {
            Epic parentEpic = epics.get(oldEpicId);
            if (parentEpic != null) {
                parentEpic.removeSubtask(id);
                parentEpic.addSubtask(subtask);
            }
        }
        prioritizedSet.remove(old);
        validateTaskTime(subtask);
        subtasks.put(id, subtask);
        prioritizedSet.add(subtask);
        recalcEpicStatus(oldEpicId);
        recalcEpicTime(oldEpicId);
        if (oldEpicId != newEpicId) {
            recalcEpicStatus(newEpicId);
            recalcEpicTime(newEpicId);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed != null) {
            prioritizedSet.remove(removed);
            historyManager.remove(id);
            int eid = removed.getEpicID();
            Epic epic = epics.get(eid);
            if (epic != null) {
                epic.removeSubtask(id);
                recalcEpicStatus(eid);
                recalcEpicTime(eid);
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedSet);
    }

    @Override
    public boolean hasIntersection(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
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
                return true;
            }
        }
        return false;
    }

    private void validateTaskTime(Task newTask) {
        if (hasIntersection(newTask)) {
            throw new RuntimeException("Time interval conflict");
        }
    }

    private void recalcEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        List<Subtask> list = epic.getSubtaskList();
        if (list.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        for (Subtask s : list) {
            Status st = s.getStatus();
            if (st != Status.NEW) {
                allNew = false;
            }
            if (st != Status.DONE) {
                allDone = false;
            }
        }
        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void recalcEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        List<Subtask> list = epic.getSubtaskList();
        if (list.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            return;
        }
        LocalDateTime minStart = null;
        long totalMinutes = 0;
        for (Subtask s : list) {
            if (s.getStartTime() != null && s.getDuration() != null) {
                LocalDateTime sStart = s.getStartTime();
                if (minStart == null || sStart.isBefore(minStart)) {
                    minStart = sStart;
                }
                totalMinutes += s.getDuration().toMinutes();
            }
        }
        if (minStart == null) {
            epic.setStartTime(null);
            epic.setDuration(null);
        } else {
            epic.setStartTime(minStart);
            epic.setDuration(Duration.ofMinutes(totalMinutes));
        }
    }
}
