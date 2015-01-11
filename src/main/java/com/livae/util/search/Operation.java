package com.livae.util.search;

import com.livae.util.ResourcesFactory;

import java.util.Collection;

public interface Operation {

	public void apply(State state, Collection<State> offspring, ResourcesFactory<State> factory);

	public String getName();

}
