package maxit.core.players;

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

import maxit.core.MAXIT;
import maxit.core.Position;
import maxit.util.tuple.IntPair;
import maxit.util.tuple.Pair;

public class RecursiveComputerPlayer extends ComputerPlayer {

	private static final boolean RANDOM_FIRST_MOVE = true;

	private static final Random RANDY = new Random();

	private final int maxCacheDepth;
	private final int maxSearchDepth;

	public RecursiveComputerPlayer(String name, int maxCacheDepth, int maxSearchDepth) {
		super(name);
		this.maxCacheDepth = maxCacheDepth;
		this.maxSearchDepth = maxSearchDepth;
	}

	@Override
	protected Position computeMove(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, String playerName) {
		int gridSize = valueGrid.length;

		if (RANDOM_FIRST_MOVE && currentPos == null)
			return new Position(RANDY.nextInt(gridSize), RANDY.nextInt(gridSize));

		ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		Position[] validMoves = MAXIT.getValidMoves(takenGrid, currentPos, horizontal);

		int moveCount = validMoves.length;

		@SuppressWarnings("rawtypes")
		Future[] futures = new Future[validMoves.length];

		AtomicInteger counter = new AtomicInteger();

		String moveCountStr = Integer.toString(moveCount);

		System.out.println(playerName);
		for (int i = 0; i < moveCount; i++) {
			Position move = validMoves[i];

			String format = "  Move %" + moveCountStr.length() + "d/" + moveCountStr + " at " + String.format("%8s", move) + ": %6d%n";

			IntConsumer callback = value -> System.out.printf(format, counter.incrementAndGet(), value);

			score = MAXIT.simulateMove(valueGrid, takenGrid, validMoves[i], score);

			boolean[][] taken = new boolean[gridSize][];
			for (int j = 0; j < gridSize; j++)
				taken[j] = Arrays.copyOf(takenGrid[j], gridSize);

			futures[i] = threadPool.submit(new Evaluator(valueGrid, taken, validMoves[i], !horizontal, score, oppScore, callback));

			score = MAXIT.undoSimulateMove(valueGrid, takenGrid, validMoves[i], score);
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

		private final Map<Pair<Pair<TakenGridWrapper, Position>, IntPair>, Integer> cache = new HashMap<>();

		private int[][]     valueGrid;
		private boolean[][] takenGrid;
		private Position    currentPos;
		private boolean     horizontal;
		private int         score;
		private int         oppScore;

		private IntConsumer callback;

		private Evaluator(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, IntConsumer callback) {
			// @formatter:off
			this.valueGrid  = valueGrid;
			this.takenGrid  = takenGrid;
			this.currentPos = currentPos;
			this.horizontal = horizontal;
			this.score      = score;
			this.oppScore   = oppScore;
			this.callback   = callback;
			// @formtter:on
		}

		@Override
		public Integer call() {
			try {
				int val = evaluateRecursively(Integer.MIN_VALUE, Integer.MAX_VALUE, 1, true);
				callback.accept(val);
				return val;
			} catch (Throwable t) {
				t.printStackTrace();
				System.exit(-1);
				return null;
			}
		}

		private int evaluateRecursively(int alpha, int beta, int depth, boolean oppPlaying) {
			boolean writeCache = depth <= maxCacheDepth;
			writeCache = false;

			Integer value = readCache();

			if (value != null)
				return value;

			if (depth >= maxSearchDepth || !MAXIT.hasRemainingMoves(currentPos, takenGrid, horizontal ^ oppPlaying)) {
				value = score - oppScore;
				if (writeCache)
					writeCache(value, depth);
				return value;
			}

			int val = oppPlaying ? beta : alpha;
			int nextVal;
			int sc;

			Position[]     validMoves    = MAXIT.getValidMoves(takenGrid, currentPos, horizontal ^ oppPlaying);
			List<Position> validMoveList = new ArrayList<>(Arrays.asList(validMoves));
			Collections.shuffle(validMoveList, RANDY);
			validMoveList.toArray(validMoves);

			for (Position move : validMoves) {
				sc = MAXIT.simulateMove(valueGrid, takenGrid, move, oppPlaying ? oppScore : score);
				if (oppPlaying)
					oppScore = sc;
				else
					score = sc;
				Position temp = currentPos;
				currentPos = move;

				nextVal = evaluateRecursively(alpha, beta, depth + 1, !oppPlaying);

				currentPos = temp;
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
				writeCache(val, depth);
			return val;
		}

		private Integer readCache() {
			return cache.get(new Pair<>(new Pair<>(new TakenGridWrapper(), currentPos), new IntPair(score, oppScore)));
		}

		private Integer writeCache(Integer value, int depth) {
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
				TakenGridWrapper tgw = (TakenGridWrapper) obj;
				for (int i = 0; i < grid.length; i++)
					for (int j = 0; j < grid.length; j++)
						if (grid[i][j] != tgw.grid[i][j])
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
