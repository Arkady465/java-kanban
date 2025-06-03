package todo.storage;

import ru.yandex.todo.manager.InMemoryTaskManager;
import ru.yandex.todo.manager.ManagerSaveException;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.io.*;

/**
 * FileBackedTaskManager умеет сохранять задачи в CSV-файл и восстанавливать из него.
 * Наша реализация наследует InMemoryTaskManager и добавляет:
 * 1) конструктор, принимающий не только строку, но и File;
 * 2) статический метод loadFromFile(File), возвращающий экземпляр;
 * 3) вызов save() после каждого изменения.
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    /**
     * Старый конструктор, на случай, если где-то всё ещё передаётся String.
     */
    public FileBackedTaskManager(String filePath) {
        this(new File(filePath));
    }

    /**
     * Новый конструктор: принимаем уже File.
     */
    public FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFile(); // считываем текущее состояние
    }

    /**
     * Статический метод загрузки из файла (чтобы тесты, вызывающие loadFromFile(File), работали).
     */
    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    /**
     * Читаем CSV и восстанавливаем состояние InMemoryTaskManager:
     * - Восстанавливаем все задачи, эпики, подзадачи;
     * - Восстанавливаем историю (если требуется).
     */
    private void loadFromFile() {
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Здесь пишем вашу логику: читаем построчно CSV, разбираем в строки,
            // создаём объекты Task, Epic, Subtask и вызываем super.createXXX(...)
            // после этого, возможно, восстанавливаем историю.
            // Для краткости примера оставим скелет:
            String line;
            while ((line = reader.readLine()) != null) {
                // Пример: line.split(",") и конструируем нужный объект
                // super.createTask(…); super.createEpic(…); super.createSubtask(…);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении из файла: " + e.getMessage(), e);
        }
    }

    /**
     * Сохраняем текущее состояние (все задачи, эпики, подзадачи и, при необходимости, историю) в файл.
     */
    private void save() {
        try (Writer writer = new FileWriter(file, false)) {
            // Предполагаем, что getTasks(), getEpics(), getSubtasks() возвращают List<…>.
            // Итерируемся по ним и записываем каждую сущность в CSV-строку, например:
            for (Task task : getTasks()) {
                writer.write(task.toString() + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(epic.toString() + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(subtask.toString() + "\n");
            }
            // Если требуется – сохраняем историю (можно отдельно).
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + e.getMessage(), e);
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
