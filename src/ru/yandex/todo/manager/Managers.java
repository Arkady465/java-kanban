package ru.yandex.todo.manager;

import ru.yandex.todo.storage.FileBackedTaskManager;

import java.io.File;

/**
 * Утилитный класс – фабрика менеджеров.
 * Метод getDefault() возвращает экземпляр TaskManager.
 */
public class Managers {

    public static TaskManager getDefault() {
        // Предположим, что в корне проекта хотим хранить файл "tasks_data.csv"
        File file = new File("tasks_data.csv");
        return new FileBackedTaskManager(file);
    }
}
