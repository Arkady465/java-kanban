package service;

import model.Task;

import java.util.List;

/**
 * Интерфейс менеджера истории просмотров.
 * Тесты ожидают:
 *  - void add(Task task);
 *  - List<Task> getHistory();
 *  - void remove(int id);
 */
public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

    void remove(int id);
}
