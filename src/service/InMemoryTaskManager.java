package service;

import model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private int idCounter = 0;

    protected int generateId() {
        return ++idCounter;
    }

    @Override
    public Task addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
        }
        return subtask;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) historyManager.add(subtask);
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
        return List.of();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return List.of();
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) historyManager.remove(id);
    }

    @Override
    public void clearAllTasks() {

    }

    @Override
    public void clearAllEpics() {

    }

    @Override
    public void clearAllSubtasks() {

    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask sub : epic.getSubtaskList()) {
                subtasks.remove(sub.getId());
                historyManager.remove(sub.getId());
            }
            epic.clearSubtasks();
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicID());
            if (epic != null) {
                epic.getSubtaskList().removeIf(s -> s.getId() == id);
                updateEpicStatus(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubtaskList();
        }
        return Collections.emptyList();
    }

    protected void updateEpicStatus(Epic epic) {
        List<Subtask> subs = epic.getSubtaskList();
        if (subs.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = subs.stream().allMatch(s -> s.getStatus() == Status.NEW);
        boolean allDone = subs.stream().allMatch(s -> s.getStatus() == Status.DONE);

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            updateEpicStatus(epic);
        }
        return subtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic;
    }
}
