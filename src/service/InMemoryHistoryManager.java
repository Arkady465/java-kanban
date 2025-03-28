package service;

import model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        // Добавляем задачу в конец истории
        history.add(task);
        // Если количество задач превышает 10, удаляем самые старые
        while (history.size() > 10) {
            history.remove(0);
        }
    }

    @Override
    public void remove(int id) {
        // Удаляет первую найденную задачу с данным id.
        // Обратите внимание: если задачи не имеют уникального id, этот метод может удалять не то, что ожидается.
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).getId() == id) {
                history.remove(i);
                break;
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}

