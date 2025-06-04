package ru.yandex.todoo.manager;

import ru.yandex.todo.storage.FileBackedTaskManager;
import ru.yandex.todo.manager.TaskManager;

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
