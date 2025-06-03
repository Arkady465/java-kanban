package service;

import model.Task;

import java.util.*;

/**
 * Реализация HistoryManager с помощью двусвязного списка + HashMap<id, Node>.
 * Ограничиваем историю последними 10 элементами.
 */
public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY = 10;

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    // Голова (самая «древняя»), хвост (самая «поздняя»)
    private Node head;
    private Node tail;
    // Быстрый доступ: id → Node
    private final Map<Integer, Node> nodeById = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        // Если уже есть в истории — удаляем старый узел
        if (nodeById.containsKey(task.getId())) {
            removeNode(nodeById.get(task.getId()));
            nodeById.remove(task.getId());
        }
        // Вставляем в конец списка
        Node newNode = new Node(task);
        linkLast(newNode);
        nodeById.put(task.getId(), newNode);

        // Если длина истории превысила MAX_HISTORY, удаляем голову
        if (nodeById.size() > MAX_HISTORY) {
            Node toRemove = head;
            removeNode(toRemove);
            nodeById.remove(toRemove.task.getId());
        }
    }

    @Override
    public void remove(int id) {
        Node node = nodeById.get(id);
        if (node != null) {
            removeNode(node);
            nodeById.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node current = head;
        while (current != null) {
            result.add(current.task);
            current = current.next;
        }
        return result;
    }

    // Вспомогательные методы работы с двусвязным списком

    private void linkLast(Node node) {
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        node.prev = null;
        node.next = null;
    }
}
