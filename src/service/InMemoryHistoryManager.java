package service;

import model.Task;

import java.util.*;

/**
 * Упрощённая реализация HistoryManager:
 * Хранит до последних 10 уникальных Task (по id).
 * Если добавляем тот же task повторно, удаляем старый и добавляем новый в конец.
 */
public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY = 10;
    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        history.removeIf(t -> t.getId() == task.getId());
        history.addLast(task);
        if (history.size() > MAX_HISTORY) {
            history.removeFirst();
        }
    }

    @Override
    public void remove(int id) {
        history.removeIf(t -> t.getId() == id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
