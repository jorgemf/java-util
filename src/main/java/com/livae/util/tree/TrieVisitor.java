package com.livae.util.tree;

public interface TrieVisitor<k> {

	public void visit(k element, int count, int depth, int children);

}
