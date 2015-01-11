package com.livae.util.search;

import com.livae.util.ResourcesFactory;
import com.livae.util.tree.Btree;
import com.livae.util.tree.BtreeVisitor;

import java.util.HashSet;
import java.util.Vector;

public class AstarTree implements BtreeVisitor<State> {

	private static final int TIME_PRINT_STATS = 1000;
	private static boolean PRINT_STATS = false;
	private static boolean DEBUG = false;
	private HashSet<String> visitedStates;

	private Btree<State>[] openStateLists;

	private Operation[] operations;

	private Heuristic[] heuristics;

	private State bestState;

	private int bestHeuristic;

	private long visitedStatesNumber;

	private long generatedStates;

	private long repeatedGeneratedStates;

	private long timeHeuristic;

	private long[] timeHeuristics;

	private long[] generatedStatesOperator;

	private long[] repeatedGeneratedStatesOperator;

	private long timeOperator;

	private long[] timeOperators;

	private long timeToHash;

	private long timeHashTable;

	private long timeAddToTree;

	private long startAlgorithmTime;

	private long accumulatedTime;

	private Vector<State> offsprings;

	private ResourcesFactory<State> statesFactory;

	private State[] oneStateArray;

	public AstarTree(Operation[] operations, Heuristic[] heuristics,
	                 ResourcesFactory<State> statesFactory) {
		this.operations = operations;
		this.heuristics = heuristics;
		this.visitedStates = new HashSet<String>();
		this.openStateLists = new Btree[heuristics.length];
		for (Btree<State> stateList : this.openStateLists) {
			stateList = new Btree<State>();
		}
		this.timeHeuristics = new long[this.heuristics.length];
		this.generatedStatesOperator = new long[this.operations.length];
		this.repeatedGeneratedStatesOperator = new long[this.operations.length];
		this.timeOperators = new long[this.operations.length];
		this.offsprings = new Vector<State>();
		this.statesFactory = statesFactory;
		this.oneStateArray = new State[1];
	}

	public void visit(State state, int deep) {
		this.statesFactory.releaseResource(state);
	}

	public void setDebugMode(boolean debug) {
		DEBUG = debug;
	}

	public void setPrintStatsMode(boolean printStats) {
		PRINT_STATS = printStats;
	}

	public State start(long time, int maximumVisitedStates, State initialState) {
		long targetTime = System.currentTimeMillis() + time;
		if (time <= 0 || DEBUG) {
			targetTime = Long.MAX_VALUE;
		}
		if (maximumVisitedStates <= 0) {
			maximumVisitedStates = Integer.MAX_VALUE;
		}
		int currentVisitedStates = 0;

		int heuristicsLength = this.heuristics.length;
		int operatorsLength = this.operations.length;
		int heuristicIndex;
		int operatorIndex;
		State current;
		int i, j;
		long t1;
		long t2;
		String hash;
		boolean exists;
		this.startAlgorithmTime = System.currentTimeMillis();
		int[] heuristicsArray;
		long timeNextPrint = System.currentTimeMillis() + TIME_PRINT_STATS;

		State[] currentStates = null;
		// initializing, cleaning structures.
		if (initialState != null) {
			visitedStates.clear();
			// visit and clear the btree
			openStateLists[0].visitInOrder(this);
			for (Btree<State> openStateList : openStateLists) {
				openStateList.clear();
			}

			bestHeuristic = Integer.MAX_VALUE;
			currentStates = this.oneStateArray;
			for (i = 0; i < heuristicsLength; i++) {
				initialState.heuristic[i] = heuristics[i].calculateHeuristic(initialState);
			}
			for (Btree<State> openStateList : openStateLists) {
				openStateList.add(initialState);
			}

			visitedStatesNumber = 0;
			generatedStates = 0;
			repeatedGeneratedStates = 0;
			for (i = 0; i < generatedStatesOperator.length; i++) {
				generatedStatesOperator[i] = 0;
			}
			for (i = 0; i < repeatedGeneratedStatesOperator.length; i++) {
				repeatedGeneratedStatesOperator[i] = 0;
			}
			timeHeuristic = 0;
			for (i = 0; i < timeHeuristics.length; i++) {
				timeHeuristics[i] = 0;
			}
			timeOperator = 0;
			for (i = 0; i < timeOperators.length; i++) {
				timeOperators[i] = 0;
			}
			timeToHash = 0;
			timeHashTable = 0;
			timeAddToTree = 0;
		}

		while (bestHeuristic > 0 && targetTime >= System.currentTimeMillis()
				&& currentVisitedStates < maximumVisitedStates
				&& !openStateLists[0].isEmpty()) {
			for (i = 0; i < openStateLists.length; i++) {
				currentStates[i] = openStateLists[i].getFirst();
			}
			// clean up same states
			for (i = 0; i < currentStates.length - 1; i++) {
				if (currentStates[i] != null) {
					for (j = i + 1; j < currentStates.length; j++) {
						if (currentStates[i] == currentStates[j]) {
							currentStates[j] = null;
						}
					}
				}
			}

			if (DEBUG) {
				System.out.println("---------- A STAR (get first nodes, one per heuristic if " +
						"there are enough nodes) ----------");
			}

			for (heuristicIndex = 0; heuristicIndex < currentStates.length; heuristicIndex++) {
				current = currentStates[heuristicIndex];
				if (current != null) {
					currentVisitedStates++;
					if (DEBUG) {
						System.out.println("----------------------------------------");
						System.out.println("------------- Current node -------------");
						System.out.println(current);
					}
					visitedStatesNumber++;
					heuristicsArray = current.heuristic;
					for (i = 0; i < heuristicsLength; i++) {
						if (heuristicsArray[i] < bestHeuristic
								|| (heuristicsArray[i] == bestHeuristic
									&& current.cost < bestState.cost)) {
							if (bestState != null) {
								this.statesFactory.releaseResource(bestState);
							}
							bestState = current;
							bestHeuristic = heuristicsArray[i];
						}
					}
					if (DEBUG) {
						System.out.println("-------------- Offspring ---------------");
					}
					for (operatorIndex = 0; operatorIndex < operatorsLength; operatorIndex++) {
						if (DEBUG) {
							System.out.println("-------------- Operator: "
									+ operations[operatorIndex].getName());
						}
						t1 = System.nanoTime();
						offsprings.clear();
						operations[operatorIndex].apply(current, offsprings, statesFactory);
						t2 = System.nanoTime();
						timeOperator += t2 - t1;
						timeOperators[operatorIndex] += t2 - t1;
						for (i = 0; i < offsprings.size(); i++) {
							State offspring = offsprings.get(i);
							generatedStates++;
							generatedStatesOperator[operatorIndex]++;
							t1 = System.nanoTime();
							hash = offspring.getHash();
							t2 = System.nanoTime();
							timeToHash += t2 - t1;
							t1 = System.nanoTime();
							exists = visitedStates.contains(hash);
							t2 = System.nanoTime();
							timeHashTable += t2 - t1;
							if (exists) {
								repeatedGeneratedStates++;
								repeatedGeneratedStatesOperator[operatorIndex]++;
								this.statesFactory.releaseResource(offspring);
							} else {
								t1 = System.nanoTime();
								visitedStates.add(hash);
								t2 = System.nanoTime();
								timeHashTable += t2 - t1;
								for (i = 0; i < heuristicsLength; i++) {
									t1 = System.nanoTime();
									offspring.heuristic[i] =
											heuristics[i].calculateHeuristic(offspring);
									t2 = System.nanoTime();
									timeHeuristic += t2 - t1;
									timeHeuristics[i] += t2 - t1;
								}
								t1 = System.nanoTime();
								for (Btree<State> openStateList : openStateLists) {
									openStateList.add(offspring);
								}
								t2 = System.nanoTime();
								timeAddToTree += t2 - t1;
							}
							if (DEBUG) {
								System.out.print("-------------- NODE ");
								if (exists) {
									System.out.print(" (repeated)");
								}
								System.out.println();
								System.out.println(offspring);
								try {
									System.in.read();
								} catch (Exception e) {
								}
							}
						}
					}
				}
				if (bestState != current) {
					this.statesFactory.releaseResource(current);
				}
			}
			if (PRINT_STATS && System.currentTimeMillis() > timeNextPrint) {
				printStats();
				timeNextPrint = System.currentTimeMillis() + TIME_PRINT_STATS;
			}
		}
		if (PRINT_STATS) {
			printStats();
		}
		this.accumulatedTime += System.currentTimeMillis() - this.startAlgorithmTime;
		if (bestHeuristic == 0) {
			return bestState;
		} else {
			return null;
		}
	}

	public State getBestState() {
		return this.bestState;
	}

	private void printStats() {
		long timeExecuting = System.currentTimeMillis() - this.startAlgorithmTime
				+ this.accumulatedTime;
		System.out.println("Time executing: " + timeExecuting + " ms");
		System.out.println("Visited states: " + visitedStatesNumber);
		double seconds = timeExecuting / 1000;
		double statesPerSecond = visitedStatesNumber / seconds;
		System.out.println("Visited states per second: " + statesPerSecond);

		System.out.println("Generated states: " + generatedStates);
		System.out.println("Generated states per second: " + (generatedStates / seconds));
		System.out.println("Generated states repeated: " + repeatedGeneratedStates);
		double collisions = repeatedGeneratedStates * 100.0 / generatedStates;
		System.out.println("Collisions: " + collisions + " %");

		System.out.println("Time operators: " + timeOperator + " ns");
		double operatorsPerVisitedState = timeOperator * 1.0 / visitedStatesNumber;
		System.out.println("Time operators per visited state: " + operatorsPerVisitedState + " ns");
		for (int i = 0; i < timeOperators.length; i++) {
			System.out.println("\t" + this.operations[i].getName() + ":");
			System.out.println("\t\t time " + timeOperators[i]);
			System.out.println("\t\t time per visit " +
					(timeOperators[i] * 1.0 / visitedStatesNumber));
			System.out.println("\t\t generated states (per visit): " +
					this.generatedStatesOperator[i] + " (" +
					(this.generatedStatesOperator[i] * 1.0 / visitedStatesNumber) + ")");
			System.out.println("\t\t repeated generated states (per visit): " +
					this.repeatedGeneratedStatesOperator[i] + " (" +
					(this.repeatedGeneratedStatesOperator[i] * 1.0 / visitedStatesNumber) + ")");
			System.out.println("\t\t collisions: " +
					this.repeatedGeneratedStatesOperator[i] * 100.0
							/ this.generatedStatesOperator[i]);
		}
		System.out.println("Time generating hash: " + timeToHash);
		double timeHashPerState = timeToHash * 1.0 / generatedStates;
		System.out.println("Time generating hash per state: " + timeHashPerState + " ns");
		System.out.println("Time hashtable: " + timeHashTable + " ns");
		System.out.println("Size hashtable: " + this.visitedStates.size());

		System.out.println("Time heuristic: " + timeHeuristic + " ns");
		double heuristicPerGenerateState = timeHeuristic * 1.0 / generatedStates;
		System.out.println("Time heuristic per generatedState state: " +
				heuristicPerGenerateState + " ns");
		for (int i = 0; i < timeHeuristics.length; i++) {
			System.out.println("\t" + this.heuristics[i].getName() + ": ");
			System.out.println("\t\t time " + timeHeuristics[i]);
			System.out.println("\t\t time por generatedState " +
					(timeHeuristics[i] * 1.0 / generatedStates));
		}

		System.out.println("Time adding to tree: " + timeAddToTree + " ns");
		long treeSize = generatedStates - (visitedStatesNumber + repeatedGeneratedStates);
		long realTreeSize = this.openStateLists[0].getSize();
		System.out.println("Approximate tree size: " + treeSize);
		System.out.println("Tree size: " + realTreeSize);
		double ratioList = timeAddToTree * 1.0 / treeSize;
		System.out.println("Ratio Time / Tree Size: " + ratioList);

		System.out.println("-----------------------------------------------------");
	}

	public State continueAlgorithm(long time, int maximumVisitedStates) {
		return start(time, maximumVisitedStates, null);
	}

}
