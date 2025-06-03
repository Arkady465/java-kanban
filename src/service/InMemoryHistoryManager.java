package service;

import model.Task;

import java.util.*;

/**
 * Реализация HistoryManager с помощью двусвязного списка + HashMap<Task, Node>.
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

    private Node head;
    private Node tail;
    private final Map<Task, Node> nodeByTask = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (nodeByTask.containsKey(task)) {
            removeNode(nodeByTask.get(task));
            nodeByTask.remove(task);
        }
        Node newNode = new Node(task);
        linkLast(newNode);
        nodeByTask.put(task, newNode);
        if (nodeByTask.size() > MAX_HISTORY) {
            Node toRemove = head;
            removeNode(toRemove);
            nodeByTask.remove(toRemove.task);
        }
    }

    @Override
    public void remove(int id) {
        Task found = null;
        for (Task t : nodeByTask.keySet()) {
            if (t.getId() == id) {
                found = t;
                break;
            }
        }
        if (found != null) {
            Node node = nodeByTask.get(found);
            removeNode(node);
            nodeByTask.remove(found);
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
