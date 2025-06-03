package ru.yandex.todo.manager;

import ru.yandex.todo.model.Task;

import java.util.*;

/**
 * Реализация истории через связный список и HashMap для O(1)-доступа и удаления.
 * Позволяет хранить неограниченную историю без дубликатов:
 *   если Task уже есть в истории, сначала убираем старый узел, затем добавляем в конец.
 */
public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeById = new HashMap<>();
    private Node head;
    private Node tail;

    // Внутренний класс узла двусвязного списка
    private static class Node {
        Task task;
        Node prev;
        Node next;
        Node(Task task) {
            this.task = task;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        // Если уже есть в истории – удаляем старый узел
        if (nodeById.containsKey(task.getId())) {
            removeNode(nodeById.get(task.getId()));
        }
        // Добавляем в конец списка
        linkLast(task);
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

    // ===== Вспомогательные методы для двусвязного списка =====

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
        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }

        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }
    }
}
