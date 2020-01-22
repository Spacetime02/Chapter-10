package tic_tac_toe_5x5;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Game {

	public static final int BOARD_SIZE = 5;
	public static final int CELL_COUNT = BOARD_SIZE * BOARD_SIZE;
	public static final int MATCH_SIZE = 4;

	private final Map<BoardState, Move> cache;

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
		int eval = state.evaluateImmediate(0);
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

	private Point computeMove() {
		return computeMove(currentPlayer, BoardState.HUMAN_IMMEDIATE + turn, BoardState.COMPUTER_IMMEDIATE + turn, turn).toPoint();
	}

	private Move computeMove(int alpha, int beta, int depth, int player) {
		BoardState canonical = new BoardState();
		Transform inv = state.canonicalize(canonical);
		Move move = cache.get(canonical);
		Move best = new Move(null, BoardState.UNKNOWN);
		if (move != null)
			return move.withDepth(depth);
		Point[] empty = state.getCells(BoardState.EMPTY);
		if (empty.length == 0) {
			move = new Move(null, state.evaluateImmediate(depth));
			cache.put(new BoardState(state), move);
			return move;
		}
		int opponent = 3 - player;
		int value = player == BoardState.COMPUTER ? alpha : beta;
		for (Point p : state.getCells(BoardState.EMPTY)) {
			state.set(p, player);
			move = computeMove(alpha, beta, depth + 1, opponent);
			state.set(p, BoardState.EMPTY);

			boolean comp = player == BoardState.COMPUTER && move.value > value;
			boolean humn = player == BoardState.HUMAN && move.value < value;
			if (comp || humn) {
				value = move.value;
				if (comp)
					alpha = value;
				else
					beta = value;
				best = move;
				if (alpha >= beta)
					break;
			}
		}
		cache.put(new BoardState(canonical), best);
		System.out.println(move);
		return move.transform(inv);
	}
}
