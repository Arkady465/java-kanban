package ru.yandex.todo.manager;

import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Status;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Простая InMemory‐реализация TaskManager.
 * Содержит:
 *   - Хранение задач, подзадач и эпиков в HashMap
 *   - Уникальные id через счётчик
 *   - Расчёт статусов эпиков (NEW, IN_PROGRESS, DONE) по статусам подзадач
 *   - Хранение истории просмотров (через связный список и HashMap) – но можно оставить простую версию
 *   - Приоритетная сортировка через TreeSet по startTime
 */
public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(
            Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())
    ));
    private int idCounter = 1;

    // ===== Task =====

    @Override
    public List<Task> getTasks() {
        for (Task task : tasks.values()) {
            historyManager.add(task);
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task createTask(Task task) throws ManagerSaveException {
        validateTaskTime(task);
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) throws ManagerSaveException {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task с id=" + task.getId() + " не найдена");
        }
        validateTaskTime(task);
        // удаляем старую из приоритета, заменяем новой
        prioritizedTasks.remove(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public void deleteTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
            historyManager.remove(id);
        }
    }

    // ===== Subtask =====

    @Override
    public List<Subtask> getSubtasks() {
        for (Subtask sub : subtasks.values()) {
            historyManager.add(sub);
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask sub = subtasks.get(id);
        if (sub != null) {
            historyManager.add(sub);
        }
        return sub;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws ManagerSaveException {
        // проверяем, существует ли эпик
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id=" + subtask.getEpicId() + " не найден");
        }
        validateTaskTime(subtask);
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatusAndTime(epic.getId());
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws ManagerSaveException {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Subtask с id=" + subtask.getId() + " не найдена");
        }
        validateTaskTime(subtask);
        Subtask old = subtasks.get(subtask.getId());
        Epic epic = epics.get(old.getEpicId());
        // если изменился эпик у подзадачи — обновляем списки
        if (subtask.getEpicId() != old.getEpicId()) {
            epic.removeSubtaskId(old.getId());
            Epic newEpic = epics.get(subtask.getEpicId());
            if (newEpic == null) {
                throw new IllegalArgumentException("Новый эпик с id=" + subtask.getEpicId() + " не найден");
            }
            newEpic.addSubtaskId(subtask.getId());
        }
        prioritizedTasks.remove(old);
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        updateEpicStatusAndTime(epic.getId());
        return subtask;
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
            historyManager.remove(id);
            Epic epic = epics.get(removed.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatusAndTime(epic.getId());
            }
        }
    }

    // ===== Epic =====

    @Override
    public List<Epic> getEpics() {
        for (Epic epic : epics.values()) {
            historyManager.add(epic);
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) throws ManagerSaveException {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
        // Статус новому эпику пока – NEW, время рассчитывается далее, но если нет подзадач, duration и startTime остаются null
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) throws ManagerSaveException {
        if (!epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("Epic с id=" + epic.getId() + " не найден");
        }
        // При обновлении эпика меняются только name и description. Подзадачи не трогаем.
        Epic old = epics.get(epic.getId());
        epic.setSubtaskIds(old.getSubtaskIds());
        epic.setStatus(old.getStatus());
        epic.setDuration(old.getDuration());
        epic.setStartTime(old.getStartTime());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void deleteEpic(int id) {
        Epic removed = epics.remove(id);
        if (removed != null) {
            // удаляем все связанные подзадачи
            for (Integer subId : removed.getSubtaskIds()) {
                Subtask sub = subtasks.remove(subId);
                if (sub != null) {
                    prioritizedTasks.remove(sub);
                    historyManager.remove(subId);
                }
            }
            removed.getSubtaskIds().clear();
            historyManager.remove(id);
            prioritizedTasks.remove(removed);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        List<Subtask> result = new ArrayList<>();
        for (Integer subId : epic.getSubtaskIds()) {
            Subtask sub = subtasks.get(subId);
            if (sub != null) {
                historyManager.add(sub);
                result.add(sub);
            }
        }
        return result;
    }

    // ===== History & Prioritized =====

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    // ===== Вспомогательные методы =====

    /**
     * Проверяет, что новая или обновлённая задача/subtask не пересекается по времени с уже существующими.
     * Выбрасывает ManagerSaveException, если обнаружено пересечение.
     */
    private void validateTaskTime(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            // Если у задачи нет времени – считаем, что пересечения быть не может
            return;
        }
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newStart.plus(newTask.getDuration());
        for (Task existing : prioritizedTasks) {
            if (existing.getStartTime() == null || existing.getDuration() == null) {
                continue;
            }
            LocalDateTime existStart = existing.getStartTime();
            LocalDateTime existEnd = existStart.plus(existing.getDuration());
            boolean overlap = newStart.isBefore(existEnd) && existStart.isBefore(newEnd);
            if (overlap && existing.getId() != newTask.getId()) {
                throw new ManagerSaveException("Найдена пересекающаяся задача/subtask с id=" + existing.getId());
            }
        }
    }

    /**
     * Пересчитывает статус, startTime, duration эпика на основе его подзадач.
     */
    private void updateEpicStatusAndTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        List<Integer> subs = epic.getSubtaskIds();
        if (subs.isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.setStartTime(null);
            epic.setDuration(null);
            return;
        }
        // Собираем статусы подзадач
        boolean allNew = true;
        boolean allDone = true;

        LocalDateTime earliest = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (Integer subId : subs) {
            Subtask sub = subtasks.get(subId);
            if (sub == null) continue;

            // Статусы
            if (sub.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (sub.getStatus() != Status.DONE) {
                allDone = false;
            }

            // Время
            if (sub.getStartTime() != null && sub.getDuration() != null) {
                LocalDateTime s = sub.getStartTime();
                LocalDateTime e = s.plus(sub.getDuration());
                if (earliest == null || s.isBefore(earliest)) {
                    earliest = s;
                }
                if (latestEnd == null || e.isAfter(latestEnd)) {
                    latestEnd = e;
                }
                totalDuration = totalDuration.plus(sub.getDuration());
            }
        }
        // Устанавливаем статус эпика
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        // Время эпика: с earliest до latestEnd, duration = разница
        if (earliest != null && latestEnd != null) {
            epic.setStartTime(earliest);
            epic.setDuration(Duration.between(earliest, latestEnd));
            // В prioritizedTasks мы эпик не храним (в большинстве реализаций),
            // но если храним, нужно обновить:
            prioritizedTasks.remove(epic);
            prioritizedTasks.add(epic);
        } else {
            epic.setStartTime(null);
            epic.setDuration(null);
        }
    }
}
