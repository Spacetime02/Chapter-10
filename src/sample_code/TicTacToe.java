package sample_code;

import sample_code.weiss.util.HashMap;
import sample_code.weiss.util.Map;

final class Position implements Comparable<Position> {

	private int[][] board;

	public Position(int[][] theBoard) {
		board = new int[3][3];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				board[i][j] = theBoard[i][j];
	}

	public boolean equals(Object rhs) {
		if (!(rhs instanceof Position))
			return false;

		// Position other = (Position) rhs;

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (board[i][j] != ((Position) rhs).board[i][j])
					return false;
		return true;
	}

	public int hashCode() {
		int hashVal = 0;

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				hashVal = hashVal * 4 + board[i][j];

		return hashVal;
	}

	@Override
	public int compareTo(Position o) {
		return Integer.compare(hashCode(), o.hashCode());
	}
}

class TicTacToe {

	public static final int HUMAN = 0;
	public static final int COMPUTER = 1;
	public static final int EMPTY = 2;

	public static final int HUMAN_WIN = 0;
	public static final int DRAW = 1;
	public static final int UNCLEAR = 2;
	public static final int COMPUTER_WIN_LAST = 3;
	public static final int COMPUTER_WIN_IMMEDIATE = 12;

	// Constructor
	public TicTacToe() {
		clearBoard();
	}

	public int[][] getBoard() {
		return board;
	}

	public Best chooseMove(int side) {
		return chooseMove(side, HUMAN_WIN, PRIORITIZE_IMMEDIACY ? COMPUTER_WIN_IMMEDIATE : COMPUTER_WIN_LAST, 0);
	}

	// Find optimal move
	private Best chooseMove(int side, int alpha, int beta, int depth) {
		int opp; // The other side
		Best reply; // Opponent's best reply
		// int dc; // Placeholder
		int simpleEval; // Result of an immediate evaluation
		Position thisPosition = new Position(board);
		int tableDepth = 3; // Max depth placed in Trans. table
		int bestRow = 0;
		int bestColumn = 0;
		int value;

		if ((simpleEval = positionValue(depth)) != UNCLEAR)
			return new Best(simpleEval);

		if (USE_TRANSPOSITION_TABLE)
			if (depth == 0)
				transpositions.clear();
			else if (depth >= 3 && depth <= tableDepth) {
				Integer lookupVal = transpositions.get(thisPosition);
				if (lookupVal != null)
					return new Best(lookupVal);
			}

		if (ALPHA_BETA_PRUNING) {
			if (side == COMPUTER) {
				opp = HUMAN;
				value = alpha;
			}
			else {
				opp = COMPUTER;
				value = beta;
			}
		}
		else {
			if (side == COMPUTER) {
				opp = HUMAN;
				value = HUMAN_WIN;
			}
			else {
				opp = COMPUTER;
				value = PRIORITIZE_IMMEDIACY ? COMPUTER_WIN_IMMEDIATE : COMPUTER_WIN_LAST;
			}
		}

		// if (ALPHA_BETA_PRUNING)
		Outer: for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++)
				if (squareIsEmpty(row, column)) {
					place(row, column, side);
					reply = chooseMove(opp, alpha, beta, depth + 1);
					place(row, column, EMPTY);

					if (side == COMPUTER && reply.val > value || side == HUMAN && reply.val < value) {
						if (side == COMPUTER)
							alpha = value = reply.val;
						else
							beta = value = reply.val;

						bestRow = row;
						bestColumn = column;
						if (ALPHA_BETA_PRUNING && alpha >= beta)
							break Outer; // Refutation
					}
				}
			// }
			// else
			// for (int row = 0; row < 3; row++)
			// for (int column = 0; column < 3; column++)
			// if (squareIsEmpty(row, column)) {
			// place(row, column, side);
			// reply = chooseMove(opp, alpha, beta, depth + 1);
			// place(row, column, EMPTY);
			//
			// if ()
		}

		if (USE_TRANSPOSITION_TABLE && depth <= tableDepth)
			transpositions.put(thisPosition, value);

		return new Best(value, bestRow, bestColumn);

	}

	// Play move, including checking legality
	public boolean playMove(int side, int row, int column) {
		if (row < 0 || row >= 3 || column < 0 || column >= 3 || board[row][column] != EMPTY)
			return false;
		board[row][column] = side;
		return true;
	}

	// Simple supporting routines
	public void clearBoard() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				board[i][j] = EMPTY;
	}

	public boolean boardIsFull() {
		for (int row = 0; row < 3; row++)
			for (int column = 0; column < 3; column++)
				if (board[row][column] == EMPTY)
					return false;
		return true;
	}

	public boolean isAWin(int side) {
		int row, column;

		/* Look for all in a row */
		for (row = 0; row < 3; row++) {
			for (column = 0; column < 3; column++)
				if (board[row][column] != side)
					break;
			if (column >= 3)
				return true;
		}

		/* Look for all in a column */
		for (column = 0; column < 3; column++) {
			for (row = 0; row < 3; row++)
				if (board[row][column] != side)
					break;
			if (row >= 3)
				return true;
		}

		/* Look on diagonals */
		if (board[1][1] == side && board[2][2] == side && board[0][0] == side)
			return true;

		if (board[0][2] == side && board[1][1] == side && board[2][0] == side)
			return true;

		return false;
	}

	private Map<Position, Integer> transpositions = new HashMap<Position, Integer>();
	// private Map<Position, Integer> transpositions = new TreeMap<Position, Integer>();
	private int[][] board = new int[3][3];

	// Play a move, possibly clearing a square
	private void place(int row, int column, int piece) {
		board[row][column] = piece;
	}

	private boolean squareIsEmpty(int row, int column) {
		return board[row][column] == EMPTY;
	}

	private static final boolean PRIORITIZE_IMMEDIACY = true;
	private static final boolean ALPHA_BETA_PRUNING = false;
	private static final boolean USE_TRANSPOSITION_TABLE = false;

	// Compute static value of current position (win, draw, etc.)
	private int positionValue(int depth) {
		return isAWin(COMPUTER) ? PRIORITIZE_IMMEDIACY ? COMPUTER_WIN_IMMEDIATE - depth : COMPUTER_WIN_LAST : isAWin(HUMAN) ? HUMAN_WIN : boardIsFull() ? DRAW : UNCLEAR;
	}
}
