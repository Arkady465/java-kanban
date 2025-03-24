package service;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;
import model.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private boolean autoSaveEnabled = true;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // Переопределённые методы изменения состояния – после базовой логики вызывается save()

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

    // Метод загрузки менеджера из файла с использованием BufferedReader
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.autoSaveEnabled = false;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String header = reader.readLine(); // читаем заголовок
            if (header == null) {
                return manager;
            }
            String line;
            int maxId = 0;
            List<String> taskLines = new ArrayList<>();
            // Читаем строки с задачами до пустой строки
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                taskLines.add(line);
                String[] parts = line.split(",", -1);
                int id = Integer.parseInt(parts[0]);
                if (id > maxId) {
                    maxId = id;
                }
            }
            // Сначала добавляем задачи и эпики
            List<String> subtaskLines = new ArrayList<>();
            for (String taskLine : taskLines) {
                String[] parts = taskLine.split(",", -1);
                TaskType type = TaskType.valueOf(parts[1]);
                if (type == TaskType.SUBTASK) {
                    subtaskLines.add(taskLine);
                } else {
                    Task task = fromString(taskLine);
                    manager.addRestoredTask(task);
                }
            }
            // Затем добавляем подзадачи
            for (String taskLine : subtaskLines) {
                Task task = fromString(taskLine);
                manager.addRestoredTask(task);
                if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    Task epicTask = manager.tasks.get(subtask.getEpicID());
                    if (epicTask instanceof Epic) {
                        ((Epic) epicTask).addSubtask(subtask);
                    }
                }
            }
            // Читаем историю (если она есть)
            String historyLine = reader.readLine();
            if (historyLine != null && !historyLine.isBlank()) {
                String[] historyIds = historyLine.split(",");
                for (String idStr : historyIds) {
                    int id = Integer.parseInt(idStr);
                    manager.getTask(id); // добавляет задачу в историю
                }
            }
            manager.setIdCounter(maxId);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки задач из файла", e);
        }
        manager.autoSaveEnabled = true;
        return manager;
    }

    // Приватный метод сохранения состояния менеджера в файл с использованием BufferedWriter
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
            for (Task task : getAllTasks()) {
                writer.write(task.toCsvString());
                writer.newLine();
            }
            writer.newLine();
            List<Task> history = getHistory();
            if (!history.isEmpty()) {
                List<String> historyIds = new ArrayList<>();
                for (Task task : history) {
                    historyIds.add(String.valueOf(task.getId()));
                }
                writer.write(String.join(",", historyIds));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задач в файл", e);
        }
    }

    // Приватный метод создания задачи из строки CSV
    private static Task fromString(String value) {
        String[] parts = value.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        Task task;
        if (type == TaskType.TASK) {
            task = new Task(name, description);
        } else if (type == TaskType.EPIC) {
            task = new Epic(name, description);
        } else if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(parts[5]);
            task = new Subtask(name, description, epicId);
        } else {
            throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        task.setId(id);
        task.setStatus(status);
        return task;
    }
}
