package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;


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

    private void loadFromFile() {
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении из файла: " + e.getMessage(), e);
        }
    }

    private void save() {
        try (Writer writer = new FileWriter(file, false)) {
            // Сохраняем сначала все задачи
            for (Task t : getAllTasks()) {
                writer.write(t.toString() + "\n");
            }
            // Затем все эпики
            for (Epic e : getAllEpics()) {
                writer.write(e.toString() + "\n");
            }
            // Затем все подзадачи
            for (Subtask s : getAllSubtasks()) {
                writer.write(s.toString() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении в файл: " + e.getMessage(), e);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }
}
