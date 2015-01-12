package com.livae.util;

/**
 * Abstract class to create resources that will be created and deleted very often. The main purpose is to prevent the
 * garbage collector to run as the objects are created and destroyed very often, so they are kept in memory an reused
 * when needed.
 *
 * @param <k> Type of resource to be create
 */
public abstract class ResourcesFactory<k> {

	private static final int LIST_INCREMENT = 10;

	private k[] resources;

	private int lastFreeResource;

	private int increment;

	public ResourcesFactory() {
		this(LIST_INCREMENT);
	}

	public ResourcesFactory(int listIncrement) {
		if (listIncrement <= 0) {
			throw new RuntimeException("Increment has to be greater than 0");
		}
		increment = listIncrement;
		//noinspection unchecked
		resources = (k[]) (new Object[0]);
		lastFreeResource = -1;
	}

	protected abstract k createResource();

	public void releaseResource(k resource) {
		if (resources.length == lastFreeResource + 1) {
			//noinspection unchecked
			k[] newList = (k[]) (new Object[resources.length + increment]);
			System.arraycopy(resources, 0, newList, 0, resources.length);
			resources = newList;
		}
		lastFreeResource++;
		resources[lastFreeResource] = resource;
	}

	public k getResource() {
		k resource;
		if (lastFreeResource < 0) {
			resource = createResource();
		} else {
			resource = resources[lastFreeResource];
			resources[lastFreeResource] = null;
			lastFreeResource--;
		}
		return resource;
	}

}
