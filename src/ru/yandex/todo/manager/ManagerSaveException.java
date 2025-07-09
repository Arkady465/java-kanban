package ru.yandex.todo.manager;

/**
 * Исключение, возникающее при ошибках сохранения/загрузки из файла.
 */
public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
