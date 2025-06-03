package ru.yandex.todo.storage;

import ru.yandex.todo.manager.InMemoryTaskManager;
import ru.yandex.todo.manager.ManagerSaveException;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

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
        // Логика записи в CSV (все задачи, эпики, подзадачи, и история).
        // Должна открывать `file` на запись и последовательно сохранять:
        // 1) Все задачи (Task)
        // 2) Все эпики (Epic)
        // 3) Все подзадачи (Subtask)
        // 4) И, возможно, историю просмотров (если нужна)
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
