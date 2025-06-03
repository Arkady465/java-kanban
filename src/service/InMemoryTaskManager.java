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
 *   - Хранит все Task, Epic, Subtask в отдельных HashMap<id, …>;
 *   - Генерирует idCounter++;
 *   - Следит за историей через InMemoryHistoryManager;
 *   - Проверяет пересечение по времени до вставки в приоритетный TreeSet;
 *   - Пересчитывает статус и время эпика при изменениях подзадач.
 */
public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    private final HistoryManager historyManager = new InMemoryHistoryManager();

    // TreeSet для упорядочивания по startTime (null → к концу)
    private final TreeSet<Task> prioritizedSet = new TreeSet<>(
            Comparator.comparing(
                    (Task t) -> {
                        LocalDateTime st = t.getStartTime();
                        return st == null ? LocalDateTime.MAX : st;
                    }
            )
    );

    private int idCounter = 1;

    // ===== Task =====

    @Override
    public Task addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getId() == 0) {
            task.setId(idCounter++);
        } else {
            // если ид уже существует, убираем старую версию из приоритета
            if (tasks.containsKey(task.getId())) {
                prioritizedSet.remove(tasks.get(task.getId()));
            }
        }
        // Сначала проверяем на пересечение
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
        // Удаляем старую версию из приоритета
        prioritizedSet.remove(tasks.get(id));
        // Проверяем новое время
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
        // Каждый раз, когда просят “все таски”, кидаем их в историю
        for (Task t : tasks.values()) {
            historyManager.add(t);
        }
        return new ArrayList<>(tasks.values());
    }

    // ===== Epic =====

    @Override
    public Epic addEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }
        if (epic.getId() == 0) {
            epic.setId(idCounter++);
        } else {
            // если эпик уже был, переносим старые подзадачи в новый объект
            if (epics.containsKey(epic.getId())) {
                Epic old = epics.get(epic.getId());
                for (Subtask s : old.getSubtaskList()) {
                    epic.addSubtask(s);
                }
                // Удаляем старые подзадачи из приоритета (они позже вставятся заново)
                for (Subtask s : old.getSubtaskList()) {
                    prioritizedSet.remove(s);
                }
            }
        }
        epics.put(epic.getId(), epic);
        // После вставки эпика пересчитываем его статус (после загрузки подзадач — отдельно)
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
        // Собираем список старых подзадач, чтобы потом их перезаписать
        List<Subtask> oldSubs = new ArrayList<>(old.getSubtaskList());
        // Убираем старые подзадачи из приоритета
        for (Subtask s : oldSubs) {
            prioritizedSet.remove(s);
        }
        // Переносим все старые сабтаски в новый эпик
        for (Subtask s : oldSubs) {
            epic.addSubtask(s);
        }
        epics.put(id, epic);
        // Заново вставляем подзадачи в приоритет
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

    // ===== Subtask =====

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
                // Убираем старую версию из приоритета и убираем связь с эпиком
                Subtask old = subtasks.get(subtask.getId());
                prioritizedSet.remove(old);
                Epic oldEpic = epics.get(old.getEpicID());
                if (oldEpic != null) {
                    oldEpic.removeSubtask(old.getId());
                }
            }
        }
        // Проверяем пересечение по времени
        validateTaskTime(subtask);

        subtasks.put(subtask.getId(), subtask);
        prioritizedSet.add(subtask);
        parentEpic.addSubtask(subtask);

        // Пересчитаем статус и время эпика
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

        // Если эпик меняется, убираем связь со старым эпиком
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
            // Если тот же эпик, просто удаляем старую версию из списка эпика
            Epic parentEpic = epics.get(oldEpicId);
            if (parentEpic != null) {
                parentEpic.removeSubtask(id);
                parentEpic.addSubtask(subtask);
            }
        }

        // Убираем старую подзадачу из приоритета
        prioritizedSet.remove(old);
        // Проверяем пересечение по времени новой подзадачи
        validateTaskTime(subtask);
        // Переписываем
        subtasks.put(id, subtask);
        prioritizedSet.add(subtask);

        // Пересчитаем статус/время для затронутых эпиков
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

    /**
     * Проверяет, что новая задача/подзадача не пересекаются с уже вставленными.
     * Бросает RuntimeException, если найдена коллизия.
     */
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
                throw new RuntimeException("Time interval conflict with task id=" + existing.getId());
            }
        }
    }

    /**
     * Пересчитывает статус эпика:
     *   - ни одной подзадачи → NEW
     *   - все NEW → NEW
     *   - все DONE → DONE
     *   - иначе → IN_PROGRESS
     */
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

    /**
     * Пересчитывает время эпика:
     *   startTime = min(startTime всех подзадач),
     *   duration = сумма(duration всех подзадач).
     * Если нет подзадач, сбрасывает оба поля в null.
     */
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
