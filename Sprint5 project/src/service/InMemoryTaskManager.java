package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int idCounter = 0;

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
        if (task != null) {
            historyManager.remove(id); // Удаляем задачу из истории просмотров
            tasks.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Task task = tasks.get(id);
        if (task instanceof Epic) {
            Epic epic = (Epic) task;

            // Удаляем все связанные подзадачи
            for (Subtask subtask : epic.getSubtaskList()) {
                historyManager.remove(subtask.getId());
                tasks.remove(subtask.getId());
            }

            historyManager.remove(id); // Удаляем эпик из истории
            tasks.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Task task = tasks.get(id);
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            Epic epic = (Epic) tasks.get(subtask.getEpicID());

            if (epic != null) {
                epic.getSubtaskList().remove(subtask);
                updateEpicStatus(epic);
            }

            historyManager.remove(id); // Удаляем подзадачу из истории просмотров
            tasks.remove(id);
        }
    }

    @Override
    public void clearAllTasks() {
        for (Integer id : new ArrayList<>(tasks.keySet())) {
            if (!(tasks.get(id) instanceof Subtask || tasks.get(id) instanceof Epic)) {
                historyManager.remove(id);
                tasks.remove(id);
            }
        }
    }

    @Override
    public void clearAllEpics() {
        for (Task task : new ArrayList<>(tasks.values())) {
            if (task instanceof Epic) {
                for (Subtask subtask : ((Epic) task).getSubtaskList()) {
                    historyManager.remove(subtask.getId());
                    tasks.remove(subtask.getId());
                }
                historyManager.remove(task.getId());
                tasks.remove(task.getId());
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
                historyManager.remove(subtask.getId());
                tasks.remove(subtask.getId());
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
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
}
