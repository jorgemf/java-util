package com.jorgemf.util.search;

class ListNode {

    protected State state;

    protected int[] values;

    protected ListNode[] next;

    protected ListNode[] previous;

    protected ListNode(int heuristicLength) {
        this.values = new int[heuristicLength];
        this.next = new ListNode[this.values.length];
        this.previous = new ListNode[this.values.length];
    }

    protected State getState() {
        return this.state;
    }

    protected void setState(State state) {
        this.state = state;
        int[] heuristic = state.heuristic;
        int cost = state.cost;
        int size = values.length;
        for (int i = 0; i < size; i++) {
            values[i] = cost + heuristic[i];
        }
    }

    protected ListNode[] getNext() {
        return next;
    }

    protected ListNode[] getPrevious() {
        return previous;
    }

}
