package ru_yandex_todo.manager;

import service.FileBackedTaskManager;

import java.io.File;

/**
 * Утилитный класс – фабрика менеджеров.
 * Метод getDefault() возвращает экземпляр TaskManager
 * (по умолчанию – файловый).
 */
public class Managers {
    public static TaskManager getDefault() {
        File file = new File("tasks_data.csv");
        return new FileBackedTaskManager(file);
    }
}
