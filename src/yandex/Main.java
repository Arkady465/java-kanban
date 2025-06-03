package yandex;

import manager.TaskManager;
import service.FileBackedTaskManager;
import model.Task;
import model.Status;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Простой пример использования FileBackedTaskManager.
 */
public class Main {
    public static void main(String[] args) {
        TaskManager manager = new FileBackedTaskManager("tasks.csv");

        // Пример создания задачи (здесь используем полный конструктор: name, description, status, duration, startTime)
        Task t1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(t1);

        Task t2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));
        manager.createTask(t2);

        System.out.println("=== Задачи, сохранённые в менеджере:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        // Загружаем из файла через статический метод
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new java.io.File("tasks.csv"));
        System.out.println("=== Задачи после загрузки из файла:");
        for (Task task : loadedManager.getAllTasks()) {
            System.out.println(task);
        }
    }
}
