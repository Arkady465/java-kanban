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
 *  - Хранит приоритет (TreeSet<Task>), упорядочивая по startTime (раньше – впереди).
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
    public Task addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        // Если id не установлен, выдаём новый:
        if (task.getId() == 0) {
            task.setId(idCounter++);
        } else {
            // Если id уже есть и задача существует — удаляем старую из приоритета
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
        // Удаляем старый из приоритета, вставляем новый:
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

    // ===== Epic =====

    @Override
    public Epic addEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }
        if (epic.getId() == 0) {
            epic.setId(idCounter++);
        } else {
            if (epics.containsKey(epic.getId())) {
                // При обновлении эпика переносим старые подзадачи в новый объект
                Epic old = epics.get(epic.getId());
                for (Subtask s : old.getSubtaskList()) {
                    epic.addSubtask(s);
                }
                prioritizedSet.removeAll(old.getSubtaskList()); // удалим старые подзадачи из приоритета
            }
        }
        epics.put(epic.getId(), epic);
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
        // При обновлении эпика сохраняем связи с подзадачами из старого объекта:
        Epic oldEpic = epics.get(id);
        List<Subtask> oldSubtasks = oldEpic.getSubtaskList();
        // Удаляем подзадачи из приоритета (обновим ссылки заново ниже):
        for (Subtask s : oldSubtasks) {
            prioritizedSet.remove(s);
        }
        for (Subtask s : oldSubtasks) {
            epic.addSubtask(s);
        }
        epics.put(id, epic);
        // Заново вставляем подзадачи в приоритет (они всё ещё лежат в subtasks):
        for (Subtask s : oldSubtasks) {
            prioritizedSet.add(s);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic removed = epics.remove(id);
        if (removed != null) {
            // При удалении эпика — удаляем все его подзадачи
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
                prioritizedSet.remove(subtasks.get(subtask.getId()));
                parentEpic.removeSubtask(subtask.getId());
            }
        }
        validateTaskTime(subtask);
        subtasks.put(subtask.getId(), subtask);
        prioritizedSet.add(subtask);
        parentEpic.addSubtask(subtask);
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
        // Меняем принадлежность к эпикам, если нужно:
        Subtask old = subtasks.get(id);
        if (old.getEpicID() != subtask.getEpicID()) {
            Epic oldEpic = epics.get(old.getEpicID());
            if (oldEpic != null) {
                oldEpic.removeSubtask(id);
            }
            Epic newEpic = epics.get(subtask.getEpicID());
            if (newEpic == null) {
                throw new IllegalArgumentException("Epic with id=" + subtask.getEpicID() + " not found");
            }
            newEpic.addSubtask(subtask);
        }
        // Обновляем в приоритете:
        prioritizedSet.remove(old);
        validateTaskTime(subtask);
        subtasks.put(id, subtask);
        prioritizedSet.add(subtask);
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
