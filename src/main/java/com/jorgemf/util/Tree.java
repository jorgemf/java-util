package com.jorgemf.util;

public interface Tree<k> {

    public void visitPreOrder(TreeVisitor<k> visitor);

    public void visitPostOrder(TreeVisitor<k> visitor);

    public void visitInOrder(TreeVisitor<k> visitor);

    public void visitBreadth(TreeVisitor<k> visitor);

}
