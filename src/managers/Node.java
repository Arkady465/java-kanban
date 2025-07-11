package managers;

public class Node<T> {
    private T node;

    private Node<T> nextNode;

    private Node<T> prevNode;

    public Node(Node<T> prevNode, T node, Node<T> nextNode) {
        this.prevNode = prevNode;
        this.node = node;
        this.nextNode = nextNode;
    }

    public T getNode() {
        return node;
    }

    public void setNode(T node) {
        this.node = node;
    }

    public Node<T> getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node<T> nextNode) {
        this.nextNode = nextNode;
    }

    public Node<T> getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(Node<T> prevNode) {
        this.prevNode = prevNode;
    }
}
