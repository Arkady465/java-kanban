package yandex.service;

import yandex.service.model.*;

import java.util.*;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private int idCounter = 0;

    public Task addTask(Task task) {
        task.setId(++idCounter);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic addEpic(Epic epic) {
        epic.setId(++idCounter);
        tasks.put(epic.getId(), epic);
        return epic;
    }

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

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Task && !(task instanceof Subtask || task instanceof Epic)) {
                allTasks.add(task);
            }
        }
        return allTasks;
    }

    public List<Epic> getAllEpics() {
        List<Epic> epics = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic) {
                epics.add((Epic) task);
            }
        }
        return epics;
    }

    public List<Subtask> getAllSubtasks() {
        List<Subtask> subtasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) {
                subtasks.add((Subtask) task);
            }
        }
        return subtasks;
    }

    public List<Subtask> getSubtasksOfEpic(int epicID) {
        Task task = tasks.get(epicID);
        if (task instanceof Epic) {
            return ((Epic) task).getSubtaskList();
        }
        return Collections.emptyList();
    }

    public Task updateTask(Task task) {
        tasks.putIfAbsent(task.getId(), task);
        return task;
    }

    public Epic updateEpic(Epic epic) {
        tasks.putIfAbsent(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic;
    }

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

    public void deleteTask(int id) {
        Task task = tasks.get(id);
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
            }
        }
        tasks.remove(id);
    }

    public void clearAllTasks() {
        tasks.values().removeIf(task -> task instanceof Task && !(task instanceof Subtask || task instanceof Epic));
    }

    public void clearAllEpics() {
        for (Iterator<Task> iterator = tasks.values().iterator(); iterator.hasNext();) {
            Task task = iterator.next();
            if (task instanceof Epic) {
                for (Subtask subtask : ((Epic) task).getSubtaskList()) {
                    tasks.remove(subtask.getId());
                }
                iterator.remove();
            }
        }
    }

    public void clearAllSubtasks() {
        for (Iterator<Task> iterator = tasks.values().iterator(); iterator.hasNext();) {
            Task task = iterator.next();
            if (task instanceof Subtask) {
                Subtask subtask = (Subtask) task;
                Task epic = tasks.get(subtask.getEpicID());
                if (epic instanceof Epic) {
                    ((Epic) epic).getSubtaskList().remove(subtask);
                    updateEpicStatus((Epic) epic);
                }
                iterator.remove();
            }
        }
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
