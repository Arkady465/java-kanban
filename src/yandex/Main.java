package yandex;

import manager.TaskManager;
import service.FileBackedTaskManager;
import model.Task;

public class Main {
    public static void main(String[] args) {
        // Создаём менеджер, привязанный к файлу tasks.csv
        TaskManager manager = new FileBackedTaskManager("tasks.csv");

        // Создадим несколько задач и добавим их
        manager.createTask(new Task("Task 1", "Description 1"));
        manager.createTask(new Task("Task 2", "Description 2"));

        // Вместо getAllTasks() – используем getTasks(), который возвращает List<Task>
        System.out.println("Задачи в менеджере:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        // Допустим, у нас есть метод loadFromFile(), который вернул
        // другой экземпляр менеджера (reloadManager)
        TaskManager loadedManager = new FileBackedTaskManager("tasks.csv");

        System.out.println("Задачи после загрузки из файла:");
        for (Task task : loadedManager.getTasks()) {
            System.out.println(task);
        }
    }
}
