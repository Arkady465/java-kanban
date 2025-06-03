package service;

import exception.ManagerSaveException;
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
 * Формат CSV:
 *   Шапка:
 *     id,type,name,status,description,startTime,duration,endTime,epicID   (для Subtask последний столбец)
 *   Затем пустая строка + история (список id через запятую).
 */
public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FileBackedTaskManager(String filePath) {
        this(new File(filePath));
    }

    public FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFile();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    /**
     * Сохраняет в файл:
     *   1) Шапка CSV
     *   2) Все задачи, эпики, подзадачи как строки CSV
     *   3) Пустая строка
     *   4) История (список id через запятую)
     */
    private void save() {
        try (Writer writer = new FileWriter(file, false)) {
            // 1. Шапка
            writer.write("id,type,name,status,description,startTime,duration,endTime,epicID\n");
            // 2. Все Task
            for (Task t : getAllTasks()) {
                writer.write(toCsvLine(t));
                writer.write("\n");
            }
            // 3. Все Epic
            for (Epic e : getAllEpics()) {
                writer.write(toCsvLine(e));
                writer.write("\n");
            }
            // 4. Все Subtask
            for (Subtask s : getAllSubtasks()) {
                writer.write(toCsvLine(s));
                writer.write("\n");
            }
            // 5. Пустая строка, разделитель
            writer.write("\n");
            // 6. История
            writer.write(historyToString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + e.getMessage(), e);
        }
    }

    @Override
    public Task addTask(Task task) {
        Task result = super.addTask(task);
        save();
        return result;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic result = super.addEpic(epic);
        save();
        return result;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask result = super.addSubtask(subtask);
        save();
        return result;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    /**
     * Загружает данные из CSV:
     *   1) Парсит строки до пустой, добавляет задачи/эпики/подзадачи в менеджер (через super.add…)
     *   2) После пустой строки – строка истории: “1,3,5,...”, восстанавливает порядок вызовом getTask/getEpic/getSubtask
     */
    private void loadFromFile() {
        if (!file.exists()) {
            return;
        }
        Map<Integer, String> linesById = new LinkedHashMap<>();
        List<Integer> historyIds = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // 1) Читаем все строки до пустой
            String line = reader.readLine(); // шапка
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    // дальше идёт история
                    break;
                }
                // id,type,name,status,description,startTime,duration,endTime,epicID
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                linesById.put(id, line);
            }
            // 2) Читаем строку истории
            String histLine = reader.readLine(); // например “1,3,5”
            if (histLine != null && !histLine.isEmpty()) {
                String[] ids = histLine.split(",");
                for (String s : ids) {
                    historyIds.add(Integer.parseInt(s));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении из файла: " + e.getMessage(), e);
        }

        // 3) Восстанавливаем задачи/эпики/подзадачи в том же порядке
        for (String csvLine : linesById.values()) {
            TaskType type = TaskType.valueOf(csvLine.split(",")[1]);
            switch (type) {
                case TASK:
                    Task t = fromCsvLineToTask(csvLine);
                    super.addTask(t);
                    break;
                case EPIC:
                    Epic e = fromCsvLineToEpic(csvLine);
                    super.addEpic(e);
                    break;
                case SUBTASK:
                    Subtask s = fromCsvLineToSubtask(csvLine);
                    super.addSubtask(s);
                    break;
            }
        }
        // 4) Восстанавливаем историю, вызывая getTask/getEpic/getSubtask
        for (int id : historyIds) {
            if (tasksExists(id)) {
                super.getTask(id);
            } else if (epicExists(id)) {
                super.getEpic(id);
            } else if (subtaskExists(id)) {
                super.getSubtask(id);
            }
        }
        // После полной загрузки можно сохранить, чтобы сбросить файл (необязательно)
        // save();
    }

    private boolean tasksExists(int id) {
        try {
            return super.getTask(id) != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean epicExists(int id) {
        try {
            return super.getEpic(id) != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean subtaskExists(int id) {
        try {
            return super.getSubtask(id) != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    // Преобразование задачи/эпика/сабтаска ↔ CSV

    private String toCsvLine(Task task) {
        // id,type,name,status,description,startTime,duration,endTime,epicID
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(escapeCommas(task.getName())).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(escapeCommas(task.getDescription())).append(",");
        LocalDateTime st = task.getStartTime();
        sb.append(st == null ? "" : DATE_FORMATTER.format(st)).append(",");
        Duration dur = task.getDuration();
        sb.append(dur == null ? "" : dur.toMinutes()).append(",");
        LocalDateTime et = task.getEndTime();
        sb.append(et == null ? "" : DATE_FORMATTER.format(et));
        if (task instanceof Subtask) {
            sb.append(",").append(((Subtask) task).getEpicID());
        } else {
            sb.append(",");
        }
        return sb.toString();
    }

    private Task fromCsvLineToTask(String line) {
        // id,TASK,name,status,description,startTime,duration,endTime,
        String[] parts = line.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        String name = unescapeCommas(parts[2]);
        String description = unescapeCommas(parts[4]);
        Status status = Status.valueOf(parts[3]);
        LocalDateTime startTime = parts[5].isEmpty() ? null : LocalDateTime.parse(parts[5], DATE_FORMATTER);
        Duration duration = parts[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(parts[6]));
        return new Task(id, name, description, status, duration, startTime);
    }

    private Epic fromCsvLineToEpic(String line) {
        // id,EPIC,name,status,description,startTime,duration,endTime,
        String[] parts = line.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        String name = unescapeCommas(parts[2]);
        String description = unescapeCommas(parts[4]);
        Status status = Status.valueOf(parts[3]);
        LocalDateTime startTime = parts[5].isEmpty() ? null : LocalDateTime.parse(parts[5], DATE_FORMATTER);
        Duration duration = parts[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(parts[6]));
        Epic epic = new Epic(id, name, description, status, duration, startTime);
        return epic;
    }

    private Subtask fromCsvLineToSubtask(String line) {
        // id,SUBTASK,name,status,description,startTime,duration,endTime,epicID
        String[] parts = line.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        String name = unescapeCommas(parts[2]);
        String description = unescapeCommas(parts[4]);
        Status status = Status.valueOf(parts[3]);
        LocalDateTime startTime = parts[5].isEmpty() ? null : LocalDateTime.parse(parts[5], DATE_FORMATTER);
        Duration duration = parts[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(parts[6]));
        int epicId = Integer.parseInt(parts[8]);
        Subtask subtask = new Subtask(id, name, description, status, duration, startTime, epicId);
        return subtask;
    }

    private String historyToString() {
        List<Task> history = super.getHistory();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            sb.append(history.get(i).getId());
            if (i < history.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private String escapeCommas(String text) {
        return text == null ? "" : text.replace(",", "&#44;");
    }

    private String unescapeCommas(String text) {
        return text == null ? "" : text.replace("&#44;", ",");
    }
}
