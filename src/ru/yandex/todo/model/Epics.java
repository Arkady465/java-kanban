package ru.yandex.todo.model;

import java.util.ArrayList;

public class Epics extends Tasks {
    private final ArrayList<Subtasks> subtasksList = new ArrayList<>();

    public Epics(String name, String description) {
        super(name, description);
    }

    public void addSubtask(Subtasks subtasks) {
        subtasksList.add(subtasks);
    }

    public void clearSubtasks() {
        subtasksList.clear();
    }

    public ArrayList<Subtasks> getSubtaskList() {
        return new ArrayList<>(subtasksList);
    }
}
