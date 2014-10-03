package com.jorgemf.util.tree;

import com.jorgemf.util.ResourcesFactory;

public class Btree<k extends Comparable<k>> extends ResourcesFactory<BtreePage<k>> {

    private static final int NODES_PER_PAGE = 6;
    private long size;
    private BtreePage<k> root;
    private int nodesPerPage;

    public Btree() {
        this(NODES_PER_PAGE);
    }

    public Btree(int nodesPerPage) {
        if (nodesPerPage < 3) {
            throw new RuntimeException("Nodes per page must be greater than 2");
        }
        this.nodesPerPage = nodesPerPage;
        this.root = getResource();
        this.size = 0;
        //noinspection unchecked
    }

    public void add(k object) {
        size++;
        root.add(object);
    }

    public k getFirst() {
        k object = root.getFirst();
        remove(object);
        return object;
    }

    public void remove(k object) {
        if (root.remove(object)) {
            this.size--;
        }
    }

    public long getSize() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void visitInOrder(BtreeVisitor<k> visitor) {
        this.root.visitInOrder(visitor, 0);
    }

    public void checkStructure() {
        root.checkIntegrity();
    }

    @Override
    protected BtreePage<k> createResource() {
        return new BtreePage<k>(this.nodesPerPage, this);
    }

    public void clear() {
        root.clear();
    }


}
