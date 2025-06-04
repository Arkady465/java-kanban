package ru.yandex.todo.manager;

import ru.yandex.todo.model.Tasks;

import java.util.*;

public class InMemoryHistoryManagers implements HistoryManagers {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    private static class Node {
        Tasks tasks;
        Node prev;
        Node next;

        Node(Tasks tasks) {
            this.tasks = tasks;
        }
    }

    @Override
    public void add(Tasks tasks) {
        if (nodeMap.containsKey(tasks.getId())) {
            remove(tasks.getId());
        }
        linkLast(tasks);
        nodeMap.put(tasks.getId(), tail);

        if (nodeMap.size() > 10) {
            remove(head.tasks.getId());
        }
    }

    @Override
    public List<Tasks> getHistory() {
        List<Tasks> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.tasks);
            current = current.next;
        }
        return history;
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private void linkLast(Tasks tasks) {
        Node newNode = new Node(tasks);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    private void removeNode(Node node) {
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
    }
}
