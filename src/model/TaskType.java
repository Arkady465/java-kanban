package model;

/**
 * Тип задачи: TASK, SUBTASK или EPIC.
 * Нужен для того, чтобы InMemoryTaskManager мог различать “чьи” задачи.
 */
public enum TaskType {
    TASK,
    SUBTASK,
    EPIC
}
