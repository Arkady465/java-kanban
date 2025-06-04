package ru.yandex.todo.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс подзадачи. Наследует Task и содержит ссылку на эпик (epicId).
 */
public class Subtasks extends Tasks {
    private int epicId;  // id эпика, к которому относится подзадача

    public Subtasks() {
        // Конструктор без параметров для Gson
    }

    public Subtasks(String name, String description, Status status,
                    Duration duration, LocalDateTime startTime, int epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtasks(int id, String name, String description, Status status,
                    Duration duration, LocalDateTime startTime, int epicId) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    // ===== Геттер и сеттер для epicId =====

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    // ===== equals(), hashCode(), toString() =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtasks subtasks = (Subtasks) o;
        return epicId == subtasks.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", epicId=" + epicId +
                '}';
    }
}
