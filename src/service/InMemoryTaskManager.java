ppackage service;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;
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
        tasks.values().removeIf(task -> !(task instanceof Subtask) && !(task instanceof Epic));
    }

    @Override
    public void clearAllEpics() {
        for (Task task : new ArrayList<>(tasks.values())) {
            if (task instanceof Epic) {
                Epic epic = (Epic) task;
                for (Subtask subtask : epic.getSubtaskList()) {
                    tasks.remove(subtask.getId());
                    historyManager.remove(subtask.getId());
                }
                tasks.remove(epic.getId());
                historyManager.remove(epic.getId());
            }
        }
    }

    @Override
    public void clearAllSubtasks() {
        for (Task task : new ArrayList<>(tasks.values())) {
            if (task instanceof Subtask) {
                Subtask subtask = (Subtask) task;
                Epic epic = (Epic) tasks.get(subtask.getEpicID());
                if (epic != null) {
                    epic.getSubtaskList().remove(subtask);
                    updateEpicStatus(epic);
                }
                tasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
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
        boolean allDone = true;
        boolean anyInProgress = false;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.NEW) {
                allDone = false;
            } else if (subtask.getStatus() == Status.IN_PROGRESS) {
                allDone = false;
                anyInProgress = true;
            }
        }
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    protected void addRestoredTask(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void setIdCounter(int value) {
        idCounter = value;
    }
}
