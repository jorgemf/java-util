package com.jorgemf.util.search;

import com.jorgemf.util.ResourcesFactory;

import java.util.Collection;

public interface Operation {

    public void apply(State state, Collection<State> offspring, ResourcesFactory<State> factory);

    public String getName();

}
