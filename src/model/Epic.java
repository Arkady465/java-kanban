package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtaskList);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toCsvString() {
        return String.format("%d,%s,%s,%s,%s,", getId(), getType().name(), getName(), getStatus().name(), getDescription());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name=" + getName() +
                ", description=" + getDescription() +
                ", id=" + getId() +
                ", subtaskList.size=" + subtaskList.size() +
                ", status=" + getStatus() +
                '}';
    }
}
