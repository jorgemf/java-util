package com.livae.util.tree;

import com.livae.util.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Vector;

public class Trie<k> {

	private int maxDepth;

	private TrieNode root = new TrieNode(null, -1);

	private HashMap<k, Integer> eventNamesMap = new HashMap<k, Integer>();

	private Vector<k> eventNamesVector = new Vector<k>();

	private LinkedList<Integer> sequence = new LinkedList<Integer>();

	private int size = 0;

	private int depth = 0;

	private int totalCounter = 0;

	public Trie(int maximumDepth) {
		if (maximumDepth <= 0) {
			throw new RuntimeException("Sequences require a limit in depth");
		}
		this.maxDepth = maximumDepth;
	}

	private Trie(Trie<k> trieToClone) {
		maxDepth = trieToClone.maxDepth;
		size = trieToClone.size;
		depth = trieToClone.depth;
		totalCounter = trieToClone.totalCounter;
		for (Map.Entry<k, Integer> entry : trieToClone.eventNamesMap.entrySet()) {
			eventNamesMap.put(entry.getKey(), entry.getValue());
		}
		for (k entry : trieToClone.eventNamesVector) {
			eventNamesVector.add(entry);
		}
		for (Integer entry : trieToClone.sequence) {
			sequence.add(entry);
		}
		root = trieToClone.root.clone(null);
	}

	public void add(List<k> events) {
		if (events.size() > maxDepth) {
			throw new RuntimeException("Sequence is longer than expected");
		}
		depth = Math.max(depth, events.size());
		TrieNode currentNode = root;
		for (k event : events) {
			currentNode = getOrCreateChildNode(currentNode, getKey(event));
			currentNode.increaseCounter();
			totalCounter++;
		}
	}

	public void addToSequence(k event) {
		sequence.addLast(getKey(event));
		boolean newSequence = false;
		if (sequence.size() > maxDepth) {
			sequence.removeFirst();
			newSequence = true;
		} else {
			depth = Math.max(depth, sequence.size());
		}
		TrieNode currentNode = root;
		for (int keys : sequence) {
			currentNode = getOrCreateChildNode(currentNode, keys);
			if (newSequence) {
				currentNode.increaseCounter();
				totalCounter++;
			}
		}
		if (!newSequence) {
			currentNode.increaseCounter();
			totalCounter++;
		}
	}

	public void restartSequence() {
		sequence.clear();
	}

	private TrieNode getOrCreateChildNode(TrieNode currentNode, int key) {
		if (!currentNode.containsChild(key)) {
			size++;
			return currentNode.createChild(key);
		} else {
			return currentNode.getChild(key);
		}
	}

	private int getKey(k event) {
		int key;
		if (eventNamesMap.containsKey(event)) {
			key = eventNamesMap.get(event);
		} else {
			eventNamesVector.add(event);
			key = eventNamesVector.size() - 1;
			eventNamesMap.put(event, key);
		}
		return key;
	}

	public void visitPreOrder(TrieVisitor<k> visitor) {
		for (TrieNode n : root.getChildren()) {
			visitPreOrder(visitor, n);
		}
	}

	private void visitPreOrder(TrieVisitor<k> visitor, TrieNode node) {
		visitor.visit(eventNamesVector.get(node.getKeyEvent()), node.getCounter(), node.getDepth(),
		              node.getChildren().size(), node.getChildrenSize());
		for (TrieNode n : node.getChildren()) {
			visitPreOrder(visitor, n);
		}
	}

	public void visitPostOrder(TrieVisitor<k> visitor) {
		for (TrieNode n : root.getChildren()) {
			visitPostOrder(visitor, n);
		}
	}

	private void visitPostOrder(TrieVisitor<k> visitor, TrieNode node) {
		for (TrieNode n : node.getChildren()) {
			visitPostOrder(visitor, n);
		}
		visitor.visit(eventNamesVector.get(node.getKeyEvent()), node.getCounter(), node.getDepth(),
		              node.getChildren().size(), node.getChildrenSize());
	}

	public void visitBreadth(TrieVisitor<k> visitor) {
		Queue<TrieNode> nodesQueue = new LinkedList<TrieNode>();
		nodesQueue.addAll(root.getChildren());
		TrieNode head;
		Collection<TrieNode> children;
		int childrenSize;
		while (!nodesQueue.isEmpty()) {
			head = nodesQueue.remove();
			children = head.getChildren();
			childrenSize = children.size();
			if (childrenSize > 0) {
				nodesQueue.addAll(children);
			}
			visitor.visit(eventNamesVector.get(head.getKeyEvent()), head.getCounter(),
			              head.getDepth(), childrenSize, head.getChildrenSize());
		}
	}

	public int getSize() {
		return size;
	}

	public int getDepth() {
		return depth;
	}

	public int getTotalCounter() {
		return totalCounter;
	}

	public Trie<k> clone() {
		return new Trie<>(this);
	}

	public void merge(Trie<k> trie) {
		int[] trieEventsTranslator = new int[trie.eventNamesVector.size()];
		for (int i = 0; i < trieEventsTranslator.length; i++) {
			trieEventsTranslator[i] = getKey(trie.eventNamesVector.get(i));
		}
		merge(root, trie.root, trieEventsTranslator);
		maxDepth = Math.max(maxDepth, trie.maxDepth);
		depth = Math.max(depth, trie.depth);
		totalCounter = totalCounter + trie.totalCounter;
	}

	private void merge(TrieNode node, TrieNode otherNode, int[] trieEventsTranslator) {
		for (TrieNode otherNodeChild : otherNode.getChildren()) {
			int key = trieEventsTranslator[otherNodeChild.keyEvent];
			TrieNode nodeChild = getOrCreateChildNode(node, key);
			nodeChild.counter += otherNodeChild.counter;
			merge(nodeChild, otherNodeChild, trieEventsTranslator);
		}
	}

	public Trie<k> reverse() {
		ReverseVisitor reverseVisitor = new ReverseVisitor();
		visitPreOrder(reverseVisitor);
		return reverseVisitor.getReversedTrie();
	}

	public String getDebugString() {
		TriePrinter v = new TriePrinter();
		visitPreOrder(v);
		return v.getString();
	}

	public List<Tuple<List<k>, Integer>> getSequences() {
		SequenceVisitor sequenceVisitor = new SequenceVisitor();
		visitPreOrder(sequenceVisitor);
		return sequenceVisitor.getSequences();
	}

	class TrieNode {

		private int keyEvent;

		private TrieNode parent;

		private TreeMap<Integer, TrieNode> childrend;

		private int counter;

		private int depth;

		private TrieNode(TrieNode parent, int keyEvent) {
			childrend = new TreeMap<>();
			this.keyEvent = keyEvent;
			counter = 0;
			this.parent = parent;
			if (parent != null) {
				depth = parent.depth + 1;
			} else {
				depth = -1;
			}
		}

		private TrieNode(TrieNode nodeToClone, TrieNode parentNode) {
			this(parentNode, nodeToClone.keyEvent);
			counter = nodeToClone.counter;
			childrend = new TreeMap<>();
			for (Map.Entry<Integer, TrieNode> entry : nodeToClone.childrend.entrySet()) {
				childrend.put(entry.getKey(), entry.getValue().clone(this));
			}
		}

		private TrieNode clone(TrieNode newParentNode) {
			return new TrieNode(this, newParentNode);
		}

		private void increaseCounter() {
			counter++;
		}

		private int getCounter() {
			return counter;
		}

		private boolean containsChild(int key) {
			return childrend.containsKey(key);
		}

		private TrieNode getChild(int key) {
			return childrend.get(key);
		}

		private TrieNode createChild(int key) {
			TrieNode node = new TrieNode(this, key);
			childrend.put(key, node);
			return node;
		}

		private int getKeyEvent() {
			return keyEvent;
		}

		private TrieNode getParent() {
			return parent;
		}

		private Collection<TrieNode> getChildren() {
			return childrend.values();
		}

		private int getDepth() {
			return depth;
		}

		public String getDebugString() {
			return root.getDebugString();
		}

		private int getChildrenSize() {
			int size = 0;
			for (TrieNode childNode : childrend.values()) {
				size += childNode.getCounter();
			}
			return size;
		}
	}

	private class SequenceVisitor implements TrieVisitor<k> {

		private List<Tuple<List<k>, Integer>> sequences;

		private List<k> currentSequence;

		private SequenceVisitor() {
			sequences = new ArrayList<>();
			currentSequence = new ArrayList<>();
		}

		@Override
		public void visit(k element, int count, int depth, int children, int childrenCount) {
			while (depth < currentSequence.size() && currentSequence.size() > 0) {
				currentSequence.remove(currentSequence.size() - 1);
			}
			currentSequence.add(element);
			if (children == 0) {
				addSequence(currentSequence, count);// add new longer sequence
			} else if (childrenCount < count) {
				addSequence(currentSequence, count - childrenCount);// add new short sequence
			}
		}

		private List<Tuple<List<k>, Integer>> getSequences() {
			return sequences;
		}

		protected void addSequence(List<k> sequence, int times) {
			List<k> copy = new ArrayList<>();
			copy.addAll(sequence);
			sequences.add(new Tuple<>(copy, times));
		}
	}

	private class ReverseVisitor extends SequenceVisitor {

		private Trie<k> reversedTrie;

		private ReverseVisitor() {
			reversedTrie = new Trie<k>(maxDepth);
		}

		@Override
		protected void addSequence(List<k> sequence, int times) {
			ArrayList<k> reversedSequence = new ArrayList<>(sequence.size());
			for (int i = sequence.size() - 1; i >= 0; i--) {
				reversedSequence.add(sequence.get(i));
			}
			for (int i = 0; i < times; i++) {
				reversedTrie.add(reversedSequence);
			}
		}

		public Trie<k> getReversedTrie() {
			return reversedTrie;
		}
	}

	private class TriePrinter implements TrieVisitor<k> {

		private Vector<StringBuilder> stringBuilders;

		private int charactersAdded;

		private TriePrinter() {
			stringBuilders = new Vector<StringBuilder>();
			charactersAdded = 0;
		}

		protected String getString() {
			StringBuilder sb = new StringBuilder();
			for (StringBuilder stringBuilder : stringBuilders) {
				sb.append(stringBuilder).append('\n');
			}
			return sb.toString();
		}

		public void visit(k object, int count, int deep, int children, int childrenCount) {
			while (stringBuilders.size() <= deep) {
				StringBuilder sb = new StringBuilder();
				stringBuilders.add(sb);
			}
			StringBuilder sb = stringBuilders.get(deep);
			while (sb.length() < charactersAdded) {
				sb.append(' ');
			}
			String objectString = "null " + count;
			if (object != null) {
				objectString = object.toString() + " " + count + " ";
			}
			sb.append(objectString);
			charactersAdded += objectString.length();
		}

	}

}
