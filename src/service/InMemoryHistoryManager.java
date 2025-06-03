package service;

import model.Task;

import java.util.*;

/**
 * Упрощённая реализация HistoryManager:
 * Хранит до последних 10 уникальных Task (по id).
 * Если добавляем тот же task вторично, убираем старое в списке и заново добавляем в конец.
 */
public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY = 10;
    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        // Если в списке уже был этот task (с тем же id), удаляем старый вариант
        history.removeIf(t -> t.getId() == task.getId());
        // Добавляем в конец
        history.addLast(task);
        // Если стали длиннее 10, удаляем из головы
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
