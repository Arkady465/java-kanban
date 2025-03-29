package service;

import model.*;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int idCounter = 0;

    @Override
    public Task addTask(Task task) {
        task.setId(++idCounter);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(++idCounter);
        tasks.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(++idCounter);
        tasks.put(subtask.getId(), subtask);
        Task epic = tasks.get(subtask.getEpicID());
        if (epic instanceof Epic) {
            ((Epic) epic).addSubtask(subtask);
            updateEpicStatus((Epic) epic);
        }
        return subtask;
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
    public List<Epic> getAllEpics() {
        List<Epic> epics = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic) {
                epics.add((Epic) task);
            }
        }
        return epics;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> subtasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) {
                subtasks.add((Subtask) task);
            }
        }
        return subtasks;
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicID) {
        Task task = tasks.get(epicID);
        if (task instanceof Epic) {
            return ((Epic) task).getSubtaskList();
        }
        return Collections.emptyList();
    }

    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        tasks.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask oldSubtask = (Subtask) tasks.get(subtask.getId());
        tasks.put(subtask.getId(), subtask);
        Task epic = tasks.get(subtask.getEpicID());
        if (epic instanceof Epic) {
            Epic epicTask = (Epic) epic;
            epicTask.getSubtaskList().remove(oldSubtask);
            epicTask.addSubtask(subtask);
            updateEpicStatus(epicTask);
        }
        return subtask;
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return;
        }
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            Task epic = tasks.get(subtask.getEpicID());
            if (epic instanceof Epic) {
                ((Epic) epic).getSubtaskList().remove(subtask);
                updateEpicStatus((Epic) epic);
            }
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            for (Subtask subtask : epic.getSubtaskList()) {
                tasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void clearAllTasks() {
        List<Integer> idsToRemove = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (!(task instanceof Epic) && !(task instanceof Subtask)) {
                idsToRemove.add(task.getId());
            }
        }
        for (int id : idsToRemove) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearAllEpics() {
        List<Integer> idsToRemove = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic) {
                Epic epic = (Epic) task;
                for (Subtask subtask : epic.getSubtaskList()) {
                    tasks.remove(subtask.getId());
                    historyManager.remove(subtask.getId());
                }
                idsToRemove.add(epic.getId());
            }
        }
        for (int id : idsToRemove) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearAllSubtasks() {
        List<Integer> idsToRemove = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) {
                Subtask subtask = (Subtask) task;
                Epic epic = (Epic) tasks.get(subtask.getEpicID());
                if (epic != null) {
                    epic.getSubtaskList().remove(subtask);
                    updateEpicStatus(epic);
                }
                idsToRemove.add(subtask.getId());
            }
        }
        for (int id : idsToRemove) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = epic.getSubtaskList();
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
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
}
