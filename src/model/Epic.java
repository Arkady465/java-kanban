package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Эпик (Epic) — задача, содержащая список подзадач.
 * Статус и время рассчитываются на основе подзадач в менеджере.
 */
public class Epic extends Task {
    // Список id подзадач
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic() {
        // Для десериализации
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, null);
    }

    public Epic(int id,
                String name,
                String description,
                Status status,
                Duration duration,
                LocalDateTime startTime,
                List<Integer> subtaskIds) {
        super(id, name, description, status, duration, startTime);
        this.subtaskIds = new ArrayList<>(subtaskIds);
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = new ArrayList<>(subtaskIds);
    }

    public void addSubtaskId(int subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
