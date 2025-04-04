package service;

import model.*;
import exception.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            for (Task task : getAllTasks()) {
                writer.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving tasks to file", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = Managers.getFileBacked(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = fromString(line);
                // Добавляем задачу в зависимости от типа
                if (task.getType() == TaskType.TASK) {
                    manager.addTask(task);
                } else if (task.getType() == TaskType.EPIC) {
                    manager.addEpic((Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    manager.addSubtask((Subtask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error loading tasks from file", e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                task = new Subtask(name, description, epicId);
                break;
            default:
                throw new IllegalArgumentException("Unknown task type");
        }
        task.setId(id);
        task.setStatus(status);
        return task;
    }

    @Override
    public Task addTask(Task task) {
        Task t = super.addTask(task);
        save();
        return t;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic e = super.addEpic(epic);
        save();
        return e;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask s = super.addSubtask(subtask);
        save();
        return s;
    }

    @Override
    public Task updateTask(Task task) {
        Task t = super.updateTask(task);
        save();
        return t;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask s = super.updateSubtask(subtask);
        save();
        return s;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic e = super.updateEpic(epic);
        save();
        return e;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }
}
