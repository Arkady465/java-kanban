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
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private int idCounter = 1;

    private final HistoryManager historyManager = new InMemoryHistoryManager();

    // Для приоритетов (отсортированы по startTime; null иду́т последними)
    private final Set<Task> prioritizedSet = new TreeSet<>(
            Comparator.comparing((Task t) -> {
                LocalDateTime st = t.getStartTime();
                return st == null ? LocalDateTime.MAX : st;
            })
    );

    @Override
    public Task addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task не может быть null");
        }
        if (task.getId() == 0) {
            task.setId(idCounter++);
        } else {
            // обновление существующей: если есть, удаляем из приоритетов старое
            if (tasks.containsKey(task.getId())) {
                Task old = tasks.get(task.getId());
                prioritizedSet.remove(old);
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
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task не найден");
        }
        // удалить старую запись из приоритета
        Task old = tasks.get(task.getId());
        prioritizedSet.remove(old);

        validateTaskTime(task);
        tasks.put(task.getId(), task);
        prioritizedSet.add(task);
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
    public Epic addEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic не может быть null");
        }
        if (epic.getId() == 0) {
            epic.setId(idCounter++);
        } else {
            // при обновлении надо перенести дочерние Subtask в новый экземпляр
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
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("Epic не найден");
        }
        Epic old = epics.get(epic.getId());
        // при обновлении пересчитываем только поля, которые поменялись в самом epic, субзадачи остаются
        epic.getSubtaskList().clear();
        for (Subtask s : old.getSubtaskList()) {
            epic.addSubtask(s);
        }
        epics.put(epic.getId(), epic);
        recalcEpicStatus(epic.getId());
        recalcEpicTime(epic.getId());
        return epic;
    }

    @Override
    public void deleteEpic(int id) {
        Epic removed = epics.remove(id);
        if (removed != null) {
            // удалить все связанные Subtask из maps и из приоритетов
            for (Subtask s : removed.getSubtaskList()) {
                subtasks.remove(s.getId());
                prioritizedSet.remove(s);
                historyManager.remove(s.getId());
            }
            historyManager.remove(id);
        }
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask не может быть null");
        }
        Epic parentEpic = epics.get(subtask.getEpicID());
        if (parentEpic == null) {
            throw new IllegalArgumentException("Epic с id=" + subtask.getEpicID() + " не найден");
        }
        if (subtask.getId() == 0) {
            subtask.setId(idCounter++);
        } else {
            if (subtasks.containsKey(subtask.getId())) {
                Subtask old = subtasks.get(subtask.getId());
                prioritizedSet.remove(old);
                parentEpic.removeSubtask(old.getId());
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
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Subtask не найден");
        }
        Subtask old = subtasks.get(subtask.getId());
        Epic oldEpic = epics.get(old.getEpicID());
        if (oldEpic != null) {
            oldEpic.removeSubtask(old.getId());
        }
        prioritizedSet.remove(old);

        Epic parentEpic = epics.get(subtask.getEpicID());
        if (parentEpic == null) {
            throw new IllegalArgumentException("Новое Epic с id=" + subtask.getEpicID() + " не найден");
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
    public void deleteSubtask(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed != null) {
            prioritizedSet.remove(removed);
            Epic epic = epics.get(removed.getEpicID());
            if (epic != null) {
                epic.removeSubtask(removed.getId());
                recalcEpicStatus(epic.getId());
                recalcEpicTime(epic.getId());
            }
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedSet);
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Epic с id=" + epicId + " не найден");
        }
        return new ArrayList<>(epic.getSubtaskList());
    }

    /* === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ EPIC === */

    private void recalcEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> sublist = epic.getSubtaskList();
        if (sublist.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        for (Subtask s : sublist) {
            if (s.getStatus() != Status.NEW) allNew = false;
            if (s.getStatus() != Status.DONE) allDone = false;
        }
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void recalcEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> sublist = epic.getSubtaskList();
        if (sublist.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            return;
        }
        LocalDateTime minStart = null;
        long totalMinutes = 0;
        for (Subtask s : sublist) {
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

    private void validateTaskTime(Task task) {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return;
        }
        LocalDateTime newStart = task.getStartTime();
        LocalDateTime newEnd = task.getEndTime();
        for (Task t : prioritizedSet) {
            if (t.getStartTime() == null || t.getDuration() == null) {
                continue;
            }
            LocalDateTime existStart = t.getStartTime();
            LocalDateTime existEnd = t.getEndTime();
            boolean overlap = newStart.isBefore(existEnd) && existStart.isBefore(newEnd);
            if (overlap) {
                throw new IllegalArgumentException("Пересечение по времени c Task id=" + t.getId());
            }
        }
    }
}
