package com.livae.util.tree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BtreeTest {

	@Test
	public void testClone() throws Exception {
		Btree<Integer> btree = new Btree<Integer>(3);
		for (int i = 0; i < 100; i++) {
			btree.add(i);
		}
		btree.checkStructure();
		Btree<Integer> clone = btree.clone();
		clone.checkStructure();
		List<Integer[]> btreeList = new ArrayList<Integer[]>();
		List<Integer[]> cloneList = new ArrayList<Integer[]>();

		btree.visitInOrder(new TreeVisitor(btreeList));
		clone.visitInOrder(new TreeVisitor(cloneList));

		assertEquals(btreeList.size(), clone.getSize());
		for (int i = 0; i < btreeList.size(); i++) {
			Integer[] a = btreeList.get(i);
			Integer[] b = cloneList.get(i);
			assertEquals(a[0], b[0]);
			assertEquals(a[1], b[1]);
		}
	}

	private class TreeVisitor implements BtreeVisitor<Integer> {

		private Integer previousItem;

		private List<Integer[]> btreeList;

		public TreeVisitor(List<Integer[]> btreeList) {
			this.btreeList = btreeList;
		}

		public void visit(Integer object, int deep) {
			btreeList.add(new Integer[]{object, deep});
			if (previousItem != null) {
				assertTrue(previousItem < object);
			}
			previousItem = object;
		}
	}

	private static class BtreePrinterVisitor implements BtreeVisitor<Integer> {

		public static final BtreePrinterVisitor instance = new BtreePrinterVisitor();

		public void visit(final Integer object, final int deep) {
			for (int i = 0; i < deep; i++) {
				System.out.print("  ");
			}
			System.out.println(object);
		}
	}

	@Test
	public void testAddRemove() throws Exception {
		for (int nodesPerPage = 3; nodesPerPage < 7; nodesPerPage++) {
			Btree<Integer> btree = new Btree<Integer>(nodesPerPage);
			int pagesPerPage = nodesPerPage + 1;
			int totalNodes = (1 + pagesPerPage + pagesPerPage * pagesPerPage) * nodesPerPage;
			// test add
			for (int i = 0; i < totalNodes; i++) {
				btree.add(i);
				btree.checkStructure();
			}
			Btree<Integer> btreePlain = new Btree<Integer>(nodesPerPage);
			for (int i = 0; i < totalNodes; i++) {
				btreePlain.add(1);
				btree.checkStructure();
			}
			// test remove
			for (int i = 0; i < totalNodes; i++) {
				Btree<Integer> clone = btree.clone();
				for (int j = 0; j < totalNodes; j++) {
					clone.remove((i + j) % totalNodes);
					clone.checkStructure();
				}
			}
			// test split any position and add duplicates
			Btree<Double> btreeD = new Btree<Double>(nodesPerPage);
			for (double i = 0; i < totalNodes; i++) {
				btreeD.add(i);
				btreeD.checkStructure();
			}
			for (double i = 0; i < totalNodes; i++) {
				Btree<Double> clone = btreeD.clone();
				clone.add(i + 0.5d);
				clone.checkStructure();
			}
		}
	}


}