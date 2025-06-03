package todo.manager;

import model.Task;

import java.util.List;

/**
 * Интерфейс для менеджера истории просмотров.
 */
public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

    void remove(int id);
}
