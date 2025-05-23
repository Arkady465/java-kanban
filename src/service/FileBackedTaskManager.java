package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import model.TaskType;
import exception.ManagerSaveException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

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
        try (BufferedReader reader = new BufferedReader(
                new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = fromString(line);
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

    // Метод десериализации с учетом новых полей:
    // Task: id, TASK, name, status, description, startTime, duration
    // Epic: id, EPIC, name, status, description, startTime, duration, endTime
    // Subtask: id, SUBTASK, name, status, description, startTime, duration, epicId
    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startTime = null;
        Duration duration = null;
        if (parts.length > 5 && !parts[5].equals("null")) {
            startTime = LocalDateTime.parse(parts[5]);
        }
        if (parts.length > 6 && !parts[6].equals("null")) {
            duration = Duration.ofMinutes(Long.parseLong(parts[6]));
        }
        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[7]);
                task = new Subtask(name, description, epicId);
                break;
            default:
                throw new IllegalArgumentException("Unknown task type");
        }
        task.setId(id);
        task.setStatus(status);
        task.setStartTime(startTime);
        task.setDuration(duration);
        if (type == TaskType.EPIC && parts.length > 7) {
            Epic epic = (Epic) task;
            if (!parts[7].equals("null")) {
                epic.setEndTime(LocalDateTime.parse(parts[7]));
            }
        }
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
