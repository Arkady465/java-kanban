package ru.yandex.todo.manager;

import ru.yandex.todo.model.Task;

import java.util.List;

/**
 * Интерфейс для истории просмотров.
 */
public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(int id);
}
