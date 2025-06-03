package yandex;

import ru.yandex.todo.manager.TaskManager;
import ru.yandex.todo.storage.FileBackedTaskManager;
import ru.yandex.todo.model.Task;

/**
 * Простой пример использования FileBackedTaskManager.
 */
public class Main {
    public static void main(String[] args) {
        // Теперь TaskManager и FileBackedTaskManager импортируются из ru.yandex.todo...
        TaskManager manager = new FileBackedTaskManager("tasks.csv");

        // Добавим пару задач
        manager.createTask(new Task("Task 1", "Description 1"));
        manager.createTask(new Task("Task 2", "Description 2"));

        System.out.println("Задачи в менеджере:");
        // Предположим, что в TaskManager есть метод getTasks(), возвращающий List<Task>
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        // Загружаем заново из файла (через статический метод)
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new java.io.File("tasks.csv"));
        System.out.println("Задачи после загрузки из файла:");
        for (Task task : loadedManager.getTasks()) {
            System.out.println(task);
        }
    }
}
