package ru.yandex.todo.manager;

/**
 * Исключение, выбрасываемое при ошибке сохранения или при пересечении времени задач.
 */
public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    // Добавим конструктор, принимающий только сообщение:
    public ManagerSaveException(String message) {
        super(message);
    }
}
