package todo.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс эпика. Наследует Task и содержит список подзадач.
 * Статус эпика пересчитывается на основе статусов подзадач в менеджере.
 */
public class Epic extends Task {
    // Список id подзадач, принадлежащих этому эпику
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic() {
        // Пустой конструктор для Gson
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, null);
    }

    public Epic(int id, String name, String description, Status status,
                Duration duration, LocalDateTime startTime, List<Integer> subtaskIds) {
        super(id, name, description, status, duration, startTime);
        this.subtaskIds = new ArrayList<>(subtaskIds);
    }

    // ===== Геттеры и сеттеры для subtaskIds =====

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = new ArrayList<>(subtaskIds);
    }

    // ===== Добавление / удаление id подзадачи =====

    public void addSubtaskId(int subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    // ===== equals(), hashCode(), toString() =====

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
