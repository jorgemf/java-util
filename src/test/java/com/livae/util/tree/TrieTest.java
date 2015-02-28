package com.livae.util.tree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class TrieTest {

	@Test
	public void testAddEvents() throws Exception {
		Trie<String> trie = new Trie<>(4);
		trie.addToSequence("first");
		trie.addToSequence("second");
		trie.addToSequence("third");
		trie.addToSequence("fourth");
		trie.addToSequence("fifth");
		trie.addToSequence("sixth");
		trie.getSequences();// TODO
		System.out.println(trie.getDebugString());
		List<String> sequence = new ArrayList<>();
		sequence.add("first");
		sequence.add("second");
		sequence.add("third");
		trie.add(sequence);
		System.out.println(trie.getDebugString());
		fail();
	}
//
//	@Test
//	public void testAddSequence() throws Exception {
//		fail();
//	}
//
//	@Test
//	public void testClone() throws Exception {
//		fail();
//	}
//
//	@Test
//	public void testReverse() throws Exception {
//		fail();
//	}

}