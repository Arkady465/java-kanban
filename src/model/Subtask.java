package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Подзадача, привязанная к определённому эпику.
 */
public class Subtask extends Task {
    private final int epicID;

    public Subtask(String name,
                   String description,
                   int epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    public Subtask(int id,
                   String name,
                   String description,
                   Status status,
                   Duration duration,
                   LocalDateTime startTime,
                   int epicID) {
        super(id, name, description, status, duration, startTime);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicID == subtask.epicID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicID);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", description='" + getDescription() + "'" +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", epicID=" + epicID +
                '}';
    }
}
