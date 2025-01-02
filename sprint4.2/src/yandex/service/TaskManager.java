package yandex.service;

import com.yandex.app.model.*;

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
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getSubtasksOfEpic(int epicID) {
        Task task = tasks.get(epicID);
        if (task instanceof Epic) {
            return ((Epic) task).getSubtaskList();
        }
        return Collections.emptyList();
    }

    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
        return task;
    }

    public Epic updateEpic(Epic epic) {
        if (tasks.containsKey(epic.getId())) {
            tasks.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
        return epic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if (tasks.containsKey(subtask.getId())) {
            tasks.put(subtask.getId(), subtask);
            Task epic = tasks.get(subtask.getEpicID());
            if (epic instanceof Epic) {
                updateEpicStatus((Epic) epic);
            }
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
        tasks.clear();
        idCounter = 0;
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
