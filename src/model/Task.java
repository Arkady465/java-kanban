package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Базовый класс задачи (Task).
 *
 * Конструкторы:
 *  - Task(String name, String description)               – тесты используют именно этот.
 *  - Task(int id, String name, String description)       – чтобы присвоить id, если нужно.
 *
 * Поля:
 *  - int id
 *  - String name
 *  - String description
 *  - Status status         (NEW по умолчанию)
 *  - Duration duration     (по умолчанию null)
 *  - LocalDateTime startTime (по умолчанию null)
 *
 * Методы:
 *  - getEndTime() возвращает (startTime + duration) или null, если хоть одно из полей null.
 *  - getType() возвращает TaskType.TASK.
 */
public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task() {
        // Пустой конструктор нужен для десериализации / гибкости
    }

    /**
     * Конструктор, используемый в тестах:
     * Task task = new Task("Task 1", "Description 1");
     * По умолчанию статус = NEW, время = null.
     */
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.duration = null;
        this.startTime = null;
    }

    /**
     * Если нужен полный конструктор (например, чтобы явно задать id).
     */
    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.duration = null;
        this.startTime = null;
    }

    // ===== Геттеры и сеттеры =====

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Возвращает LocalDateTime конца задачи, как (startTime + duration).
     * Если хоть одно поле null, возвращает null.
     */
    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    /**
     * Тип задачи — для приоритетной сортировки и фильтров.
     */
    public TaskType getType() {
        return TaskType.TASK;
    }

    // ===== equals(), hashCode(), toString() =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status &&
                Objects.equals(duration, task.duration) &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, duration, startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
