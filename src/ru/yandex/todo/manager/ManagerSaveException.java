package ru.yandex.todo.manager;

/**
 * Исключение, выбрасываемое при ошибке сохранения или при пересечении времени задач.
 */
public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }
}
