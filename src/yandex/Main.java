package yandex;

import model.Task;
import model.Status;
import service.FileBackedTaskManager;
import service.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new FileBackedTaskManager(new File("tasks.csv"));

        Task t1 = new Task("Task 1", "Description 1");
        t1.setStatus(Status.NEW);
        t1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        t1.setDuration(Duration.ofMinutes(30));
        manager.addTask(t1);

        System.out.println("All tasks:");
        for (Task t : manager.getAllTasks()) {
            System.out.println(t);
        }
    }
}
