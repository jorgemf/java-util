package com.jorgemf.util.tree;

import java.util.Collection;
import java.util.TreeMap;

class TrieNode {

    private int keyEvent;

    private TrieNode parent;

    private TreeMap<Integer, TrieNode> childrend;

    private int counter;

    private int depth;

    protected TrieNode(TrieNode parent, int keyEvent) {
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

    protected void increaseCounter() {
        counter++;
    }

    protected int getCounter() {
        return counter;
    }

    protected boolean containsChild(int key) {
        return childrend.containsKey(key);
    }

    protected TrieNode getChild(int key) {
        return childrend.get(key);
    }

    protected TrieNode createChild(int key) {
        TrieNode node = new TrieNode(this, key);
        childrend.put(key, node);
        return node;
    }

    protected int getKeyEvent() {
        return keyEvent;
    }

    protected TrieNode getParent() {
        return parent;
    }

    protected Collection<TrieNode> getChildren() {
        return childrend.values();
    }

    protected int getDepth() {
        return depth;
    }
}