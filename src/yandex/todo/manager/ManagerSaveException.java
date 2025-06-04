package ru.yandex.todo.manager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(); // гарантированно рабочий менеджер
    }
}
