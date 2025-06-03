package service;

import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;

/**
 * Пример класса, который сохраняет задачи в CSV‐файл и восстанавливает их.
 * Он наследует InMemoryTaskManager и добавляет сохранение/загрузку.
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(String filePath) {
        this.file = new File(filePath);
        loadFromFile();
    }

    private void loadFromFile() {
        // Логика чтения из CSV и восстановления задач, эпиков, подзадач, истории...
        // Здесь нужно читать из файла `file` и заполнять внутренние структуры InMemoryTaskManager.
    }

    private void save() {
        try (Writer writer = new FileWriter(file, false)) {
            // Вместо getAllTasks() теперь используем getTasks()
            // getTasks() возвращает List<Task>, поэтому его можно обойти в for-each
            for (Task task : getTasks()) {
                writer.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    @Override
    public Task createTask(Task task) throws ManagerSaveException {
        Task result = super.createTask(task);
        save();
        return result;
    }

    @Override
    public Task updateTask(Task task) throws ManagerSaveException {
        Task result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws ManagerSaveException {
        Subtask result = super.createSubtask(subtask);
        save();
        return result;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws ManagerSaveException {
        Subtask result = super.updateSubtask(subtask);
        save();
        return result;
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public Epic createEpic(Epic epic) throws ManagerSaveException {
        Epic result = super.createEpic(epic);
        save();
        return result;
    }

    @Override
    public Epic updateEpic(Epic epic) throws ManagerSaveException {
        Epic result = super.updateEpic(epic);
        save();
        return result;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }
}
