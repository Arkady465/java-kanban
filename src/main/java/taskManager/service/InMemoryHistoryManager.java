package taskManager.service;

import taskManager.model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        // Просто добавляем задачу в конец истории
        history.add(task);
        // Если количество задач превышает 10, удаляем самые старые.
        while (history.size() > 10) {
            history.remove(0);
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
