package yandex;

import model.*;
import service.FileBackedTaskManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("data.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Покупки", "Купить хлеб и молоко");
        Epic epic = new Epic("Ремонт", "Сделать ремонт в комнате");
        Subtask sub1 = new Subtask("Поклеить обои", "Выбрать светлые", epic.getId());

        manager.addTask(task1);
        manager.addEpic(epic);
        manager.addSubtask(sub1);

        System.out.println("Сохранённые задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        // Загружаем менеджер из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println("\nЗагруженные задачи из файла:");
        for (Task task : loadedManager.getAllTasks()) {
            System.out.println(task);
        }
    }
}
