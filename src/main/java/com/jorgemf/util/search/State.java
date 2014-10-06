package com.jorgemf.util.search;

public abstract class State implements Comparable<State> {

    protected int[] heuristic;

    protected int cost;

    protected State parent;

    protected Operation operation;

    protected abstract String getHash();

    public abstract String toString();

}
