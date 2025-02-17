package yandex;

import com.yandex.app.model.*;
import com.yandex.app.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("Clean floor", "Use new detergent");
        taskManager.addTask(task);
        System.out.println(taskManager.getTask(task.getId()));

        Epic epic = new Epic("Renovate flat", "Complete during vacation");
        taskManager.addEpic(epic);
        System.out.println(taskManager.getTask(epic.getId()));

        Subtask subtask = new Subtask("Wallpapering", "Choose light colors", epic.getId());
        taskManager.addSubtask(subtask);
        System.out.println(taskManager.getTask(subtask.getId()));

        System.out.println("History:");
        for (Task t : taskManager.getHistory()) {
            System.out.println(t);
        }
    }
}