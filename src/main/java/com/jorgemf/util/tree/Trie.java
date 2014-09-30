package com.jorgemf.util.tree;

import java.util.*;

public class Trie<k> {

    private int maxDepth;

    private TrieNode root = new TrieNode(null, -1);

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
        TrieNode currentNode = root;
        for (k event : events) {
            currentNode = getOrCreateChildNode(currentNode, getKey(event));
            currentNode.increaseCounter();
            totalCounter++;
        }
    }

    public void addToSequence(k event) {
        if (maxDepth <= 0) {
            throw new RuntimeException("Sequences require a limit in depth");
        }
        sequence.addLast(getKey(event));
        if (sequence.size() > maxDepth) {
            sequence.removeFirst();
        } else {
            depth = Math.max(depth, sequence.size());
        }
        TrieNode currentNode = root;
        for (int keys : sequence) {
            currentNode = getOrCreateChildNode(currentNode, keys);
            currentNode.increaseCounter();
            totalCounter++;
        }
    }

    public void restartSequence() {
        sequence.clear();
    }

    private TrieNode getOrCreateChildNode(TrieNode currentNode, int key) {
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

    public void visitPreOrder(TrieVisitor<k> visitor) {
        for (TrieNode n : root.getChildren()) {
            visitPreOrder(visitor, n);
        }
    }

    private void visitPreOrder(TrieVisitor<k> visitor, TrieNode node) {
        visitor.visit(eventNamesVector.get(node.getKeyEvent()), node.getDepth(), node.getChildren().size());
        for (TrieNode n : node.getChildren()) {
            visitPreOrder(visitor, n);
        }
    }

    public void visitPostOrder(TrieVisitor<k> visitor) {
        for (TrieNode n : root.getChildren()) {
            visitPostOrder(visitor, n);
        }
    }

    private void visitPostOrder(TrieVisitor<k> visitor, TrieNode node) {
        for (TrieNode n : node.getChildren()) {
            visitPostOrder(visitor, n);
        }
        visitor.visit(eventNamesVector.get(node.getKeyEvent()), node.getDepth(), node.getChildren().size());
    }

    public void visitBreadth(TrieVisitor<k> visitor) {
        Queue<TrieNode> nodesQueue = new LinkedList<TrieNode>();
        nodesQueue.addAll(root.getChildren());
        nodesQueue.add(null);
        TrieNode head;
        Collection<TrieNode> children;
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


    class TrieNode {

        private int keyEvent;

        private TrieNode parent;

        private TreeMap<Integer, TrieNode> childrend;

        private int counter;

        private int depth;

        TrieNode(TrieNode parent, int keyEvent) {
            childrend = new TreeMap<Integer, TrieNode>();
            this.keyEvent = keyEvent;
            counter = 0;
            this.parent = parent;
            if (parent != null) {
                depth = parent.depth + 1;
            } else {
                depth = -1;
            }
        }

        void increaseCounter() {
            counter++;
        }

        int getCounter() {
            return counter;
        }

        boolean containsChild(int key) {
            return childrend.containsKey(key);
        }

        TrieNode getChild(int key) {
            return childrend.get(key);
        }

        TrieNode createChild(int key) {
            TrieNode node = new TrieNode(this, key);
            childrend.put(key, node);
            return node;
        }

        int getKeyEvent() {
            return keyEvent;
        }

        TrieNode getParent() {
            return parent;
        }

        Collection<TrieNode> getChildren() {
            return childrend.values();
        }

        int getDepth() {
            return depth;
        }
    }

}
