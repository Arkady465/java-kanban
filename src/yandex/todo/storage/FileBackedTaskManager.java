package todo.storage;

import ru.yandex.todo.manager.InMemoryTaskManager;
import ru.yandex.todo.manager.ManagerSaveException;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void save() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("id,type,name,status,description,startTime,duration,epic\n");
                for (Task task : getAllTasks()) {
                    writer.write(toString(task) + "\n");
                }
                for (Epic epic : getAllEpics()) {
                    writer.write(toString(epic) + "\n");
                }
                for (Subtask subtask : getAllSubtasks()) {
                    writer.write(toString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    // Остальные методы toString(), fromString(), loadFromFile() — по текущей логике проекта
}
