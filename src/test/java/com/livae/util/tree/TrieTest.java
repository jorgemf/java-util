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
		trie.addToSequence("first");
		assertEquals(1, trie.getDepth());
		trie.addToSequence("second");
		assertEquals(2, trie.getDepth());
		trie.addToSequence("third");
		assertEquals(3, trie.getDepth());
		trie.addToSequence("fourth");
		assertEquals(4, trie.getDepth());
		trie.addToSequence("fifth");
		assertEquals(4, trie.getDepth());
		trie.addToSequence("sixth");
		assertEquals(12, trie.getTotalCounter());
		List<List<Tuple<String, Integer>>> sequences = trie.getSequences();
		assertEquals(3, sequences.size());
		assertEquals("first:1 second:1 third:1 fourth:1 ", sequenceToString(sequences.get(0)));
		assertEquals("second:1 third:1 fourth:1 fifth:1 ", sequenceToString(sequences.get(1)));
		assertEquals("third:1 fourth:1 fifth:1 sixth:1 ", sequenceToString(sequences.get(2)));
		trie.addToSequence("first");
		trie.addToSequence("first");
		trie.addToSequence("first");
		trie.addToSequence("first");
		assertEquals(28, trie.getTotalCounter());
		sequences = trie.getSequences();
		assertEquals(7, sequences.size());
		assertEquals("first:2 first:1 first:1 first:1 ", sequenceToString(sequences.get(0)));
		assertEquals("first:2 second:1 third:1 fourth:1 ", sequenceToString(sequences.get(1)));
		assertEquals("second:1 third:1 fourth:1 fifth:1 ", sequenceToString(sequences.get(2)));
		assertEquals("third:1 fourth:1 fifth:1 sixth:1 ", sequenceToString(sequences.get(3)));
		assertEquals("fourth:1 fifth:1 sixth:1 first:1 ", sequenceToString(sequences.get(4)));
		assertEquals("fifth:1 sixth:1 first:1 first:1 ", sequenceToString(sequences.get(5)));
		assertEquals("sixth:1 first:1 first:1 first:1 ", sequenceToString(sequences.get(6)));
	}

	@Test
	public void testAddSequence() throws Exception {
		Trie<String> trie = new Trie<>(4);
		trie.addToSequence("first");
		trie.addToSequence("second");
		trie.addToSequence("third");
		trie.addToSequence("fourth");
		trie.addToSequence("fifth");
		trie.addToSequence("sixth");
		List<String> sequence = new ArrayList<>();
		sequence.add("first");
		sequence.add("second");
		sequence.add("third");
		trie.add(sequence);
		assertEquals(15, trie.getTotalCounter());
		List<List<Tuple<String, Integer>>> sequences = trie.getSequences();
		assertEquals(3, sequences.size());
		assertEquals("first:2 second:2 third:2 fourth:1 ", sequenceToString(sequences.get(0)));
		assertEquals("second:1 third:1 fourth:1 fifth:1 ", sequenceToString(sequences.get(1)));
		assertEquals("third:1 fourth:1 fifth:1 sixth:1 ", sequenceToString(sequences.get(2)));
	}

	@Test
	public void testClone() throws Exception {
		Trie<String> trie = new Trie<>(4);
		trie.addToSequence("first");
		trie.addToSequence("second");
		trie.addToSequence("third");
		trie.addToSequence("fourth");
		trie.addToSequence("fifth");
		trie.addToSequence("sixth");
		trie.addToSequence("first");
		trie.addToSequence("first");
		trie.addToSequence("first");
		trie.addToSequence("first");
		List<List<Tuple<String, Integer>>> sequences = trie.getSequences();
		Trie<String> clone = trie.clone();
		List<List<Tuple<String, Integer>>> cloneSequences = clone.getSequences();
		assertEquals(trie.getDepth(), clone.getDepth());
		assertEquals(trie.getSize(), clone.getSize());
		assertEquals(sequences.size(), cloneSequences.size());
		for (int i = 0; i < sequences.size(); i++) {
			assertEquals(sequenceToString(sequences.get(i)), sequenceToString(cloneSequences
			                                                                    .get(i)));
		}
	}

	@Test
	public void testReverse() throws Exception {
		Trie<String> trie = new Trie<>(4);
		trie.addToSequence("first");
		trie.addToSequence("second");
		trie.addToSequence("third");
		trie.addToSequence("fourth");
		trie.addToSequence("fifth");
		trie.addToSequence("sixth");
		trie.addToSequence("first");
		trie.addToSequence("first");
		trie.addToSequence("first");
		trie.addToSequence("first");
		System.out.println("\nnormal:\n");
		System.out.printf(trie.getDebugString());
		Trie<String> reverseTrie = trie.reverse();
		System.out.println("\n\nreverse:\n");
		System.out.printf(reverseTrie.getDebugString());
	}

	private String sequenceToString(List<Tuple<String, Integer>> sequence) {
		String string = "";
		for (Tuple<String, Integer> entry : sequence) {
			string += entry.first + ":" + entry.second + " ";
		}
		return string;
	}

}