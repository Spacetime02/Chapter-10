package tic_tac_toe_5x5;

import java.awt.Point;

public class Move {

	public final int i;
	public final int j;
	public final int value;

	public Move(int i, int j, int val) {
		this.i = i;
		this.j = j;
		this.value = val;;
	}

	public Move(Point point, int val) {
		if (point == null) {
			i = -1;
			j = -1;
		} else {
			i = point.x;
			j = point.y;
		}
		this.value = val;
	}

	public Move(Move move) {
		this(move.i, move.j, move.value);
	}

	public Move transform(Transform transform) {
		return transform.apply(this);
	}

	public void apply(BoardState state, int currentPlayer) {
		state.set(i, j, currentPlayer);
	}

	public Move withDepth(int depth) {
		int newVal;
		if (value == BoardState.UNKNOWN)
			newVal = BoardState.UNKNOWN;
		else if (value > BoardState.UNKNOWN)
			newVal = BoardState.COMPUTER_IMMEDIATE - depth;
		else
			newVal = BoardState.HUMAN_IMMEDIATE + depth;
		return new Move(i, j, newVal);
	}

	public Point toPoint() {
		return i < 0 || j < 0 ? null : new Point(i, j);
	}

	@Override
	public String toString() {
		return i + "," + j + " - " + value;
	}

}