package com.jorgemf.util.search;

import com.jorgemf.util.ResourcesFactory;

class List extends ResourcesFactory<ListNode> {

    private ListNode[] first;

    private long size;

    private int heuristicSize;

    protected List(int heuristicSize) {
        this.heuristicSize = heuristicSize;
        first = new ListNode[heuristicSize];
        size = 0;
    }

    protected void clear() {
        ListNode node = first[0];
        ListNode next;
        int size;
        while (node != null) {
            next = node.next[0];
            size = node.next.length;
            for (int i = 0; i < size; i++) {
                node.next[i] = null;
                node.previous[i] = null;
                node.values[i] = -1;
            }
            releaseResource(node);
            node = next;
        }
        size = first.length;
        for (int i = 0; i < size; i++) {
            first[i] = null;
        }
        size = 0;
    }

    protected long getSize() {
        return size;
    }

    protected boolean isEmpty() {
        return first[0] == null;
    }

    protected void add(State state) {
        ListNode node = getResource();
        node.setState(state);
        add(node);
        size++;
    }

    private void remove(ListNode node) {
        ListNode aux;
        boolean removed = false;
        int s = first.length;
        for (int i = 0; i < s; i++) {
            aux = first[i];
            if (aux == node) {
                first[i] = node.next[i];
                if (first[i] != null) {
                    first[i].previous[i] = null;
                }
                removed = true;
            } else {
                while (aux != null && aux.next[i] != node) {
                    aux = aux.next[i];
                }
                if (aux != null && aux.next[i] == node) {
                    aux.next[i] = node.next[i];
                    if (aux.next[i] != null) {
                        aux.next[i].previous[i] = aux;
                    }
                    removed = true;
                }
            }
        }
        if (removed) {
            size--;
            s = node.next.length;
            for (int i = 0; i < s; i++) {
                node.next[i] = null;
                node.previous[i] = null;
                node.values[i] = -1;
            }
            releaseResource(node);
        }
    }

    protected State[] getFirst() {
        State[] states = new State[first.length];
        int s = first.length;
        for (int i = 0; i < s; i++) {
            if (first[i] != null) {
                states[i] = first[i].state;
                remove(first[i]);
            } else {
                states[i] = null;
            }
        }
        return states;
    }

    protected State[] getLast() {
        State[] states = new State[first.length];
        int s = first.length;
        for (int i = 0; i < s; i++) {
            if (first[i] != null) {
                ListNode node = first[i];
                while (node.next[i] != null) {
                    node = node.next[i];
                }
                states[i] = node.getState();
                remove(node);
            } else {
                states[i] = null;
            }
        }
        return states;
    }

    protected ListNode[] getFirstNodes() {
        return first;
    }

    private void add(ListNode node) {
        ListNode aux;
        ListNode auxNext;
        int value;
        int s = first.length;
        for (int i = 0; i < s; i++) {
            aux = first[i];
            value = node.values[i];
            if (aux == null) {
                first[i] = node;
                node.previous[i] = null;
                node.next[i] = null;
            } else if (aux.values[i] > value) {
                node.previous[i] = null;
                node.next[i] = aux;
                aux.previous[i] = node;
                first[i] = node;
            } else {
                auxNext = aux.next[i];
                while (auxNext != null && auxNext.values[i] < value) {
                    aux = auxNext;
                    auxNext = aux.next[i];
                }
                aux.next[i] = node;
                node.previous[i] = aux;
                if (auxNext != null) {
                    auxNext.previous[i] = node;
                    node.next[i] = auxNext;
                } else {
                    node.next[i] = null;
                }
            }
        }
    }

    protected void add(List list) {
        ListNode current;
        ListNode currentNext;
        ListNode aux;
        ListNode next;
        int s = first.length;
        for (int i = 0; i < s; i++) {
            current = list.first[i];
            if (current != null) {
                currentNext = current.next[i];
                aux = first[i];
                if (aux == null) {
                    current.previous[i] = null;
                    current.next[i] = null;
                    first[i] = current;
                    aux = current;
                    current = currentNext;
                } else if (aux.values[i] > current.values[i]) {
                    current.previous[i] = null;
                    current.next[i] = aux;
                    aux.previous[i] = current;
                    first[i] = current;
                    aux = current;
                    current = currentNext;
                }
                while (current != null) {
                    currentNext = current.next[i];
                    next = aux.next[i];
                    if (next == null) {
                        aux.next[i] = current;
                        current.previous[i] = aux;
                        current.next[i] = null;
                        current = currentNext;
                    } else if (next.values[i] > current.values[i]) {
                        aux.next[i] = current;
                        current.previous[i] = aux;
                        current.next[i] = next;
                        next.previous[i] = current;
                        aux = current;
                        current = currentNext;
                    } else {
                        aux = next;
                    }
                }
            }
        }
        size += list.size;
    }

    @Override
    protected ListNode createResource() {
        return new ListNode(heuristicSize);
    }

}