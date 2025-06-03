package service;

import model.Task;

import java.util.*;

/**
 * Реализация истории просмотров через двусвязный список и HashMap,
 * чтобы операции add/remove/getHistory были O(1).
 *
 * При переполнении истории (в тестах проверено, что не больше 10 элементов) –
 * оставляем только последние 10. Остальные выбрасываем из начала.
 */
public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeById = new HashMap<>();
    private Node head;
    private Node tail;
    private static final int MAX_HISTORY = 10;

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    public InMemoryHistoryManager() {
        // пустой конструктор
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (nodeById.containsKey(task.getId())) {
            removeNode(nodeById.get(task.getId()));
        }
        linkLast(task);

        // Если после добавления длина истории > 10, удаляем самый старый
        if (nodeById.size() > MAX_HISTORY) {
            // head указывает на самый первый
            if (head != null) {
                removeNode(head);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    @Override
    public void remove(int id) {
        Node node = nodeById.get(id);
        if (node != null) {
            removeNode(node);
            nodeById.remove(id);
        }
    }

    private void linkLast(Task task) {
        Node node = new Node(task);
        nodeById.put(task.getId(), node);
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }
    }
}
