package tic_tac_toe_5x5;

import java.awt.Point;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class Game {

	private static class ComputeMoveTask implements Callable<Integer> {

		private final Map<BoardState, Integer> cache;

		private final int index;
		private final BoardState state;
		private final int turn;

		private ComputeMoveTask(Point cell, BoardState state, int index, int turn) {
			this.state = new BoardState(state);
			this.index = index;
			this.turn = turn;
			cache = new HashMap<>();
			this.state.set(cell, BoardState.COMPUTER);
		}

		@Override
		public Integer call() {
			return evaluateBoard(BoardState.HUMAN_WIN, BoardState.COMPUTER_WIN, turn + 1, BoardState.HUMAN);
		}

		private int evaluateBoard(int alpha, int beta, int depth, int player) {
			if (depth != state.getData().cardinality())
				throw new Error("Wrong depth (" + depth + "):\n" + state);
			if (player == BoardState.EMPTY)
				throw new Error("Empty State");
			if (depth > CELL_COUNT)
				throw new Error("Too Deep");
			boolean useCache = depth < MAX_CACHE_DEPTH;
			Transform inv = null;
			BoardState copy = new BoardState(state);
			if (useCache)
				inv = state.canonicalize();
			Integer value;
			if (useCache) {
				value = cache.get(state);
				if (value != BoardState.UNKNOWN) {
					state.set(inv.apply(state));
					return value;
				}
			}
			value = state.evaluateImmediate();
			if (useCache && value != BoardState.UNKNOWN) {
				cache.put(new BoardState(state), value);
				state.set(inv.apply(state));
				return value;
			}
			boolean isComputer = player == BoardState.COMPUTER;
			int val = isComputer ? alpha : beta;
			int nextVal;
			int opponent = 3 - player;
			Point[] emptyCells = state.getCells(BoardState.EMPTY);
			for (Point p : emptyCells)
				if (state.get(p) != 0)
					throw new Error("WHEE");
			int i = 0;
			for (Point emptyCell : emptyCells) {
				Point p = new Point(emptyCell);
				if (state.get(p) != BoardState.EMPTY)
					throw new Error("Wrong State (" + state.get(emptyCell) + ") at " + emptyCell.x + "," + emptyCell.y + " depth=" + depth + "\n" + state);
				if (depth < 20) {
					StringBuilder sb = new StringBuilder();
					sb.append(index).append(":").append(depth);
					for (int j = turn; j < depth; j++)
						sb.append("-");
					sb.append(++i).append("/").append(emptyCells.length + 1).append("\n");
					print(Logger.OUT, sb);
				}
				state.set(p, player);
				nextVal = evaluateBoard(alpha, beta, depth + 1, opponent);
				state.set(p, BoardState.EMPTY);
				if (isComputer ? nextVal > val : nextVal < val) {
					val = nextVal;
					if (isComputer)
						alpha = val;
					else
						beta = val;
					if (alpha >= beta)
						break;
				}
			}
			if (useCache) {
				cache.put(new BoardState(state), val);
				state.set(inv.apply(state));
			}
			if (!copy.equals(state))
				throw new Error("copy != state:\ns\nState:\n\n" + state + "\n\nOG:\n\n" + copy + "\n\nDepth: " + depth + "\n\nTransform: " + inv.invert() + "\n\n\n");
			return val;
		}

	}

	private static class Logger extends Thread {

		private static final BlockingQueue<String> ERR = new LinkedBlockingQueue<>();
		private static final ThreadGroup GROUP = new ThreadGroup("loggers");

		private static final BlockingQueue<String> OUT = new LinkedBlockingQueue<>();

		static {
			new Logger(System.out, OUT, "out").start();
			new Logger(System.err, ERR, "err").start();
		}

		private final String name;
		private final BlockingQueue<String> queue;
		private final PrintStream stream;

		Logger(PrintStream stream, BlockingQueue<String> queue, String name) {
			super(GROUP, name);
			this.stream = stream;
			this.queue = queue;
			this.name = name;
			setDaemon(true);
		}

		@Override
		public void run() {
			while (true)
				try {
					stream.print(queue.take());
				}
				catch (InterruptedException e) {
					InterruptedException ie = new InterruptedException("Logger " + name + " was interrupted.");
					ie.initCause(e);
					ie.printStackTrace(System.err);
				}
		}

	}

	public static final int BOARD_SIZE = 5;
	public static final int CELL_COUNT = BOARD_SIZE * BOARD_SIZE;
	public static final int MATCH_SIZE = 4;

	private static final int MAX_CACHE_DEPTH = 20;

	private static final int MAX_CONCURRENCY = Math.min(Runtime.getRuntime().availableProcessors(), CELL_COUNT);

	private static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		return sw.toString();
	}

	private static void print(BlockingQueue<String> queue, Object... obj) {
		StringBuilder builder = new StringBuilder();
		for (Object o : obj)
			builder.append(o);
		queue.add(builder.toString());
	}

	private static void print(BlockingQueue<String> queue, String string) {
		queue.add(string);
	}

	@SuppressWarnings("unused")
	private static void printf(BlockingQueue<String> queue, String format, Object... args) {
		queue.add(String.format(format, args));
	}

	@SuppressWarnings("unused")
	private static void println(BlockingQueue<String> queue, Object... obj) {
		StringBuilder builder = new StringBuilder();
		for (Object o : obj) {
			builder.append(o);
			builder.append('\n');
		}
		queue.add(builder.toString());
	}

	private final GUI gui;

	private final LinkedBlockingQueue<Point> inQueue;

	private int xPlayer;
	private int oPlayer;
	private int currentPlayer;

	private Random randy;

	private final BoardState state;

	private int turn;

	public Game(GUI gui) {
		state = new BoardState();
		inQueue = new LinkedBlockingQueue<>();
		randy = new Random();
		this.gui = gui;
	}

	private Point computeMove() {
		ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CONCURRENCY);
		Point[] emptyCells = state.getCells(BoardState.EMPTY);
		int num = emptyCells.length;
		if (num == 0)
			return null;

		@SuppressWarnings("rawtypes")
		Future[] futures = new Future[num];
		for (int i = 0; i < num; i++)
			futures[i] = threadPool.submit(new ComputeMoveTask(emptyCells[i], state, i, turn));
		Point bestCell = emptyCells[0];
		int bestVal;
		try {
			bestVal = (int) futures[0].get();
		}
		catch (InterruptedException | ExecutionException e) {
			print(Logger.ERR, getStackTrace(e));
			return null;
		}
		if (bestVal >= BoardState.COMPUTER_WIN) {
			threadPool.shutdownNow();
			return bestCell;
		}
		int val;
		Point cell;
		for (int i = 1; i < num; i++) {
			try {
				val = (int) futures[i].get();
			}
			catch (InterruptedException | ExecutionException e) {
				print(Logger.ERR, getStackTrace(e));
				return null;
			}
			if (val >= bestVal) {
				cell = emptyCells[i];
				if (val >= BoardState.COMPUTER_WIN) {
					threadPool.shutdownNow();
					return cell;
				}
				bestCell = cell;
				bestVal = val;
			}
		}
		threadPool.shutdownNow();
		return bestCell;
	}

	public Integer evaluateImmediate() {
		return state.evaluateImmediate();
	}

	public void doTurn() {
		if (currentPlayer == BoardState.COMPUTER)
			if (turn == 0)
				makeMove(randy.nextInt(BOARD_SIZE), randy.nextInt(BOARD_SIZE));
			else {
				Point p = computeMove();
				inQueue.clear();
				makeMove(p.x, p.y);
			}
		else {
			Point p;
			do {
				p = readIn();
			} while (state.get(p.x, p.y) != BoardState.EMPTY);
			makeMove(p.x, p.y);
		}
		turn++;
		currentPlayer = 3 - currentPlayer;
		Integer eval = state.evaluateImmediate();
		if (eval != BoardState.UNKNOWN)
			print(Logger.OUT, "gg: ", eval, "\n\n");
	}

	public int get(int i, int j) {
		return state.get(i, j);
	}

	public int getCurrent() {
		return currentPlayer;
	}

	public int getO() {
		return oPlayer;
	}

	public int getX() {
		return xPlayer;
	}

	private void makeMove(int i, int j) {
		gui.setState(i, j, currentPlayer);
		state.set(i, j, currentPlayer);
	}

	public void queueMove(int i, int j) {
		Point p = new Point(i, j);
		while (true) {
			try {
				inQueue.put(p);
				break;
			}
			catch (InterruptedException e) {
				print(Logger.ERR, getStackTrace(e));
			}
		}
	}

	private Point readIn() {
		Point in = null;
		while (in == null)
			try {
				in = inQueue.take();
			}
			catch (InterruptedException e) {
				print(Logger.ERR, getStackTrace(e));
			}
		return in;
	}

	public void start() {
		state.clear();
		turn = 0;
		currentPlayer = randy.nextBoolean() ? BoardState.COMPUTER : BoardState.HUMAN;
		xPlayer = currentPlayer;
		oPlayer = 3 - currentPlayer;
	}

}
