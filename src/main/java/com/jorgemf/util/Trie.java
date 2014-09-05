package com.jorgemf.util;

import java.util.*;

public class Trie<k> implements Tree<k> {

    private int maxDepth;

    private Node root = new Node(null, -1);

    private HashMap<k, Integer> eventNamesMap = new HashMap<k, Integer>();

    private Vector<k> eventNamesVector = new Vector<k>();

    private LinkedList<Integer> sequence = new LinkedList<Integer>();

    private int size = 0;

    private int depth = 0;

    private int totalCounter = 0;

    public Trie() {
        this(0);
    }

    public Trie(int maximumDepth) {
        this.maxDepth = maximumDepth;
    }

    public void add(Collection<k> events) {
        if (maxDepth > 0 && events.size() > maxDepth) {
            throw new RuntimeException("Sequence is longer than expected");
        }
        depth = Math.max(depth, events.size());
        Node currentNode = root;
        for (k event : events) {
            currentNode = getOrCreateChildNode(currentNode, getKey(event));
            currentNode.increaseCounter();
            totalCounter++;
        }
    }

    public void addToSequence(k event) {
        if (maxDepth <= 0) {
            throw new RuntimeException("sequences requires a limit in depth");
        }
        sequence.addLast(getKey(event));
        if (sequence.size() > maxDepth) {
            sequence.removeFirst();
        } else {
            depth = Math.max(depth, sequence.size());
        }
        Node currentNode = root;
        for (int keys : sequence) {
            currentNode = getOrCreateChildNode(currentNode, keys);
            currentNode.increaseCounter();
            totalCounter++;
        }
    }

    public void restartSequence() {
        sequence.clear();
    }

    private Node getOrCreateChildNode(Node currentNode, int key) {
        if (!currentNode.containsChild(key)) {
            size++;
            return currentNode.createChild(key);
        } else {
            return currentNode.getChild(key);
        }
    }

    private int getKey(k event) {
        int key;
        if (eventNamesMap.containsKey(event)) {
            key = eventNamesMap.get(event);
        } else {
            eventNamesVector.add(event);
            key = eventNamesVector.size() - 1;
            eventNamesMap.put(event, key);
        }
        return key;
    }

    @Override
    public void visitPreOrder(TreeVisitor<k> visitor) {
        for (Node n : root.getChildren()) {
            visitPreOrder(visitor, n);
        }
    }

    private void visitPreOrder(TreeVisitor<k> visitor, Node node) {
        visitor.visit(eventNamesVector.get(node.getKeyEvent()), node.depth, node.getChildren().size());
        for (Node n : node.getChildren()) {
            visitPreOrder(visitor, n);
        }
    }

    @Override
    public void visitPostOrder(TreeVisitor<k> visitor) {
        for (Node n : root.getChildren()) {
            visitPostOrder(visitor, n);
        }
    }

    private void visitPostOrder(TreeVisitor<k> visitor, Node node) {
        for (Node n : node.getChildren()) {
            visitPostOrder(visitor, n);
        }
        visitor.visit(eventNamesVector.get(node.getKeyEvent()), node.depth, node.getChildren().size());
    }

    @Override
    public void visitInOrder(TreeVisitor<k> visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitBreadth(TreeVisitor<k> visitor) {
        Queue<Node> nodesQueue = new LinkedList<Node>();
        nodesQueue.addAll(root.getChildren());
        nodesQueue.add(null);
        Node head;
        Collection<Node> children;
        int childrenSize;
        while (!nodesQueue.isEmpty()) {
            head = nodesQueue.remove();
            children = head.getChildren();
            childrenSize = children.size();
            if (childrenSize > 0) {
                nodesQueue.addAll(children);
            }
            visitor.visit(eventNamesVector.get(head.getKeyEvent()), head.getDepth(), childrenSize);
        }
    }

    public int getSize() {
        return size;
    }

    public int getDepth() {
        return depth;
    }

    public int getTotalCounter() {
        return totalCounter;
    }

    class Node {

        private int keyEvent;

        private Node parent;

        private TreeMap<Integer, Node> childrend;

        private int counter;

        private int depth;

        Node(Node parent, int keyEvent) {
            childrend = new TreeMap<Integer, Node>();
            this.keyEvent = keyEvent;
            counter = 0;
            this.parent = parent;
            if (parent != null) {
                depth = parent.depth + 1;
            } else {
                depth = -1;
            }
        }

        private void increaseCounter() {
            counter++;
        }

        private int getCounter() {
            return counter;
        }

        private boolean containsChild(int key) {
            return childrend.containsKey(key);
        }

        private Node getChild(int key) {
            return childrend.get(key);
        }

        private Node createChild(int key) {
            Node node = new Node(this, key);
            childrend.put(key, node);
            return node;
        }

        private int getKeyEvent() {
            return keyEvent;
        }

        private Node getParent() {
            return parent;
        }

        private Collection<Node> getChildren() {
            return childrend.values();
        }

        private int getDepth() {
            return depth;
        }
    }
}
