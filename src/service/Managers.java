package service;

import model.Task;

import java.io.File;

/**
 * Фабрика менеджеров. Тесты ожидают:
 *  - Managers.getDefault() возвращает “на лету” новый InMemoryTaskManager
 *  - Managers.getDefaultHistory() возвращает InMemoryHistoryManager
 *  - Managers.getDefaultTaskManager() возвращает InMemoryTaskManager
 *
 * Если тесты “прямо” не используют какой‐то метод – это не критично,
 * но добавим всё, что может понадобиться.
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
}
