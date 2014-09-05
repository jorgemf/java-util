package com.jorgemf.util;

public interface TreeVisitor<k> {

    public void visit(k element, int depth, int children);

}
