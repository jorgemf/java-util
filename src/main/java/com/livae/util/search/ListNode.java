package com.livae.util.search;

class ListNode {

	protected State state;

	protected int[] values;

	protected ListNode[] next;

	protected ListNode[] previous;

	protected ListNode(int heuristicLength) {
		values = new int[heuristicLength];
		next = new ListNode[values.length];
		previous = new ListNode[values.length];
	}

	protected State getState() {
		return state;
	}

	protected void setState(State state) {
		state = state;
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
