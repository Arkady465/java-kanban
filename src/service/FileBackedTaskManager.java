package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskType;
import model.Status;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * FileBackedTaskManager сохраняет все задачи в CSV-файл и умеет их загружать обратно.
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FileBackedTaskManager(String filename) {
        this.file = new File(filename);
    }

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,duration,startTime,epic\n");

            // Сохранить обычные задачи
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            // Сохранить эпики
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            // Сохранить подзадачи
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

            writer.write("\n");
            writer.write(historyToString(getHistory()));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении в файл: " + e.getMessage(), e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // строчка с заголовком
            Map<Integer, Task> idToTask = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                Task task = fromString(line);
                if (task.getType() == TaskType.TASK) {
                    manager.tasks.put(task.getId(), task);
                } else if (task.getType() == TaskType.EPIC) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    Subtask sub = (Subtask) task;
                    manager.subtasks.put(sub.getId(), sub);
                }
                idToTask.put(task.getId(), task);
                if (task.getId() >= manager.idCounter) {
                    manager.idCounter = task.getId() + 1;
                }
            }
            // Связать подзадачи с эпиками
            for (Subtask sub : manager.subtasks.values()) {
                Epic epic = manager.epics.get(sub.getEpicID());
                epic.addSubtask(sub);
            }
            // Считать историю
            if ((line = reader.readLine()) != null) {
                List<Integer> historyIds = historyFromString(line);
                for (int id : historyIds) {
                    Task t = idToTask.get(id);
                    if (t != null) {
                        manager.historyManager.add(t);
                    }
                }
            }
            // Заполнить приоритеты
            for (Task t : manager.tasks.values()) {
                manager.prioritizedSet.add(t);
            }
            for (Subtask s : manager.subtasks.values()) {
                manager.prioritizedSet.add(s);
            }
            return manager;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении из файла: " + e.getMessage(), e);
        }
    }

    private String toString(Task task) {
        // Формат CSV: id,type,name,status,description,duration,startTime,epic
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId())
                .append(",")
                .append(task.getType())
                .append(",")
                .append(escapeCommas(task.getName()))
                .append(",")
                .append(task.getStatus())
                .append(",")
                .append(escapeCommas(task.getDescription()))
                .append(",")
                .append(task.getDuration() != null ? task.getDuration().toMinutes() : "")
                .append(",")
                .append(task.getStartTime() != null ? task.getStartTime().format(formatter) : "")
                .append(",");
        if (task.getType() == TaskType.SUBTASK) {
            sb.append(((Subtask) task).getEpicID());
        }
        return sb.toString();
    }

    private Task fromString(String value) {
        // Парсим CSV: id,type,name,status,description,duration,startTime,epic
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = unescapeCommas(parts[2]);
        Status status = Status.valueOf(parts[3]);
        String description = unescapeCommas(parts[4]);
        Long durationMinutes = parts[5].isEmpty() ? null : Long.parseLong(parts[5]);
        Duration duration = durationMinutes != null ? Duration.ofMinutes(durationMinutes) : null;
        LocalDateTime startTime = parts[6].isEmpty() ? null : LocalDateTime.parse(parts[6], formatter);

        if (type == TaskType.TASK) {
            return new Task(id, name, description, status, duration, startTime);
        } else if (type == TaskType.EPIC) {
            return new Epic(id, name, description, status, duration, startTime);
        } else {
            int epicID = Integer.parseInt(parts[7]);
            return new Subtask(id, name, description, status, duration, startTime, epicID);
        }
    }

    private String historyToString(List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        for (Task t : tasks) {
            sb.append(t.getId()).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // убрать последнюю запятую
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> result = new ArrayList<>();
        if (value == null || value.isEmpty()) {
            return result;
        }
        String[] parts = value.split(",");
        for (String p : parts) {
            result.add(Integer.parseInt(p));
        }
        return result;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedSet);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
    }

    private String escapeCommas(String text) {
        return text == null ? "" : text.replace(",", "&#44;");
    }

    private String unescapeCommas(String text) {
        return text == null ? "" : text.replace("&#44;", ",");
    }
}
