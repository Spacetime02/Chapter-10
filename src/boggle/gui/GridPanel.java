package boggle.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.JPanel;

class GridPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final char[][] DEFAULT_GRID = new char[][] {};

	private char[][] grid;

	public GridPanel() {
		super(true);
		setGrid(DEFAULT_GRID);
		initUI();
	}

	private void initUI() {
		setBackground(Color.RED);
		setPreferredSize(new Dimension(10, 10));
	}

	public void setGrid(char[][] grid) {
		if (grid.length == 0)
			grid = new char[0][];
		else {
			int width = grid[0].length;
			this.grid = new char[grid.length][];
			for (int i = 0; i < grid.length; i++)
				this.grid[i] = Arrays.copyOf(grid[i], width);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		Container parent = getParent();
		int size = Math.min(parent.getWidth(), parent.getHeight());
		return new Dimension(size, size);
	}

}
