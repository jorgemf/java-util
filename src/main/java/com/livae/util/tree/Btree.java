package com.livae.util.tree;

import com.livae.util.ResourcesFactory;

public class Btree<k extends Comparable<k>> extends ResourcesFactory<BtreePage<k>> {

	private static final int NODES_PER_PAGE = 6;

	private long size;

	private BtreePage<k> root;

	private int nodesPerPage;

	private BtreeVisitor orderVisitor;

	private Comparable orderVisitorPrevious;

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
	}

	private Btree(Btree<k> treeToClone) {
		size = treeToClone.size;
		nodesPerPage = treeToClone.nodesPerPage;
		root = treeToClone.root.clone(null, this);
	}

	public Btree<k> clone() {
		return new Btree<k>(this);
	}

	public void add(k object) {
		size++;
		root.add(object);
	}

	public k getFirst() {
		k object = null;
		if (root != null) {
			BtreePage<k> firstPage = root.getFirstPage();
			object = firstPage.getFirstFromPage();
			if (firstPage.remove(object)) {
				size--;
			}
		}
		return object;
	}

	public void remove(k object) {
		if (root.remove(object)) {
			size--;
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
		if (orderVisitor == null) {
			orderVisitor = new BtreeVisitor<Comparable>() {

				@Override
				public void visit(final Comparable object, final int deep) {
					//noinspection unchecked
					if (orderVisitorPrevious != null &&
					    orderVisitorPrevious.compareTo(object) > 0) {
						throw new RuntimeException("Wrong order: " + orderVisitorPrevious + " < " +
						                           object);
					}
					orderVisitorPrevious = object;
				}
			};
		}
		orderVisitorPrevious = null;
		//noinspection unchecked
		visitInOrder(orderVisitor);
	}

	@Override
	protected BtreePage<k> createResource() {
		return new BtreePage<k>(this.nodesPerPage, this);
	}

	public void clear() {
		root.clear();
	}

	public String getDebugString() {
		return root.getDebugString();
	}

	protected TestUtils getTestUtils() {
		return new TestUtils();
	}

	class TestUtils {

		public void setRoot(Btree<k> o, BtreePage<k> page, int size) {
			o.root = page;
			o.size = size;
		}
	}

}
