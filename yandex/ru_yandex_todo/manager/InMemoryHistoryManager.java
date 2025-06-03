package ru_yandex_todo.manager;

import model.Task;

import java.util.*;

/**
 * Реализация истории просмотров через двусвязный список и HashMap,
 * обеспечивающая O(1) добавление, удаление, получение.
 * Из истории удаляется старая запись, если та же задача просматривается вновь.
 */
public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeById = new HashMap<>();
    private Node head;
    private Node tail;

    // Внутренний узел двусвязного списка
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    public InMemoryHistoryManager() {
        // Конструктор без параметров
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (nodeById.containsKey(task.getId())) {
            removeNode(nodeById.get(task.getId()));
        }
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

    // Добавляет новый узел в конец списка
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

    // Удаляет узел из двусвязного списка
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
