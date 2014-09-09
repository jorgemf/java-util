package com.jorgemf.util.tree;

public interface BtreeVisitor<k> {

    public void visit(k object, int deep);

}
