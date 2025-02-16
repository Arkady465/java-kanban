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
    public void deleteTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void clearAllEpics() {
        // Удаляем все эпики и их подзадачи
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
        // Удаляем все подзадачи из списка задач
        tasks.values().removeIf(task -> task instanceof Subtask);

        // Также удаляем подзадачи из истории просмотров
        for (Task task : new ArrayList<>(tasks.values())) {
            if (task instanceof Subtask) {
                historyManager.remove(task.getId());
            }
        }

        // Обновляем статусы эпиков после удаления подзадач
        for (Task task : tasks.values()) {
            if (task instanceof Epic) {
                updateEpicStatus((Epic) task);
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
