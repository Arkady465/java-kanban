package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Эпик. Внутри хранит список его подзадач.
 * При пересчёте статуса и временных границ учитывает все подзадачи.
 */
public class Epic extends Task {
    private final List<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        setStatus(Status.NEW);
    }

    public Epic(int id,
                String name,
                String description,
                Status status,
                Duration duration,
                LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
    }

    public List<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public void removeSubtask(int subtaskId) {
        subtaskList.removeIf(s -> s.getId() == subtaskId);
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime max = null;
        for (Subtask s : subtaskList) {
            LocalDateTime end = s.getEndTime();
            if (end != null) {
                if (max == null || end.isAfter(max)) {
                    max = end;
                }
            }
        }
        return max;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", description='" + getDescription() + "'" +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", subtaskList=" + subtaskList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskList, epic.subtaskList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskList);
    }
}
