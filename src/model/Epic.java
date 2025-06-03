package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс “эпик” (Epic). Наследует Task, но при этом содержит список подзадач.
 *
 * В тестах ожидаются следующие методы:
 *  - конструктор Epic(String name, String description)
 *  - addSubtask(Subtask)
 *  - removeSubtask(int subtaskId)
 *  - getSubtaskList() возвращает List<Subtask>
 *  - clearSubtasks()
 *  - getEndTime() вычисляет максимальное endTime от всех подзадач (или null, если списка нет)
 *  - getType() -> TaskType.EPIC
 */
public class Epic extends Task {
    private final List<Subtask> subtaskList = new ArrayList<>();

    public Epic() {
        // Пустой конструктор
    }

    public Epic(String name, String description) {
        super(name, description);
        // Статус по умолчанию = NEW. Время/длительность – null, пока нет подзадач.
    }

    /**
     * Добавляет подзадачу в этот эпик:
     */
    public void addSubtask(Subtask subtask) {
        if (subtask != null && !subtaskList.contains(subtask)) {
            subtaskList.add(subtask);
        }
    }

    /**
     * Удаляет подзадачу по ее id (если она есть).
     */
    public void removeSubtask(int subtaskId) {
        subtaskList.removeIf(s -> s.getId() == subtaskId);
    }

    /**
     * Возвращает список подзадач этого эпика.
     */
    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(subtaskList);
    }

    /**
     * Очищает весь список подзадач (например, при удалении эпика).
     */
    public void clearSubtasks() {
        subtaskList.clear();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    /**
     * Возвращает максимальный endTime среди всех подзадач или null, если ни у одной подзадачи нет времени.
     */
    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime max = null;
        for (Subtask s : subtaskList) {
            LocalDateTime e = s.getEndTime();
            if (e != null && (max == null || e.isAfter(max))) {
                max = e;
            }
        }
        return max;
    }

    // ===== equals(), hashCode(), toString() =====

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

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", subtaskList=" + subtaskList +
                '}';
    }
}
