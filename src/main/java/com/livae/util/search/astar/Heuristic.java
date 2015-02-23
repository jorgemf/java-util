package com.livae.util.search.astar;

public interface Heuristic {

	public int calculateHeuristic(State state);

	public String getName();

}
