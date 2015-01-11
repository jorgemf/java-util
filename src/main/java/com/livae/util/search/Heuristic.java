package com.livae.util.search;

public interface Heuristic {

	public int calculateHeuristic(State state);

	public String getName();

}
