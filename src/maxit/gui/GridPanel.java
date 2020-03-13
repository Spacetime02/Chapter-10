package maxit.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JViewport;

class GridPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final static int MIN_CELL_SIZE = 20;

	private static final Random RANDY = new Random();

	private int n;
//	private int m;

	private JViewport viewport;

	private int[][] grid;

	GridPanel(int n, int m, JViewport viewport) {
		super(null, true);
		this.viewport = viewport;
		setBackground(Color.BLUE);
		setCursor(GUI.HAND_CURSOR);
		setup(n, m);
	}

	void setup(int n, int m) {
		this.n = n;
//		this.m = m;
		grid = new int[n][n];
		for (int[] row : grid)
			for (int j = 0; j < n; j++)
				row[j] = RANDY.nextInt(m);
		if (isVisible())
			repaint();
	}

	@Override
	public void paint(Graphics g) {
		Rectangle bounds  = getBounds();
		Rectangle visible = getVisibleRect();

		int cellSize = Math.max(MIN_CELL_SIZE, Math.min(bounds.width, bounds.height) / n);

		int height     = visible.height;
		int width      = visible.width;
		int minVisY    = visible.y;
		int minVisX    = visible.x;
		int maxVisY    = visible.y + height;
		int maxVisX    = visible.x + width;
		int minI       = minVisY / cellSize;
		int minJ       = minVisX / cellSize;
		int maxI       = (maxVisY + cellSize - 1) / cellSize;
		int maxJ       = (maxVisX + cellSize - 1) / cellSize;
		int maxBorderI = maxVisY / cellSize + 1;
		int maxBorderJ = maxVisX / cellSize + 1;
		int minY       = minI * cellSize;
		int minX       = minJ * cellSize;

		System.out.println(bounds);

		Graphics2D g2d = (Graphics2D) g;

		// Anti-aliasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		// Clear Background
		g2d.clearRect(minVisX, minVisY, width, height);

		// Horizontal Lines
		g.setColor(Color.BLACK);
		for (int i = minI; i < maxBorderI; i++) {
			int y = i * cellSize;
			g2d.drawLine(minVisX, y, maxVisX, y);
		}
		for (int j = minJ; j < maxBorderJ; j++) {
			int x = j * cellSize;
			g2d.drawLine(x, minVisY, x, maxVisY);
		}

		Font font = GUI.getFont(Font.DIALOG, Font.PLAIN, cellSize);
		g2d.setFont(font);
		for (int i = minI; i < Math.min(n, maxI); i++) {
			int y = i * cellSize + cellSize / 2;
			for (int j = minJ; j < Math.min(n, maxJ); j++) {
				int x = j * cellSize + cellSize / 2;
				g2d.drawString(Integer.toString(grid[i][j]), x, y);
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = viewport.getSize();

		int size = Math.max(MIN_CELL_SIZE * n, Math.min(dim.height, dim.width) - GUI.SCROLLBAR_WIDTH);
		size += 1 - size % n;
		return new Dimension(size, size);
	}

}
