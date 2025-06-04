package ru.yandex.todo.manager;

public class ManagerSaveExceptions extends RuntimeException {
    public ManagerSaveExceptions(String message) {
        super(message);
    }

    public ManagerSaveExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
