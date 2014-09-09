package com.jorgemf.util.search;

public interface Heuristic {

    public int calculateHeuristic(State state);

    public String getName();

}
