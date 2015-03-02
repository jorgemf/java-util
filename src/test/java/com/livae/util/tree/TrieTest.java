package com.livae.util.tree;

import com.livae.util.Tuple;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TrieTest {

	@Test
	public void testAddEvents() throws Exception {
		Trie<String> trie = new Trie<>(4);
		trie.addToSequence("1");
		assertEquals(1, trie.getDepth());
		trie.addToSequence("2");
		assertEquals(2, trie.getDepth());
		trie.addToSequence("3");
		assertEquals(3, trie.getDepth());
		trie.addToSequence("4");
		assertEquals(4, trie.getDepth());
		trie.addToSequence("5");
		assertEquals(4, trie.getDepth());
		trie.addToSequence("6");
		assertEquals(12, trie.getTotalCounter());
		List<Tuple<List<String>, Integer>> sequences = trie.getSequences();
		assertEquals(3, sequences.size());
		assertEquals(1, (int) sequences.get(0).second);
		assertEquals(1, (int) sequences.get(1).second);
		assertEquals(1, (int) sequences.get(2).second);
		assertEquals("1 2 3 4 ", sequenceToString(sequences.get(0).first));
		assertEquals("2 3 4 5 ", sequenceToString(sequences.get(1).first));
		assertEquals("3 4 5 6 ", sequenceToString(sequences.get(2).first));
		trie.addToSequence("1");
		trie.addToSequence("1");
		trie.addToSequence("1");
		trie.addToSequence("1");
		assertEquals(28, trie.getTotalCounter());
		sequences = trie.getSequences();
		assertEquals(7, sequences.size());
		assertEquals(1, (int) sequences.get(0).second);
		assertEquals(1, (int) sequences.get(1).second);
		assertEquals(1, (int) sequences.get(2).second);
		assertEquals(1, (int) sequences.get(3).second);
		assertEquals(1, (int) sequences.get(4).second);
		assertEquals(1, (int) sequences.get(5).second);
		assertEquals(1, (int) sequences.get(6).second);
		assertEquals("1 1 1 1 ", sequenceToString(sequences.get(0).first));
		assertEquals("1 2 3 4 ", sequenceToString(sequences.get(1).first));
		assertEquals("2 3 4 5 ", sequenceToString(sequences.get(2).first));
		assertEquals("3 4 5 6 ", sequenceToString(sequences.get(3).first));
		assertEquals("4 5 6 1 ", sequenceToString(sequences.get(4).first));
		assertEquals("5 6 1 1 ", sequenceToString(sequences.get(5).first));
		assertEquals("6 1 1 1 ", sequenceToString(sequences.get(6).first));
	}

	@Test
	public void testAddSequence() throws Exception {
		Trie<String> trie = new Trie<>(4);
		trie.addToSequence("1");
		trie.addToSequence("2");
		trie.addToSequence("3");
		trie.addToSequence("4");
		trie.addToSequence("5");
		trie.addToSequence("6");
		List<String> sequence = new ArrayList<>();
		sequence.add("1");
		sequence.add("2");
		sequence.add("3");
		trie.add(sequence);
		assertEquals(15, trie.getTotalCounter());
		List<Tuple<List<String>, Integer>> sequences = trie.getSequences();
		assertEquals(4, sequences.size());
		assertEquals(1, (int) sequences.get(0).second);
		assertEquals(1, (int) sequences.get(1).second);
		assertEquals(1, (int) sequences.get(2).second);
		assertEquals(1, (int) sequences.get(3).second);
		assertEquals("1 2 3 ", sequenceToString(sequences.get(0).first));
		assertEquals("1 2 3 4 ", sequenceToString(sequences.get(1).first));
		assertEquals("2 3 4 5 ", sequenceToString(sequences.get(2).first));
		assertEquals("3 4 5 6 ", sequenceToString(sequences.get(3).first));
	}

	@Test
	public void testClone() throws Exception {
		Trie<String> trie = new Trie<>(4);
		trie.addToSequence("1");
		trie.addToSequence("2");
		trie.addToSequence("3");
		trie.addToSequence("4");
		trie.addToSequence("5");
		trie.addToSequence("6");
		trie.addToSequence("1");
		trie.addToSequence("1");
		trie.addToSequence("1");
		trie.addToSequence("1");
		Trie<String> clone = trie.clone();
		assertSameTries(trie, clone);
	}

	@Test
	public void testReverse() throws Exception {
		Trie<String> trie = new Trie<>(4);
		trie.addToSequence("1");
		trie.addToSequence("2");
		trie.addToSequence("3");
		trie.addToSequence("4");
		trie.addToSequence("5");
		trie.addToSequence("6");
		trie.addToSequence("1");
		trie.addToSequence("1");
		trie.addToSequence("1");
		trie.addToSequence("1");
		Trie<String> reverseTrie = trie.reverse();
		List<Tuple<List<String>, Integer>> sequences = reverseTrie.getSequences();
		assertEquals(7, sequences.size());
		assertEquals("1 1 1 1 ", sequenceToString(sequences.get(0).first));
		assertEquals("1 1 1 6 ", sequenceToString(sequences.get(1).first));
		assertEquals("1 1 6 5 ", sequenceToString(sequences.get(2).first));
		assertEquals("1 6 5 4 ", sequenceToString(sequences.get(3).first));
		assertEquals("4 3 2 1 ", sequenceToString(sequences.get(4).first));
		assertEquals("5 4 3 2 ", sequenceToString(sequences.get(5).first));
		assertEquals("6 5 4 3 ", sequenceToString(sequences.get(6).first));
	}

	@Test
	public void testMerge() throws Exception {
		Trie<String> trie1 = new Trie<>(4);
		trie1.addToSequence("1");
		trie1.addToSequence("2");
		trie1.addToSequence("3");
		trie1.restartSequence();
		trie1.addToSequence("4");
		trie1.addToSequence("5");
		trie1.addToSequence("6");
		trie1.restartSequence();
		trie1.addToSequence("1");
		trie1.addToSequence("2");
		trie1.addToSequence("4");
		Trie<String> trie2 = new Trie<>(4);
		trie2.addToSequence("1");
		trie2.addToSequence("3");
		trie2.addToSequence("4");
		trie2.restartSequence();
		trie2.addToSequence("2");
		trie2.addToSequence("3");
		trie2.addToSequence("4");
		trie2.restartSequence();
		trie2.addToSequence("1");
		trie2.addToSequence("2");
		trie2.addToSequence("4");
		Trie<String> trie3 = new Trie<>(4);
		trie3.addToSequence("1");
		trie3.addToSequence("2");
		trie3.addToSequence("3");
		trie3.restartSequence();
		trie3.addToSequence("4");
		trie3.addToSequence("5");
		trie3.addToSequence("6");
		trie3.restartSequence();
		trie3.addToSequence("1");
		trie3.addToSequence("2");
		trie3.addToSequence("4");
		trie3.restartSequence();
		trie3.addToSequence("1");
		trie3.addToSequence("3");
		trie3.addToSequence("4");
		trie3.restartSequence();
		trie3.addToSequence("2");
		trie3.addToSequence("3");
		trie3.addToSequence("4");
		trie3.restartSequence();
		trie3.addToSequence("1");
		trie3.addToSequence("2");
		trie3.addToSequence("4");
		// test merge
		trie1.merge(trie2);
		assertSameTries(trie1, trie3);
	}

	@Test
	public void testVisitBreath() throws Exception {
		Trie<String> trie = new Trie<>(4);
		trie.addToSequence("1");
		trie.addToSequence("2");
		trie.addToSequence("3");
		trie.addToSequence("4");
		trie.addToSequence("5");
		trie.addToSequence("6");
		trie.addToSequence("1");
		trie.addToSequence("1");
		trie.addToSequence("1");
		trie.addToSequence("1");
		TestVisitor visitor = new TestVisitor();
		trie.visitBreadth(visitor);
		assertEquals("1 2 3 4 5 6 1 2 3 4 5 6 1 1 3 4 5 6 1 1 1 4 5 6 1 1 1 ",
		             visitor.getVisitsString());
	}

	@Test
	public void testVisitPostOrder() throws Exception {
		Trie<String> trie = new Trie<>(4);
		trie.addToSequence("1");
		trie.addToSequence("2");
		trie.addToSequence("3");
		trie.addToSequence("4");
		trie.addToSequence("5");
		trie.addToSequence("6");
		trie.addToSequence("1");
		trie.addToSequence("1");
		trie.addToSequence("1");
		trie.addToSequence("1");
		TestVisitor visitor = new TestVisitor();
		trie.visitPostOrder(visitor);
		assertEquals("1 1 1 4 3 2 1 5 4 3 2 6 5 4 3 1 6 5 4 1 1 6 5 1 1 1 6 ",
		             visitor.getVisitsString());
	}

	private void assertSameTries(Trie<String> a, Trie<String> b) {
		List<Tuple<List<String>, Integer>> aSeq = a.getSequences();
		List<Tuple<List<String>, Integer>> bSeq = b.getSequences();
		assertEquals(a.getDepth(), b.getDepth());
		assertEquals(a.getSize(), b.getSize());
		assertEquals(aSeq.size(), bSeq.size());
		TestSameVisitor tsv1 = new TestSameVisitor();
		TestSameVisitor tsv2 = new TestSameVisitor();
		a.visitPreOrder(tsv1);
		a.visitBreadth(tsv1);
		a.visitPostOrder(tsv1);
		b.visitPreOrder(tsv2);
		b.visitBreadth(tsv2);
		b.visitPostOrder(tsv2);
		assertEquals(tsv1.getVisitsString(), tsv2.getVisitsString());
	}

	private String sequenceToString(List<String> sequence) {
		String string = "";
		for (String entry : sequence) {
			string += entry + " ";
		}
		return string;
	}

	private class TestVisitor implements TrieVisitor<String> {

		private List<String> visits = new ArrayList<>();

		@Override
		public void visit(String element, int count, int depth, int children, int childrenCount) {
			visits.add(element);
		}

		public String getVisitsString() {
			String string = "";
			for (String s : visits) {
				string += s + " ";
			}
			return string;
		}
	}

	private class TestSameVisitor implements TrieVisitor<String> {

		private List<String> visits = new ArrayList<>();

		public String getVisitsString() {
			String string = "";
			for (String s : visits) {
				string += s + " ";
			}
			return string;
		}

		@Override
		public void visit(String element, int count, int depth, int children, int childrenCount) {
			visits.add(element + "_" + count + "_" + depth + "_" + children + "_" + childrenCount);
		}

	}

}