package yandex2.ru.todo.manager;

import ru.yandex.todo.storage.FileBackedTaskManager;
import service.TaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("tasks_data.csv"));
    }
}
