package maxit.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.function.IntConsumer;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import maxit.core.MAXIT;
import maxit.core.Position;
import maxit.core.players.HumanPlayer;
import maxit.core.players.Player;
import maxit.core.players.RecursiveComputerPlayer;
import maxit.util.Search;
import maxit.util.function.FloatUnaryOperator;
import maxit.util.tuple.Pair;

class GridPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int BASE_MIN_CELL_SIZE     = 5;
	private static final int MIN_CELL_SIZE_PER_CHAR = 15;
	private static final int BORDER_THICKNESS       = 2;

	private static final int BASE_SCROLL_SPEED     = 5;
	private static final int SCROLL_SPEED_PER_CHAR = 1;

	private MAXIT game;

	private int maxStrLength;

	/**
	 * Includes border thickness.
	 */
	private int minCellSize;

	private JScrollPane scrollPane;

	private String[][] strGrid;

	private GridListener listener;

	private Position hoverPos = null;

	private boolean[][] validMoveGrid;

	GridPanel(int gridSize, int minValue, int maxValue, boolean human1, String name1, boolean human2, String name2, boolean horizontal1, int cacheDepth, int searchDepth, JScrollPane scrollPane, IntConsumer scoreCallback1, IntConsumer scoreCallback2) {
		super(null, true);

		this.scrollPane = scrollPane;

		setBackground(Colors.BACKGROUND_0);
		setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.CYAN));
		setCursor(Cursors.HAND);

		setup(gridSize, minValue, maxValue, human1, name1, human2, name2, horizontal1, cacheDepth, searchDepth, scoreCallback1, scoreCallback2);

		new Thread(() -> {
			game.playGame();

			int score1 = game.getScore1();
			int score2 = game.getScore2();
			int comp   = Integer.compare(score1, score2);

			String n1 = game.getPlayer1().getName();
			String n2 = game.getPlayer2().getName();
			if (comp > 0)
				JOptionPane.showMessageDialog(this, n1 + " beat " + n2 + " " + score1 + " to " + score2 + "!", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
			else if (comp < 0)
				JOptionPane.showMessageDialog(this, n2 + " beat " + n1 + " " + score2 + " to " + score1 + "!", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(this, n1 + " and " + n2 + " tied " + score1 + " to " + score2 + "!", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
		}).start();
	}

	void setup(int gridSize, int minValue, int maxValue, boolean human1, String name1, boolean human2, String name2, boolean horizontal1, int cacheDepth, int searchDepth, IntConsumer scoreCallback1, IntConsumer scoreCallback2) {

		maxStrLength = 1;

		Player player1 = human1 ? new HumanPlayer(name1) : new RecursiveComputerPlayer(name1, cacheDepth, searchDepth);
		Player player2 = human2 ? new HumanPlayer(name2) : new RecursiveComputerPlayer(name2, cacheDepth, searchDepth);
		game = new MAXIT(gridSize, minValue, maxValue, player1, player2, horizontal1, () -> EventQueue.invokeLater(() -> {
			Point p = getMousePosition();
			listener.update(p != null && listener.clickable(processPos(p)));
		}), scoreCallback1, scoreCallback2);

		strGrid = new String[gridSize][gridSize];

		for (int i = 0; i < gridSize; i++) {

			String[] strRow = strGrid[i];

			for (int j = 0; j < gridSize; j++) {
				strRow[j] = Integer.toString(game.getValue(i, j));
				if (strRow[j].length() > maxStrLength)
					maxStrLength = strRow[j].length();
			}
		}

		minCellSize = BASE_MIN_CELL_SIZE + MIN_CELL_SIZE_PER_CHAR * maxStrLength + BORDER_THICKNESS;

		int scrollSpeed = BASE_SCROLL_SPEED + SCROLL_SPEED_PER_CHAR * maxStrLength;
		scrollPane.getVerticalScrollBar().setUnitIncrement(scrollSpeed);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(scrollSpeed);

		if (isVisible())
			repaint();

		if (listener != null) {
			removeMouseListener(listener);
			removeMouseMotionListener(listener);
		}
		listener = new GridListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);

		validMoveGrid = new boolean[gridSize][gridSize];

		Position[] positions = game.getValidMoves();

		for (Position p : positions)
			validMoveGrid[p.i][p.j] = true;
	}

	@Override
	public void paint(Graphics g) {
		int gridSize = game.getGridSize();

		boolean isPlayer1 = game.isPlayer1();

		Graphics2D g2D = (Graphics2D) g;

		Rectangle visible = getVisibleRect();

		int cellSize = computeCellSize();

		int cellSizeNoBorder = cellSize - BORDER_THICKNESS;

		int visHeight       = visible.height;
		int visWidth        = visible.width;
		int minVisY         = visible.y;
		int minVisX         = visible.x;
		int maxVisY         = minVisY + visHeight;
		int maxVisX         = minVisX + visWidth;
		int maxVisYNoBorder = Math.min(maxVisY, gridSize * cellSize);
		int maxVisXNoBorder = Math.min(maxVisX, gridSize * cellSize);
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

//		g2D.translate(-minVisX, -minVisY);
//		g2D.transform(gui.inverseTransform);

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
			Font font = Fonts.get(Font.MONOSPACED, Font.BOLD, size/* (float) (size * scale) */);

			FontMetrics metrics = g2D.getFontMetrics(font);

			int fontW = metrics.charsWidth(test, 0, maxStrLength);
			int fontH = metrics.getAscent() - metrics.getDescent();

			return Math.max(fontW, fontH) - maxTextSize;
		};

		float fontSize = Math.nextDown(Search.binarySearch(fontSizeOp, 0f, cellSize * 2f));

		Font font = Fonts.get(Font.MONOSPACED, Font.BOLD, fontSize);

		FontMetrics metrics = g2D.getFontMetrics(font);

		int textHeight = metrics.getAscent() - metrics.getDescent();

		g2D.setFont(font);

		for (int i = minI; i < maxIP1; i++) {
//			int y = borderThickness + cellSize * i;
			int y = BORDER_THICKNESS + cellSize * i;

			int textY = (cellSizeNoBorder + textHeight) / 2;

			String[] strRow = strGrid[i];

			for (int j = minJ; j < maxJP1; j++) {
				Position pos = new Position(i, j);

//				int x = borderThickness + cellSize * j;
				int x = BORDER_THICKNESS + cellSize * j;

				boolean taken  = game.isTaken(pos);
				boolean taken1 = game.isTakenByPlayer1(pos);
				boolean hover  = pos.equals(hoverPos);
				boolean valid  = validMoveGrid[i][j];

				Color bg;
				Color fg;

				if (taken) {
					if (taken1) {
						bg = Colors.ON_BACKGROUND;
						fg = Colors.blend(Colors.BACKGROUND_0, Colors.BLUE, 0.7f);
					} else {
						bg = Colors.ON_BACKGROUND;
						fg = Colors.blend(Colors.BACKGROUND_0, Colors.RED, 0.7f);
					}
				} else if (valid)
					if (hover) {
						bg = Colors.ORANGE;
						fg = Colors.BACKGROUND_1;
					} else if (isPlayer1) {
						bg = Colors.BLUE;
						fg = Colors.BACKGROUND_1;
					} else {
						bg = Colors.RED;
						fg = Colors.BACKGROUND_1;
					}
				else {
					bg = Colors.BACKGROUND_1;
					fg = Colors.ON_BACKGROUND;
				}

				// Draw Cells
				g2D.setColor(bg);

				g2D.fillRect(x, y, cellSizeNoBorder, cellSizeNoBorder);

				// Draw Text
				g2D.setColor(fg);

				String str = strRow[j];

				int textWidth = metrics.stringWidth(str);

				int textX = (cellSizeNoBorder - textWidth) / 2;

				g2D.drawString(Integer.toString(game.getValue(i, j)), x + textX, y + textY);

//				g2D.translate(cellSize, 0);
			}
//			g2D.translate(cellSize * (minJ - maxJP1), cellSize);
		}

//		g2D.translate(minVisX, minVisY);
//		super.paint(g);
	}

	private Pair<Position, Point> processPos(Point pos) {
		int cellSize = computeCellSize();

		int i = pos.y / cellSize;
		int j = pos.x / cellSize;
		int y = pos.y % cellSize;
		int x = pos.x % cellSize;

		return new Pair<>(new Position(i, j), new Point(x, y));
	}

	private int computeCellSize() {
		return (computeUnroundedSize() - BORDER_THICKNESS) / game.getGridSize();
	}

	private int computeNaiveSize() {
		Rectangle bounds = scrollPane.getBounds();
		return Math.min(bounds.width, bounds.height) - GUI.SCROLLBAR_SIZE;
	}

	private int computeUnroundedSize() {
		int size = computeNaiveSize();

		int minSize = minCellSize * game.getGridSize() + BORDER_THICKNESS;

		if (size < minSize)
			size = minSize;

		return size;
	}

	private int computeSize() {
		return computeCellSize() * game.getGridSize() + BORDER_THICKNESS;
	}

	private Dimension computeDimension() {
		int size = computeSize();
		return new Dimension(size, size);
	}

	@Override
	public Dimension getPreferredSize() {
		return computeDimension();
	}

	@Override
	public Dimension getMinimumSize() {
		return computeDimension();
	}

	@Override
	public Dimension getMaximumSize() {
		return computeDimension();
	}

	private class GridListener implements MouseListener, MouseMotionListener {

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {
			Point pos = e.getPoint();

			Pair<Position, Point> processedPos = processPos(pos);

			boolean clickable = clickable(processedPos);

			if (clickable) {
				game.queueUserInput(processedPos.first);
				update(true);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {
			hoverPos = null;
			update(true);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			handleMouseMove(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			handleMouseMove(e);
		}

		private void handleMouseMove(MouseEvent e) {
			Point pos = e.getPoint();

			Pair<Position, Point> processedPos = processPos(pos);

			validMoveGrid = game.getValidMoveGrid();

			hoverPos = processedPos.first;

			update(clickable(processedPos));
		}

		private boolean clickable(Pair<Position, Point> processedPos) {
			return processedPos.second.y >= BORDER_THICKNESS && processedPos.second.x >= BORDER_THICKNESS && game.isValid(processedPos.first);
		}

		private void update(boolean clickable) {
			setCursor(clickable && game.getCurrentPlayer() instanceof HumanPlayer ? Cursors.HAND : Cursors.DEFAULT);
			validMoveGrid = game.getValidMoveGrid();
			repaint();
		}

	}

}
