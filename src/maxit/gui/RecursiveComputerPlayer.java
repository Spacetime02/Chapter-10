package maxit.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import maxit.core.ComputerPlayer;
import maxit.core.MAXIT;
import maxit.core.Position;
import maxit.util.tuple.IntPair;
import maxit.util.tuple.Pair;

public class RecursiveComputerPlayer extends ComputerPlayer {

	private final int maxCacheDepth;
	private final int maxRecursiveDepth;

	public RecursiveComputerPlayer(String name, int maxCacheDepth, int maxRecursiveDepth) {
		super(name);
		this.maxCacheDepth = maxCacheDepth;
		this.maxRecursiveDepth = maxRecursiveDepth;
	}

	@Override
	protected Position computeMove(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, Supplier<Position> userInput) {
		outer: while (true) {
			ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

			int gridSize = valueGrid.length;

			Position[] validMoves = MAXIT.getValidMoves(takenGrid, currentPos, horizontal);

			@SuppressWarnings("rawtypes")
			Future[] futures = new Future[validMoves.length];

			for (int i = 0; i < validMoves.length; i++) {
				score = MAXIT.simulateMove(valueGrid, takenGrid, validMoves[i], score);
				boolean[][] taken = new boolean[gridSize][];
				for (int j = 0; j < gridSize; j++)
					taken[j] = Arrays.copyOf(takenGrid[j], gridSize);
				futures[i] = threadPool.submit(new Evaluator(valueGrid, taken, currentPos, !horizontal, score, oppScore));
				score = MAXIT.undoSimulateMove(valueGrid, takenGrid, validMoves[i], score);
			}

			Position bestMove = validMoves[0];

			int bestVal;

			try {
				bestVal = (int) futures[0].get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				threadPool.shutdownNow();
				continue outer;
			}

			int val;
			for (int i = 0; i < validMoves.length; i++) {
				try {
					val = (int) futures[i].get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
					threadPool.shutdownNow();
					continue outer;
				}

				if (val > bestVal) {
					bestMove = validMoves[i];
					bestVal = val;
				}
			}
			return bestMove;
		}
	}

	private class Evaluator implements Callable<Integer> {

		private final Map<Pair<Pair<TakenGridWrapper, Position>, IntPair>, Integer> cache = new HashMap<>();

		private int[][]     valueGrid;
		private boolean[][] takenGrid;
		private Position    currentPos;
		private boolean     horizontal;
		private int         score;
		private int         oppScore;

		private Evaluator(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore) {
			this.valueGrid = valueGrid;
			this.takenGrid = takenGrid;
			this.currentPos = currentPos;
			this.horizontal = horizontal;
			this.score = score;
			this.oppScore = oppScore;
		}

		@Override
		public Integer call() throws Exception {
			return evaluateRecursively(Integer.MIN_VALUE, Integer.MAX_VALUE, 1, true);
		}

		private int evaluateRecursively(int alpha, int beta, int depth, boolean oppPlaying) {
			boolean writeCache = depth < maxCacheDepth;

			Integer value = readCache();

			if (value != null)
				return value;

			if (depth >= maxRecursiveDepth || !MAXIT.hasRemainingMoves(currentPos, takenGrid, writeCache)) {
				value = score - oppScore;
				if (writeCache)
					writeCache(value);
				return value;
			}

			int val = oppPlaying ? beta : alpha;
			int nextVal;
			int sc;

			Position[] validMoves = MAXIT.getValidMoves(takenGrid, currentPos, horizontal ^ oppPlaying);
			for (Position move : validMoves) {
				sc = MAXIT.simulateMove(valueGrid, takenGrid, move, oppPlaying ? oppScore : score);
				if (oppPlaying)
					oppScore = sc;
				else
					score = sc;

				nextVal = evaluateRecursively(alpha, beta, depth + 1, !oppPlaying);

				sc = MAXIT.undoSimulateMove(valueGrid, takenGrid, move, oppPlaying ? oppScore : score);
				if (oppPlaying)
					oppScore = sc;
				else
					score = sc;

				if (oppPlaying ? nextVal < val : nextVal > val) {
					val = nextVal;
					if (oppPlaying)
						beta = val;
					else
						alpha = val;
					if (alpha >= beta)
						break;
				}
			}
			if (writeCache)
				writeCache(val);
			return val;
		}

		private Integer readCache() {
			return cache.get(new Pair<>(new Pair<>(new TakenGridWrapper(), currentPos), new IntPair(score, oppScore)));
		}

		private Integer writeCache(Integer value) {
			return cache.put(new Pair<>(new Pair<>(new TakenGridWrapper(), currentPos), new IntPair(score, oppScore)), value);
		}

		private class TakenGridWrapper {

			private final boolean[][] grid;

			private TakenGridWrapper() {
				int gridSize = valueGrid.length;
				this.grid = new boolean[gridSize][];
				for (int i = 0; i < gridSize; i++)
					grid[i] = Arrays.copyOf(takenGrid[i], gridSize);
			}

			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof TakenGridWrapper))
					return false;
				return Arrays.deepEquals(grid, ((TakenGridWrapper) obj).grid);
			}

			@Override
			public int hashCode() {
				return Arrays.deepHashCode(grid);
			}

		}

	}

}
