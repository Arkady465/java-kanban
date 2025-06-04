package ru.yandex.todo.manager;

import ru.yandex.todo.storage.FileBackedTaskManagers;

import java.io.File;

/**
 * Утилитный класс – фабрика менеджеров.
 * Метод getDefault() возвращает экземпляр TaskManager
 * (по умолчанию – файловый).
 */
public class Managers {
    public static TaskManagers getDefault() {
        File file = new File("tasks_data.csv");
        return new FileBackedTaskManagers(file);
    }
}
