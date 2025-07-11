package taskmanager.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime; // вычисляемое поле

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, Status status, String description) {
        super(id, name, status, description);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(int subtaskId) {
        subtasks.removeIf(subtask -> subtask.getId() == subtaskId);
    }

    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks);
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // Сериализация: id, тип, имя, статус, описание, startTime, duration, endTime
    @Override
    public String toString() {
        String start = (startTime != null) ? startTime.toString() : "null";
        String dur = (duration != null) ? String.valueOf(duration.toMinutes()) : "null";
        String end = (endTime != null) ? endTime.toString() : "null";
        return id + "," + getType() + "," + name + "," + status + "," + description + "," + start + "," + dur + "," + end;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Epic epic = (Epic) o;
        return subtasks.equals(epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }
}
