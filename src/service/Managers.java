package service;

/**
 * Фабрика для удобного создания TaskManager.
 */
public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefaultFileBacked(String filePath) {
        return new FileBackedTaskManager(filePath);
    }
}
