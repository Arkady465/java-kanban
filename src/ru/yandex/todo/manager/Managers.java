package ru.yandex.todo.manager;

import ru.yandex.todo.storage.FileBackedTaskManager;

/**
 * Утилитный класс – фабрика. Метод getDefault() возвращает экземпляр TaskManager.
 * Здесь вы можете выбрать, будете ли вы по умолчанию использовать
 * FileBackedTaskManager (с сериализацией в файл) или InMemoryTaskManager.
 */
public class Managers {
    public static TaskManager getDefault() {
        // Например, возвращаем FileBackedTaskManager.
        // Если хотите чистый InMemory, замените на new InMemoryTaskManager().
        return new FileBackedTaskManager("tasks_data.csv");
    }
}
