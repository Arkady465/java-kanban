package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, LocalDateTime localDateTime, Duration duration, int epicId) {
        super(name, description, status, localDateTime, duration);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, TaskType taskType, LocalDateTime localDateTime, Duration duration, int epicId) {
        super(name, description, status, taskType, localDateTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Sub{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", taskType=" + taskType +
                ", starTime=" + startTime +
                ", duration=" + duration +
                ", epicID=" + epicId +
                "} ";
    }
}

