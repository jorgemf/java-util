package com.livae.util.search.astar;

import com.livae.util.ResourcesFactory;
import com.livae.util.tree.Btree;
import com.livae.util.tree.BtreeVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AstarTreeThreads implements BtreeVisitor<State> {

	private static final int TIME_PRINT_STATS = 1000;

	private static boolean PRINT_STATS = false;

	private Lock lock;

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

	private ResourcesFactory<State> statesFactory;

	private Thread[] threads;

	private long targetTime;

	private int maximumVisitedStates;

	private int currentVisitedStates;

	public AstarTreeThreads(Operation[] operations, Heuristic heuristic,
	                        ResourcesFactory<State> statesFactory, int threads) {
		this(operations, new Heuristic[]{heuristic}, statesFactory, threads);
	}

	public AstarTreeThreads(Operation[] operations, Heuristic[] heuristics,
	                        ResourcesFactory<State> statesFactory, int threads) {
		if (threads <= 1) {
			throw new IllegalArgumentException("Threads should be greater than 1");
		}
		this.lock = new ReentrantLock();
		this.threads = new Thread[threads];
		for (int i = 0; i < this.threads.length; i++) {
			this.threads[i] = new Thread(new ThreadIteration());
		}
		this.operations = operations;
		this.heuristics = heuristics;
		this.visitedStates = new HashSet<>();
		//noinspection unchecked
		this.openStateLists = new Btree[heuristics.length];
		for (int i = 0; i < this.openStateLists.length; i++) {
			this.openStateLists[i] = new Btree<>();
		}
		this.timeHeuristics = new long[this.heuristics.length];
		this.generatedStatesOperator = new long[this.operations.length];
		this.repeatedGeneratedStatesOperator = new long[this.operations.length];
		this.timeOperators = new long[this.operations.length];
		this.statesFactory = statesFactory;
	}

	public void visit(State state, int deep) {
		this.statesFactory.releaseResource(state);
	}

	public void setPrintStatsMode(boolean printStats) {
		PRINT_STATS = printStats;
	}

	public State start(int maximumVisitedStates, State initialState) {
		return start(-1, maximumVisitedStates, initialState);
	}

	public State start(long time, State initialState) {
		return start(time, -1, initialState);
	}

	public State continueAlgorithm(long time, int maximumVisitedStates) {
		return start(time, maximumVisitedStates, null);
	}

	public State continueAlgorithm(int maximumVisitedStates) {
		return start(-1, maximumVisitedStates, null);
	}

	public State continueAlgorithm(long time) {
		return start(time, -1, null);
	}

	public State start(long time, int maximumVisitedStates, State initialState) {
		targetTime = System.currentTimeMillis() + time;
		if (time <= 0) {
			targetTime = Long.MAX_VALUE;
		}
		if (maximumVisitedStates <= 0) {
			maximumVisitedStates = Integer.MAX_VALUE;
		}
		this.maximumVisitedStates = maximumVisitedStates;
		this.currentVisitedStates = 0;

		int heuristicsLength = this.heuristics.length;
		this.startAlgorithmTime = System.currentTimeMillis();
		// initializing, cleaning structures.
		if (initialState != null) {
			visitedStates.clear();
			// visit and clear the btree
			openStateLists[0].visitInOrder(this);
			for (Btree<State> openStateList : openStateLists) {
				openStateList.clear();
			}

			bestHeuristic = Integer.MAX_VALUE;
			for (int i = 0; i < heuristicsLength; i++) {
				initialState.heuristic[i] = heuristics[i].calculateHeuristic(initialState);
			}
			for (Btree<State> openStateList : openStateLists) {
				openStateList.add(initialState);
			}

			visitedStatesNumber = 0;
			generatedStates = 0;
			repeatedGeneratedStates = 0;
			for (int i = 0; i < generatedStatesOperator.length; i++) {
				generatedStatesOperator[i] = 0;
			}
			for (int i = 0; i < repeatedGeneratedStatesOperator.length; i++) {
				repeatedGeneratedStatesOperator[i] = 0;
			}
			timeHeuristic = 0;
			for (int i = 0; i < timeHeuristics.length; i++) {
				timeHeuristics[i] = 0;
			}
			timeOperator = 0;
			for (int i = 0; i < timeOperators.length; i++) {
				timeOperators[i] = 0;
			}
			timeToHash = 0;
			timeHashTable = 0;
			timeAddToTree = 0;
		}

		threads[0].start();
		for (int i = 1; i < threads.length; i++) {
			long size;
			do {
				lock.lock();
				size = openStateLists[0].getSize();
				lock.unlock();
				if (size == 0) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} while (size == 0);
			threads[i].start();
		}

		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		long timeExecuting =
		  System.currentTimeMillis() - this.startAlgorithmTime + this.accumulatedTime;
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
			                   (this.repeatedGeneratedStatesOperator[i] * 1.0 /
			                    visitedStatesNumber) + ")");
			System.out.println("\t\t collisions: " +
			                   this.repeatedGeneratedStatesOperator[i] * 100.0 /
			                   this.generatedStatesOperator[i]);
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

	class ThreadIteration implements Runnable {

		@Override
		public void run() {
			int heuristicsLength = heuristics.length;
			int operatorsLength = operations.length;
			int heuristicIndex;
			int operatorIndex;
			State current;
			int i, j;
			long t1;
			long t2;
			String hash;
			boolean exists;
			int[] heuristicsArray;
			long timeNextPrint = System.currentTimeMillis() + TIME_PRINT_STATS;

			State[] currentStates = new State[heuristicsLength];
			ArrayList<State> offsprings = new ArrayList<>();
			ArrayList<State> statesToAdd = new ArrayList<>();
			long iterationTimeHeuristic;
			long iterationTimeHeuristics[] = new long[heuristicsLength];

			lock.lock();
			while (bestHeuristic > 0 && targetTime >= System.currentTimeMillis() &&
			       currentVisitedStates < maximumVisitedStates && !openStateLists[0].isEmpty()) {
				for (i = 0; i < heuristicsLength; i++) {
					currentStates[i] = openStateLists[i].getFirst();
				}
				// clean up same states
				for (i = 0; i < heuristicsLength - 1; i++) {
					if (currentStates[i] != null) {
						for (j = i + 1; j < heuristicsLength; j++) {
							if (currentStates[i] == currentStates[j]) {
								currentStates[j] = null;
							}
						}
					}
				}

				for (heuristicIndex = 0; heuristicIndex < currentStates.length; heuristicIndex++) {
					current = currentStates[heuristicIndex];
					if (current != null) {
						currentVisitedStates++;
						visitedStatesNumber++;
						heuristicsArray = current.heuristic;
						for (i = 0; i < heuristicsLength; i++) {
							if (heuristicsArray[i] < bestHeuristic ||
							    (heuristicsArray[i] == bestHeuristic &&
							     current.cost < bestState.cost)) {
								if (bestState != null) {
									statesFactory.releaseResource(bestState);
								}
								bestState = current;
								bestHeuristic = heuristicsArray[i];
							}
						}
						for (operatorIndex = 0; operatorIndex < operatorsLength; operatorIndex++) {
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
									statesFactory.releaseResource(offspring);
								} else {
									t1 = System.nanoTime();
									visitedStates.add(hash);
									t2 = System.nanoTime();
									timeHashTable += t2 - t1;
									statesToAdd.add(offspring);
								}
							}
						}
					}
					if (bestState != current) {
						statesFactory.releaseResource(current);
					}
				}
				if (PRINT_STATS && System.currentTimeMillis() > timeNextPrint) {
					printStats();
					timeNextPrint = System.currentTimeMillis() + TIME_PRINT_STATS;
				}
				lock.unlock();
				iterationTimeHeuristic = 0;
				for (i = 0; i < heuristicsLength; i++) {
					iterationTimeHeuristics[i] = 0;
				}
				for (State state : statesToAdd) {
					for (i = 0; i < heuristicsLength; i++) {
						t1 = System.nanoTime();
						state.heuristic[i] = heuristics[i].calculateHeuristic(state);
						t2 = System.nanoTime();
						iterationTimeHeuristic += t2 - t1;
						iterationTimeHeuristics[i] += t2 - t1;
					}
				}
				lock.lock();
				timeHeuristic += iterationTimeHeuristic;
				for (i = 0; i < heuristicsLength; i++) {
					timeHeuristics[i] += iterationTimeHeuristics[i];
				}
				t1 = System.nanoTime();
				for (State state : statesToAdd) {
					for (Btree<State> openStateList : openStateLists) {
						openStateList.add(state);
					}
				}
				t2 = System.nanoTime();
				timeAddToTree += t2 - t1;
				statesToAdd.clear();
			}
			lock.unlock();
		}
	}

}
