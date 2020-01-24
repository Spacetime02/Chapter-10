package tic_tac_toe_5x5;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Game {

	public static final int BOARD_SIZE = 3;
	public static final int CELL_COUNT = BOARD_SIZE * BOARD_SIZE;
	public static final int MATCH_SIZE = 3;

	private static final int MAX_CACHE_DEPTH = 20;

	private final Map<BoardState, Integer> cache;

	private final BoardState state;

	private final LinkedBlockingQueue<Point> inQueue;

	private Random randy;

	private int turn;
	private int xPlayer;
	private int oPlayer;
	private int currentPlayer;

	private final GUI gui;

	public Game(GUI gui) {
		cache = new HashMap<>();
		state = new BoardState();
		inQueue = new LinkedBlockingQueue<>();
		randy = new Random();
		this.gui = gui;
	}

	public int get(int i, int j) {
		return state.get(i, j);
	}

	public void start() {
		state.clear();
		turn = 0;
		currentPlayer = randy.nextBoolean() ? BoardState.COMPUTER : BoardState.HUMAN;
		xPlayer = currentPlayer;
		oPlayer = 3 - currentPlayer;
	}

	public void queueMove(int i, int j) {
		Point p = new Point(i, j);
		while (true) {
			try {
				inQueue.put(p);
				break;
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public int getX() {
		return xPlayer;
	}

	public int getO() {
		return oPlayer;
	}

	public int getCurrent() {
		return currentPlayer;
	}

	public boolean canDoTurn() {
		for (int index = 0; index < CELL_COUNT; index++)
			if (state.get(index) == BoardState.EMPTY)
				return true;
		return false;
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
			System.out.println("gg: " + eval);
	}

	private void makeMove(int i, int j) {
		gui.setState(i, j, currentPlayer);
		state.set(i, j, currentPlayer);
	}

	private Point readIn() {
		Point in = null;
		while (in == null)
			try {
				in = inQueue.take();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		return in;
	}

	private void pt(Point cell, int player, int depth) {
		if (GUI.LOG) {
			char[] names = new char[] { 'E', 'C', 'H' };
			for (int i = 0; i < depth; i++)
				System.out.print('>');
			System.out.println("Test x" + cell.x + " y" + cell.y + " d" + depth + " " + names[player]);
			System.out.println(state);
			System.out.println();
		}

	}

	private Point computeMove() {
		Point[] emptyCells = state.getCells(BoardState.EMPTY);
		if (emptyCells.length == 0)
			return null;
		int alpha = BoardState.HUMAN_WIN;
		int beta = BoardState.COMPUTER_WIN;
		int nextVal;
		Point bestCell = null;
		int i = 0;
		for (Point emptyCell : emptyCells) {
			gui.setTitle(GUI.TITLE + " (Processing " + ++i + "/" + emptyCells.length + ")");
			state.set(emptyCell, BoardState.COMPUTER);
			pt(emptyCell, BoardState.COMPUTER, 0);
			nextVal = evaluateBoard(alpha, beta, turn + 1, BoardState.HUMAN);
			state.set(emptyCell, BoardState.EMPTY);
			pt(emptyCell, BoardState.EMPTY, 0);
			if (nextVal > alpha) {
				alpha = nextVal;
				bestCell = emptyCell;
				if (alpha >= beta)
					break;
			}
		}
		return bestCell;
	}

	private int evaluateBoard(int alpha, int beta, int depth, int player) {
		boolean useCache = depth < MAX_CACHE_DEPTH;
		Transform inv = state.canonicalize();
		Integer value;
		if (useCache) {
			value = cache.get(state);
			if (value != BoardState.UNKNOWN)
				return value;
		}
		value = state.evaluateImmediate();
		if (useCache && value != BoardState.UNKNOWN) {
			cache.put(new BoardState(state), value);
			return value;
		}
		boolean isComputer = player == BoardState.COMPUTER;
		int val = isComputer ? alpha : beta;
		int nextVal;
		int opponent = 3 - player;
		Point[] emptyCells = state.getCells(BoardState.EMPTY);
		int i = 0;
		for (Point emptyCell : emptyCells) {
			if (depth < 10) {
				for (int j = turn; j < depth; j++)
					System.out.print('-');
				System.out.println(++i);
			}
			state.set(emptyCell, player);
			pt(emptyCell, player, depth);
			nextVal = evaluateBoard(alpha, beta, depth + 1, opponent);
			state.set(emptyCell, BoardState.EMPTY);
			pt(emptyCell, BoardState.EMPTY, depth);
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
		if (useCache)
			cache.put(new BoardState(state), val);
		inv.apply(state);
		return val;
	}

}
