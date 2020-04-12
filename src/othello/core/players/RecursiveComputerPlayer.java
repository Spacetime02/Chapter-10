package othello.core.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

import othello.core.Othello;
import othello.core.Position;
import othello.util.tuple.IntPair;
import othello.util.tuple.Pair;

public class RecursiveComputerPlayer extends ComputerPlayer {

	private static final Random RANDY = new Random();

	private final int maxCacheDepth;
	private final int maxSearchDepth;

	public RecursiveComputerPlayer(String name, int maxCacheDepth, int maxSearchDepth) {
		super(name);
		this.maxCacheDepth = maxCacheDepth;
		this.maxSearchDepth = maxSearchDepth;
	}

	private static boolean[][] copyGrid(boolean[][] grid) {
		int gridSize = grid.length;

		boolean[][] copyGrid = new boolean[gridSize][];

		for (int i = 0; i < gridSize; i++)
			copyGrid[i] = Arrays.copyOf(grid[i], gridSize);

		return copyGrid;
	}

	private static boolean[][] copyGridInv(boolean[][] grid) {
		int gridSize = grid.length;

		boolean[][] copyGrid = new boolean[gridSize][];

		for (int i = 0; i < gridSize; i++) {
			boolean[] gridRow     = grid[i];
			boolean[] copyGridRow = new boolean[gridSize];
			for (int j = 0; j < gridSize; j++)
				copyGridRow[j] = !gridRow[j];
			copyGrid[i] = copyGridRow;
		}

		return copyGrid;
	}

	@Override
	protected Position computeMove(boolean[][] curGrid, boolean[][] takenGrid, int curScore, int oppScore, String playerName) {
		int gridSize = curGrid.length;

		ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		Position[] validMoves = Othello.getValidMoves(curGrid, takenGrid);

		int moveCount = validMoves.length;

		@SuppressWarnings("rawtypes")
		Future[] futures = new Future[validMoves.length];

		AtomicInteger counter = new AtomicInteger();

		String moveCountStr = Integer.toString(moveCount);

		System.out.println(playerName);
		for (int i = 0; i < moveCount; i++) {
			boolean[][] curGridCopy   = copyGrid(curGrid);
			boolean[][] takenGridCopy = copyGrid(takenGrid);

			Position move = validMoves[i];

			String format = "  Move %" + moveCountStr.length() + "d/" + moveCountStr + " at " + String.format("%8s", move) + ": %6d%n";

			IntConsumer callback = value -> System.out.printf(format, counter.incrementAndGet(), value);

			IntPair scoreCopies = Othello.simulateMove(curGridCopy, takenGridCopy, move, curScore, oppScore);

			boolean[][] taken = new boolean[gridSize][];
			for (int j = 0; j < gridSize; j++)
				taken[j] = Arrays.copyOf(takenGrid[j], gridSize);

			futures[i] = threadPool.submit(new Evaluator(curGridCopy, takenGridCopy, scoreCopies.first, scoreCopies.second, callback));
		}

		Position bestMove = validMoves[0];

		int bestVal;

		try {
			bestVal = (int) futures[0].get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			threadPool.shutdownNow();
			System.exit(-1);
			return null;
		}

		int val;
		for (int i = 0; i < validMoves.length; i++) {
			try {
				val = (int) futures[i].get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				threadPool.shutdownNow();
				System.exit(-1);
				return null;
			}

			if (val > bestVal) {
				bestMove = validMoves[i];
				bestVal = val;
			}
		}
		System.out.println("  Best at " + bestMove + ": " + bestVal);
		System.out.println();

		return bestMove;

	}

	private class Evaluator implements Callable<Integer> {

		private final Map<Pair<Boolean, Pair<GridWrapper, GridWrapper>>, Integer> cache = new HashMap<>();

		private boolean[][] curGrid;
		private boolean[][] takenGrid;
		private int         curScore;
		private int         oppScore;

		private IntConsumer callback;

		private Evaluator(boolean[][] curGrid, boolean[][] takenGrid, int curScore, int oppScore, IntConsumer callback) {
			// @formatter:off
			this.curGrid   = curGrid;
			this.takenGrid = takenGrid;
			this.curScore  = curScore;
			this.oppScore  = oppScore;
			this.callback  = callback;
			// @formatter:on
		}

		@Override
		public Integer call() {
			try {
				int val = evaluateRecursively(copyGridInv(curGrid), copyGrid(takenGrid), curScore, oppScore, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, true, false);
				callback.accept(val);
				return val;
			} catch (Throwable t) {
				t.printStackTrace();
				System.exit(-1);
				return null;
			}
		}

		private int evaluateRecursively(boolean[][] grid, boolean[][] takenGrid, int curScore, int oppScore, int alpha, int beta, int depth, boolean oppPlaying, boolean prevForfeit) {
			if (depth <= 16) {
				String        depthString = Integer.toString(depth);
				StringBuilder builder     = new StringBuilder(depth + depthString.length());
				for (int i = 0; i < depth; i++)
					builder.append(' ');
				builder.append(depthString);
				System.out.println(builder.toString());
			}
			boolean writeCache = depth <= maxCacheDepth;
			writeCache = false;

			Integer value = readCache(prevForfeit, grid, takenGrid);

			if (value != null)
				return value;

			Position[] validMoves = Othello.getValidMoves(grid, takenGrid);

			boolean forfeit = validMoves[0] == null;

			if (depth >= maxSearchDepth || prevForfeit && forfeit) {
				value = curScore - oppScore;
				if (writeCache)
					writeCache(value, prevForfeit, grid, takenGrid);
				return value;
			}

			int val = oppPlaying ? beta : alpha;
			int nextVal;

			int curScoreCopy;
			int oppScoreCopy;

			List<Position> validMoveList = new ArrayList<>(Arrays.asList(validMoves));
			Collections.shuffle(validMoveList, RANDY);
			validMoveList.toArray(validMoves);

			for (Position move : validMoves) {
				boolean[][] gridCopy      = copyGrid(grid);
				boolean[][] takenGridCopy = copyGrid(takenGrid);

				IntPair scores = Othello.simulateMove(gridCopy, takenGridCopy, move, oppPlaying ? oppScore : curScore, oppPlaying ? curScore : oppScore);

				if (oppPlaying) {
					oppScoreCopy = scores.first;
					curScoreCopy = scores.second;
				} else {
					curScoreCopy = scores.first;
					oppScoreCopy = scores.second;
				}

				nextVal = evaluateRecursively(copyGridInv(gridCopy), copyGrid(takenGridCopy), curScoreCopy, oppScoreCopy, alpha, beta, depth + 1, !oppPlaying, forfeit);

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
				writeCache(val, prevForfeit, grid, takenGrid);
			return val;
		}

		private Integer readCache(boolean prevForfeit, boolean[][] grid, boolean[][] takenGrid) {
			return cache.get(new Pair<>(prevForfeit, new Pair<>(new GridWrapper(grid), new GridWrapper(takenGrid))));
		}

		private Integer writeCache(Integer value, boolean prevForfeit, boolean[][] grid, boolean[][] takenGrid) {
			return cache.put(new Pair<>(prevForfeit, new Pair<>(new GridWrapper(grid), new GridWrapper(takenGrid))), value);
		}

		private class GridWrapper {

			private final boolean[][] grid;

			private GridWrapper(boolean[][] grid) {
				int gridSize = grid.length;
				this.grid = new boolean[gridSize][];
				for (int i = 0; i < gridSize; i++)
					this.grid[i] = Arrays.copyOf(grid[i], gridSize);
			}

			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof GridWrapper))
					return false;
				GridWrapper wrapper = (GridWrapper) obj;
				for (int i = 0; i < grid.length; i++)
					for (int j = 0; j < grid.length; j++)
						if (grid[i][j] != wrapper.grid[i][j])
							return false;
				return true;
			}

			@Override
			public int hashCode() {
				return Arrays.deepHashCode(grid);
			}

		}

	}

}
