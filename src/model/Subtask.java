package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс “подзадачи” (Subtask). Наследует Task.
 * В тестах есть обход: new Subtask("Subtask 1", "Description 1", epic.getId());
 * Поэтому нужен ровно такой конструктор.
 *
 * Поля:
 *  - int epicID      – id того Epic, к которому относится подзадача
 *
 * Методы:
 *  - getEpicID()  (именно с таким регистром, т.к. в тесте assertEquals(subtask.getEpicID(), …))
 *  - getType() -> TaskType.SUBTASK
 */
public class Subtask extends Task {
    private int epicID; // именно так тесты ожидают название поля/геттера

    public Subtask() {
        // Пустой конструктор
    }

    /**
     * Конструктор, используемый в тестах:
     * new Subtask("Subtask 1", "Description 1", epic.getId());
     * По умолчанию статус = NEW, время и длительность = null.
     */
    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    /**
     * Если нужно задать id явно (реже используется), можно добавить конструктор:
     * public Subtask(int id, String name, String description, int epicID) { … }
     * Но тесты это не используют, поэтому необязательно.
     */

    // ===== Геттер и сеттер epicID =====

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    // ===== equals(), hashCode(), toString() =====

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
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", epicID=" + epicID +
                '}';
    }
}
