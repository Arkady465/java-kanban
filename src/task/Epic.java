package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> subTasks = new ArrayList<>();
    protected LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.taskType = TaskType.EPIC;
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.taskType = TaskType.EPIC;
    }


    public Epic(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.taskType = TaskType.EPIC;
    }


    public Epic(String name, String description, Status status, TaskType taskType, LocalDateTime startTime, Duration duration) {
        super(name, description, status, taskType, startTime, duration);
    }

    public List<Subtask> getSubTaskListForEpic() {
        return subTasks;
    }

    public void setSubTaskListOfEpic(List<Subtask> subTasks) {
        this.subTasks = subTasks;
    }

    public void saveSubTaskOfEpic(Subtask subtaskTaskOfEpic) {
        subTasks.add(subtaskTaskOfEpic);
    }

    public void removeSubTaskOfEpic(Subtask subtaskTaskOfEpic) {
        subTasks.remove(subtaskTaskOfEpic);
    }

    @Override
    public LocalDateTime getEndTime() {
        if (startTime != null) {
            endTime = getStartTime().plus(getDuration());
        }
        return endTime;
    }

    public void calculationTimeFields() {
        if (subTasks.isEmpty()) {
            duration = Duration.ZERO;
            startTime = null;
            endTime = null;
            return;
        }
        // минимальное время начала
        startTime = subTasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        // максимальное время конца
        endTime = subTasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (startTime != null && endTime != null) {
            duration = Duration.between(startTime, endTime);
        } else {
            duration = Duration.ZERO;
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "Id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", taskType=" + taskType +
                ", subTaskList=" + subTasks +
                ", starTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                "} ";
    }
}

