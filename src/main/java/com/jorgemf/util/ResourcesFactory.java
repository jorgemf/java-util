package com.jorgemf.util;

public abstract class ResourcesFactory<k> {

    private static final int LIST_INCREMENT = 10;

    private k[] resources;

    private int lastFreeResource;

    private int increment;

    public ResourcesFactory() {
        this(LIST_INCREMENT);
    }

    public ResourcesFactory(int listIncrement) {
        //noinspection unchecked
        resources = (k[]) (new Object[0]);
        lastFreeResource = -1;
        increment = listIncrement;
    }

    protected abstract k createResource();

    public void releaseResource(k resource) {
        if (resources.length == lastFreeResource + 1) {
            //noinspection unchecked
            k[] newlist = (k[]) (new Object[resources.length + increment]);
            System.arraycopy(resources, 0, newlist, 0, resources.length);
            resources = newlist;
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
