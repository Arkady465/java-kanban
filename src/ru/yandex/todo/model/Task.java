package ru.yandex.todo.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task
{
    private int id;
    private String name;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(int id,
                String name,
                String description,
                Status status,
                Duration duration,
                LocalDateTime startTime)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name,
                String description,
                Status status,
                Duration duration,
                LocalDateTime startTime)
    {
        this(0, name, description, status, duration, startTime);
    }

    // Конструктор для тестов и быстрого создания
    public Task(String name,
                String description)
    {
        this(0, name, description, Status.NEW, Duration.ZERO, null);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public Duration getDuration()
    {
        return duration;
    }

    public void setDuration(Duration duration)
    {
        this.duration = duration;
    }

    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime)
    {
        this.startTime = startTime;
    }
}
