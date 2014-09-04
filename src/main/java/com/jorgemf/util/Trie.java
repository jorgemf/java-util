package com.jorgemf.util;

import java.util.*;

public class Trie<k> {

    private int maxDepth;

    private Node root;

    private HashMap<k, Integer> eventNamesMap;

    private Vector<k> eventNamesVector;

    private LinkedList<Node<k>> sequence;

    public Trie() {
        this(0);
    }

    public Trie(int maximumDepth) {
        this.maxDepth = maximumDepth;
        root = new Node(null, -1);
        eventNamesMap = new HashMap<k, Integer>();
        eventNamesVector = new Vector<k>();
    }

    public void add(Collection<k> events) {
        // TODO
    }

    public void addToSequence(k event) {
        if (maxDepth <= 0) {
            throw new RuntimeException("sequences requires a limit in depth");
        }
        if (sequence.size() > maxDepth) {
            sequence.removeFirst();
        }
        // TODO
    }

    public void restartSequence() {
        // TODO
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

    class Node<k> {

        private int keyEvent;

        private Node parent;

        private TreeSet<Node<k>> childs;

        private int timesAccesed;

        Node(Node parent, int keyEvent) {
            childs = new TreeSet<Node<k>>();
            this.keyEvent = keyEvent;
            timesAccesed = 0;
        }

    }
}
