package ru.yandex.todo.manager;

import ru.yandex.todo.model.*;

import java.util.*;

public abstract class InMemoryTaskManagers implements TaskManagers {
    protected final Map<Integer, Tasks> tasks = new HashMap<>();
    protected final Map<Integer, Epics> epics = new HashMap<>();
    protected final Map<Integer, Subtasks> subtasks = new HashMap<>();
    protected final HistoryManagers historyManagers = new InMemoryHistoryManagers();
    protected int idCounter = 1;

    @Override
    public Tasks addTask(Tasks tasks) {
        tasks.setId(idCounter++);
        this.tasks.put(tasks.getId(), tasks);
        return tasks;
    }

    @Override
    public Epics addEpic(Epics epics) {
        epics.setId(idCounter++);
        this.epics.put(epics.getId(), epics);
        return epics;
    }

    @Override
    public Subtasks addSubtask(Subtasks subtasks) {
        subtasks.setId(idCounter++);
        this.subtasks.put(subtasks.getId(), subtasks);
        Epics epics = this.epics.get(subtasks.getEpicID());
        if (epics != null) {
            epics.addSubtask(subtasks);
            updateEpicStatus(epics);
        }
        return subtasks;
    }

    @Override
    public Tasks getTask(int id) {
        Tasks tasks = this.tasks.get(id);
        if (tasks != null) historyManagers.add(tasks);
        return tasks;
    }

    @Override
    public Epics getEpic(int id) {
        Epics epics = this.epics.get(id);
        if (epics != null) historyManagers.add(epics);
        return epics;
    }

    @Override
    public Subtasks getSubtask(int id) {
        Subtasks subtasks = this.subtasks.get(id);
        if (subtasks != null) historyManagers.add(subtasks);
        return subtasks;
    }

    protected void updateEpicStatus(Epics epics) {
        List<Subtasks> subtasksList = epics.getSubtaskList();
        if (subtasksList.isEmpty()) {
            epics.setStatus(Status.NEW);
            return;
        }

        boolean allNew = subtasksList.stream().allMatch(s -> s.getStatus() == Status.NEW);
        boolean allDone = subtasksList.stream().allMatch(s -> s.getStatus() == Status.DONE);

        if (allDone) {
            epics.setStatus(Status.DONE);
        } else if (allNew) {
            epics.setStatus(Status.NEW);
        } else {
            epics.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Tasks> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epics> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtasks> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Tasks> getHistory() {
        return historyManagers.getHistory();
    }

    public abstract void save();
}
