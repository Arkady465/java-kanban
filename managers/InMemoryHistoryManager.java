package managers;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> nodeHashMap = new HashMap<>();
    private Node<Task> headNode;
    private Node<Task> tailNode;

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            overwrite(task);
        }
    }

    private void overwrite(Task task) {
        Node<Task> oldTail = tailNode;
        Node<Task> newNode = new Node<>(oldTail, task, null);
        tailNode = newNode;

        if (oldTail == null) {
            headNode = newNode;
        } else {
            oldTail.setNextNode(newNode);
        }

        nodeHashMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodeHashMap.get(id);

        if (node != null) {
            if (headNode == node && tailNode == node) {
                headNode = null;
                tailNode = null;
            } else if (headNode == node) {
                headNode = node.getNextNode();
                headNode.setPrevNode(null);
            } else if (tailNode == node) {
                tailNode = node.getPrevNode();
                tailNode.setNextNode(null);
            } else {
                node.getPrevNode().setNextNode(node.getNextNode());
                node.getNextNode().setPrevNode(node.getPrevNode());
            }

            node.setNode(null);
            node.setPrevNode(null);
            node.setNextNode(null);
            nodeHashMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> node = headNode;
        while (node != null) {
            tasks.add(node.getNode());
            node = node.getNextNode();
        }
        return tasks;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "nodeHashMap=" + nodeHashMap +
                ", headNode=" + headNode +
                ", tailNode=" + tailNode +
                '}';
    }
}
