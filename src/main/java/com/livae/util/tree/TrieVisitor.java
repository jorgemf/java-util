package com.livae.util.tree;

public interface TrieVisitor<k> {

	public void visit(k element, int depth, int children);

}
