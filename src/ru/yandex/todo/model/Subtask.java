package ru.yandex.todo.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    // Конструктор для тестов
    public Subtask(String name, String description, int epicId) {
        super(name, description, Status.NEW, Duration.ZERO, null);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime, int epicId) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}


