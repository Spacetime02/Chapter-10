package maxit.gui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JPanel;
import javax.swing.JViewport;

import maxit.util.algo.Search;
import maxit.util.function.FloatUnaryOperator;
import maxit.util.tuple.Pair;

class GridPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final static int MIN_CELL_SIZE    = 20;
	private final static int BORDER_THICKNESS = 50;

	private static final Random RANDY = new Random();

	private static final BlockingQueue<Point> INPUT_QUEUE = new LinkedBlockingDeque<Point>();

	private int n;

	private int maxStrLength;

	private JViewport viewport;

	private int[][] grid;

	private String[][] strGrid;

	private boolean[][] taken;

	private GridListener listener;

	GridPanel(int n, int m, JViewport viewport) {
		super(null, true);
		this.viewport = viewport;
		setBackground(Colors.BACKGROUND_0);
//		Border border = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK);
//		setBorder(border);
		setCursor(GUI.HAND_CURSOR);
		setup(n, m);
	}

	void setup(int n, int m) {
		maxStrLength = 1;
		this.n = n;
//		this.m = m;
		grid = new int[n][n];
		strGrid = new String[n][n];
		taken = new boolean[n][n];
		for (int i = 0; i < n; i++) {

			int[] row = grid[i];

			String[] strRow = strGrid[i];

			for (int j = 0; j < n; j++) {
				row[j] = RANDY.nextInt(m);
				strRow[j] = Integer.toString(row[j]);
				if (strRow[j].length() > maxStrLength)
					maxStrLength = strRow[j].length();
			}
		}

		if (isVisible())
			repaint();

		if (listener != null) {
			removeMouseListener(listener);
			removeMouseMotionListener(listener);
		}
		listener = new GridListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;

		GraphicsConfiguration devConf = g2D.getDeviceConfiguration();

		AffineTransform defTrans = devConf.getDefaultTransform();
		AffineTransform invTrans;
		try {
			invTrans = defTrans.createInverse();
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
			System.exit(-1);
			return;
		}

		double scale = defTrans.getScaleX();
		assert scale == defTrans.getScaleY();

		Rectangle bounds  = getBounds();
		Rectangle visible = getVisibleRect();

		int minCellSize = (int) Math.round(MIN_CELL_SIZE * scale);

		int borderThickness = (int) Math.round(BORDER_THICKNESS * scale);

		int cellSize = Math.max(minCellSize + borderThickness, ((int) Math.round(Math.min(bounds.width, bounds.height) * scale) - borderThickness) / n);

		int cellSizeNoBorder = cellSize - borderThickness;

		int visHeight       = (int) Math.round(visible.height * scale);
		int visWidth        = (int) Math.round(visible.width * scale);
		int minVisY         = (int) Math.round(visible.y * scale);
		int minVisX         = (int) Math.round(visible.x * scale);
		int maxVisY         = minVisY + visHeight;
		int maxVisX         = minVisX + visWidth;
		int maxVisYNoBorder = Math.min(maxVisY, n * cellSize);
		int maxVisXNoBorder = Math.min(maxVisX, n * cellSize);
		int minI            = minVisY / cellSize;
		int minJ            = minVisX / cellSize;
		int maxIP1          = (maxVisYNoBorder + cellSizeNoBorder) / cellSize;
		int maxJP1          = (maxVisXNoBorder + cellSizeNoBorder) / cellSize;
		int maxTextSize     = cellSizeNoBorder * 3 / 4;
//		int maxBorderI      = maxVisY / cellSize + 1;
//		int maxBorderJ      = maxVisX / cellSize + 1;
//		int imgHeight       = maxBorderI == maxIP1 ? visHeight : maxIP1 * cellSize + borderThickness - minVisY; // Includes border after cell, making extension unnecessary?
//		int imgWidth        = maxBorderJ == maxJP1 ? visWidth : maxJP1 * cellSize + borderThickness - minVisX;
//		int minBorderI      = (minVisY - cellSizeNoBorder) / cellSize;
//		int minBorderJ      = (minVisX - cellSizeNoBorder) / cellSize;
//		int minY            = minI * cellSize;
//		int minX            = minJ * cellSize;

		g2D.transform(invTrans);
		g2D.translate(-minVisX, -minVisY);

		g2D.setBackground(g2D.getBackground());

		// Reset Anti-aliasing
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		// Reset Stroke
		Stroke stroke = new BasicStroke(1f);
		g2D.setStroke(stroke);

		// Reset Background
		g2D.clearRect(minVisX, minVisY, visWidth, visHeight);

		// Solve for Font Size
		// Only Works for Monospace Font
		char[] test = new char[maxStrLength];
		Arrays.fill(test, '#');

		FloatUnaryOperator fontSizeOp = size -> {
			Font font = GUI.getFont(Font.MONOSPACED, Font.PLAIN, (float) (size * scale));

			FontMetrics metrics = g2D.getFontMetrics(font);

			int fontW = metrics.charsWidth(test, 0, maxStrLength);
			int fontH = metrics.getAscent() - metrics.getDescent();

			return Math.max(fontW, fontH) - maxTextSize;
		};

		float fontSize = Math.nextDown(Search.binarySearch(fontSizeOp, 0f, cellSize * 2f));

		Font font = GUI.getFont(Font.DIALOG, Font.PLAIN, fontSize);

		FontMetrics metrics = g2D.getFontMetrics(font);

		int textHeight = metrics.getAscent() - metrics.getDescent();

		g2D.setFont(font);

		g2D.translate(borderThickness, borderThickness);
		for (int i = minI; i < maxIP1; i++) {
			int y = (cellSizeNoBorder + textHeight) / 2;

			String[] strRow = strGrid[i];

			for (int j = minJ; j < maxJP1; j++) {
				// Draw Cells
				g2D.setColor(Colors.BACKGROUND_1);

				g2D.fillRect(0, 0, cellSizeNoBorder, cellSizeNoBorder);

				// Draw Text
				if (!taken[i][j]) {
					g2D.setColor(Colors.ON_BACKGROUND);

					String str = strRow[j];

					int textWidth = metrics.stringWidth(str);

					int x = (cellSizeNoBorder - textWidth) / 2;

					g2D.drawString(Integer.toString(grid[i][j]), x, y);
				}

				g2D.translate(cellSize, 0);
			}
			g2D.translate(cellSize * (minJ - maxJP1), cellSize);
		}

		g2D.translate(minVisX, minVisY);
		g2D.transform(defTrans);
	}

	private Pair<Point, Point> getJIXY(Point pos) {
		Rectangle bounds = getBounds();

		int cellSize = Math.max(MIN_CELL_SIZE + BORDER_THICKNESS, (Math.min(bounds.width, bounds.height) - BORDER_THICKNESS) / n);

		int i = pos.y / cellSize;
		int j = pos.x / cellSize;
		int y = pos.y % cellSize;
		int x = pos.x % cellSize;

		return new Pair<>(new Point(j, i), new Point(x, y));
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = viewport.getSize();

		int size = Math.max((MIN_CELL_SIZE + BORDER_THICKNESS) * n + BORDER_THICKNESS, Math.min(dim.height, dim.width) - GUI.SCROLLBAR_WIDTH);

		return new Dimension(size, size);
	}

	private class GridListener implements MouseListener, MouseMotionListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			Point pos = e.getPoint();

			Pair<Point, Point> jixy = getJIXY(pos);

			Point cell      = jixy.first;  // (j, i)
			Point posInCell = jixy.second; // (x, y)

			boolean onBorder    = onBorder(posInCell);
			boolean unclickable = unclickable(onBorder, posInCell);

			if (!unclickable) {
				INPUT_QUEUE.add(cell); // TODO must recheck clickability upon processing!
				taken[cell.y][cell.x] = true;
				update(cell, posInCell, onBorder, unclickable);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			Point pos = e.getPoint();

			Pair<Point, Point> jixy = getJIXY(pos);

			Point cell      = jixy.first;  // (j, i)
			Point posInCell = jixy.second; // (x, y)

			boolean onBorder    = onBorder(posInCell);
			boolean unclickable = unclickable(onBorder, posInCell);

			update(cell, posInCell, onBorder, unclickable);
		}

		private boolean onBorder(Point posInCell) {
			return posInCell.y < BORDER_THICKNESS || posInCell.x < BORDER_THICKNESS;
		}

		private boolean unclickable(boolean onBorder, Point cell) {
			return onBorder || taken[cell.y][cell.x];
		}

		private void update(Point cell, Point posInCell, boolean onBorder, boolean unclickable) {
			if (cell.x >= n || cell.y >= n)
				return;

			setCursor(unclickable ? GUI.DEFAULT_CURSOR : GUI.HAND_CURSOR);

			repaint();
		}

	}

}
