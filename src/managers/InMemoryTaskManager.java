package managers;

import exception.NotFoundException;
import task.Epic;
import task.Task;
import task.Status;
import task.Subtask;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager historyManager;
    protected final Set<Task> prioritizedTasks = new TreeSet<>(new TaskStartTimeComparator());
    private int nextId = 1;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public int generateId() {
        return nextId++;
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            throw new NotFoundException("Задача не найдена");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);

        if (!isOverlapTask(task)) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        epic.setId(generateId());
        epic.calculationTimeFields();
        epics.put(epic.getId(), epic);

        if (!isOverlapTask(epic)) {
            prioritizedTasks.add(epic);
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        epic.saveSubTaskOfEpic(subtask);
        epic.calculationTimeFields();
        updateStatus(epic);

        if (!isOverlapTask(subtask)) {
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                historyManager.remove(entry.getKey());
                prioritizedTasks.remove(entry.getValue());
            }
            tasks.clear();
        }
    }

    @Override
    public void deleteAllEpics() {
        if (!epics.isEmpty()) {
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                historyManager.remove(entry.getKey());
                prioritizedTasks.remove(entry.getValue());
                deleteAllSubTasksOfEpic(entry.getValue());
            }
            epics.clear();
        }
    }

    @Override
    public void deleteAllSubtasks() {
        if (!subtasks.isEmpty()) {
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                historyManager.remove(entry.getKey());
                prioritizedTasks.remove(entry.getValue());

                Epic epic = epics.get(entry.getValue().getEpicId());
                if (epic != null) {
                    epic.removeSubTaskOfEpic(entry.getValue());
                    epic.calculationTimeFields();
                    updateStatus(epic);
                }
            }
            subtasks.clear();
        }
    }

    @Override
    public void deleteTaskById(Task task) {
        if (task == null) {
            throw new NotFoundException("Задача не найдена");
        }
        tasks.remove(task.getId());
        historyManager.remove(task.getId());
        prioritizedTasks.remove(task);
    }

    @Override
    public void deleteEpicById(Epic epic) {
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        epics.remove(epic.getId());
        historyManager.remove(epic.getId());
        prioritizedTasks.remove(epic);
        deleteAllSubTasksOfEpic(epic);
    }

    @Override
    public void deleteSubtaskById(Subtask subtask) {
        if (subtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }
        subtasks.remove(subtask.getId());
        historyManager.remove(subtask.getId());
        prioritizedTasks.remove(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubTaskOfEpic(subtask);
            epic.calculationTimeFields();
            updateStatus(epic);
        }
    }

    @Override
    public void deleteAllSubTasksOfEpic(Epic epic) {
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        List<Subtask> listSub = new ArrayList<>(epic.getSubTaskListForEpic());
        if (!listSub.isEmpty()) {
            listSub.forEach(this::deleteSubtaskById);
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubTasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        List<Subtask> listSubTask = new ArrayList<>();
        epic.getSubTaskListForEpic().forEach(subtask -> {
            listSubTask.add(subtasks.get(subtask.getId()));
        });

        return listSubTask;
    }

    @Override
    public void updateTask(Task oldTask, Task newTask) {
        if (oldTask == null || newTask == null) {
            throw new NotFoundException("Задача не найдена");
        }
        newTask.setId(oldTask.getId());
        tasks.put(oldTask.getId(), newTask);
        prioritizedTasks.remove(oldTask);

        if (!isOverlapTask(newTask)) {
            prioritizedTasks.add(newTask);
        }
    }

    @Override
    public void updateEpic(Epic oldEpic, Epic newEpic) {
        if (oldEpic == null || newEpic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        newEpic.setId(oldEpic.getId());
        newEpic.setSubTaskListOfEpic(oldEpic.getSubTaskListForEpic());
        newEpic.calculationTimeFields();
        updateStatus(newEpic);
        epics.put(oldEpic.getId(), newEpic);
        prioritizedTasks.remove(oldEpic);

        if (!isOverlapTask(newEpic)) {
            prioritizedTasks.add(newEpic);
        }
    }

    @Override
    public void updateSubTask(Subtask oldSubtaskTask, Subtask newSubtaskTask) {
        if (oldSubtaskTask == null || newSubtaskTask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }
        newSubtaskTask.setId(oldSubtaskTask.getId());
        subtasks.put(oldSubtaskTask.getId(), newSubtaskTask);
        prioritizedTasks.remove(oldSubtaskTask);

        Epic epic = epics.get(oldSubtaskTask.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        epic.setSubTaskListOfEpic(getSubTasksForEpic(epic.getId()));
        epic.calculationTimeFields();
        updateStatus(epic);

        if (!isOverlapTask(newSubtaskTask)) {
            prioritizedTasks.add(newSubtaskTask);
        }
    }

    @Override
    public void updateStatus(Epic epic) {
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        List<Subtask> listSubTaskOfEpic = new ArrayList<>(epic.getSubTaskListForEpic());
        if (listSubTaskOfEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtaskTaskID : listSubTaskOfEpic) {
            Subtask subtask = subtasks.get(subtaskTaskID.getId());
            if (subtask != null) {
                if (subtask.getStatus() != Status.NEW) {
                    allNew = false;
                }
                if (subtask.getStatus() != Status.DONE) {
                    allDone = false;
                }
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public boolean isOverlapTask(Task newTask) {
        if (newTask.getStartTime() == null) {
            return true;
        }
        return prioritizedTasks.stream().anyMatch(task -> isOverlapTime(task, newTask));
    }

    private boolean isOverlapTime(Task a, Task b) {
        LocalDateTime aStart = a.getStartTime();
        LocalDateTime aEnd   = a.getStartTime().plus(a.getDuration());
        LocalDateTime bStart = b.getStartTime();
        LocalDateTime bEnd   = b.getStartTime().plus(b.getDuration());
        return !aEnd.isBefore(bStart) && !bEnd.isBefore(aStart);
    }

    @Override
    public String toString() {
        return "TaskManager.TaskManager{" +
                "tasks=" + tasks +
                ", subtasks=" + subtasks +
                ", epics=" + epics +
                '}';
    }
}
