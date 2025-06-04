package ru.yandex.todo.storage;

import ru.yandex.todo.manager.InMemoryTaskManagers;
import ru.yandex.todo.manager.ManagerSaveExceptions;
import ru.yandex.todo.model.Epics;
import ru.yandex.todo.model.Subtasks;
import ru.yandex.todo.model.Tasks;

import java.io.*;

public class FileBackedTaskManagers extends InMemoryTaskManagers {
    private final File file;

    public FileBackedTaskManagers(File file) {
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
                for (Tasks tasks : getAllTasks()) {
                    writer.write(toString(tasks) + "\n");
                }
                for (Epics epics : getAllEpics()) {
                    writer.write(toString(epics) + "\n");
                }
                for (Subtasks subtasks : getAllSubtasks()) {
                    writer.write(toString(subtasks) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveExceptions("Ошибка при сохранении в файл", e);
        }
    }

    // Остальные методы toString(), fromString(), loadFromFile() — по текущей логике проекта
}
