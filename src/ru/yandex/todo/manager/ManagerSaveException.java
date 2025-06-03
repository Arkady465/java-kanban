package ru.yandex.todo.manager;

/**
 * Исключение, выбрасываемое при ошибках сохранения/загрузки или пересечении времени задач.
 */
public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerSaveException(String message) {
        super(message);
    }
}
