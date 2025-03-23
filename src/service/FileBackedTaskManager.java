package service;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    private boolean autoSaveEnabled = true;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,epic").append(System.lineSeparator());
        for (Task task : getAllTasks()) {
            sb.append(toCsvString(task)).append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        List<Task> history = getHistory();
        if (!history.isEmpty()) {
            List<String> historyIds = new ArrayList<>();
            for (Task task : history) {
                historyIds.add(String.valueOf(task.getId()));
            }
            sb.append(String.join(",", historyIds));
        }
        try {
            Files.writeString(file.toPath(), sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задач в файл", e);
        }
    }

    private String toCsvString(Task task) {
        String type;
        if (task instanceof Epic) {
            type = "EPIC";
        } else if (task instanceof Subtask) {
            type = "SUBTASK";
        } else {
            type = "TASK";
        }
        String epicField = "";
        if (task instanceof Subtask) {
            epicField = String.valueOf(((Subtask) task).getEpicID());
        }
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epicField);
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        String statusStr = parts[3];
        String description = parts[4];
        Status status = Status.valueOf(statusStr);
        Task task;
        switch (type) {
            case "TASK":
                task = new Task(name, description);
                break;
            case "EPIC":
                task = new Epic(name, description);
                break;
            case "SUBTASK":
                int epicId = Integer.parseInt(parts[5]);
                task = new Subtask(name, description, epicId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        task.setId(id);
        task.setStatus(status);
        return task;
    }

    @Override
    public Task addTask(Task task) {
        Task t = super.addTask(task);
        if (autoSaveEnabled) {
            save();
        }
        return t;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic e = super.addEpic(epic);
        if (autoSaveEnabled) {
            save();
        }
        return e;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask s = super.addSubtask(subtask);
        if (autoSaveEnabled) {
            save();
        }
        return s;
    }

    @Override
    public Task updateTask(Task task) {
        Task t = super.updateTask(task);
        if (autoSaveEnabled) {
            save();
        }
        return t;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic e = super.updateEpic(epic);
        if (autoSaveEnabled) {
            save();
        }
        return e;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask s = super.updateSubtask(subtask);
        if (autoSaveEnabled) {
            save();
        }
        return s;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        if (autoSaveEnabled) {
            save();
        }
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        if (autoSaveEnabled) {
            save();
        }
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        if (autoSaveEnabled) {
            save();
        }
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        if (autoSaveEnabled) {
            save();
        }
    }

    private void restoreTask(Task task) {
        try {
            Field tasksField = InMemoryTaskManager.class.getDeclaredField("tasks");
            tasksField.setAccessible(true);
            Map<Integer, Task> tasksMap = (Map<Integer, Task>) tasksField.get(this);
            tasksMap.put(task.getId(), task);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Ошибка восстановления задачи", e);
        }
    }

    private void setIdCounter(int value) {
        try {
            Field field = InMemoryTaskManager.class.getDeclaredField("idCounter");
            field.setAccessible(true);
            field.setInt(this, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Ошибка установки id-счётчика", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.autoSaveEnabled = false;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                return manager;
            }
            int index = 1;
            int maxId = 0;
            List<String> taskLines = new ArrayList<>();
            // Читаем строки с задачами до пустой строки
            while (index < lines.size() && !lines.get(index).isBlank()) {
                taskLines.add(lines.get(index));
                String[] parts = lines.get(index).split(",");
                int id = Integer.parseInt(parts[0]);
                if (id > maxId) {
                    maxId = id;
                }
                index++;
            }
            List<Task> subtasks = new ArrayList<>();
            for (String line : taskLines) {
                Task task = fromString(line);
                if (task instanceof Subtask) {
                    subtasks.add(task);
                } else {
                    manager.restoreTask(task);
                }
            }
            for (Task task : subtasks) {
                manager.restoreTask(task);
                if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    Task epicTask = manager.getTask(subtask.getEpicID());
                    if (epicTask instanceof Epic) {
                        ((Epic) epicTask).addSubtask(subtask);
                    }
                }
            }
            if (index < lines.size() - 1) {
                String historyLine = lines.get(index + 1);
                if (!historyLine.isBlank()) {
                    String[] ids = historyLine.split(",");
                    for (String idStr : ids) {
                        int id = Integer.parseInt(idStr);
                        manager.getTask(id);
                    }
                }
            }
            manager.setIdCounter(maxId);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки задач из файла", e);
        }
        manager.autoSaveEnabled = true;
        return manager;
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
