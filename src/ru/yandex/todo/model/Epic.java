package ru.yandex.todo.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    // Конструктор для тестов и быстрого создания
    public Epic(String name, String description) {
        super(name, description, Status.NEW, Duration.ZERO, null);
    }

    // Здесь должна быть логика обновления статуса, времени и длительности по подзадачам
    // public void updateFromSubtasks(...)

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }
}
