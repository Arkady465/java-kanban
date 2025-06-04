package ru.yandex.todo.manager;

import todo.storage.FileBackedTaskManager;

import java.io.File;

/**
 * Утилитный класс – фабрика менеджеров.
 * Метод getDefault() возвращает экземпляр TaskManager
 * (по умолчанию – файловый менеджер).
 */
public class Managers {
    public static TaskManager getDefault() {
        File file = new File("tasks_data.csv");
        return new FileBackedTaskManager(file);
    }
}
