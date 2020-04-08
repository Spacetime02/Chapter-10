package othello.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.IntConsumer;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import othello.core.Othello;
import othello.core.Position;
import othello.core.players.HumanPlayer;
import othello.core.players.RandomComputerPlayer;
import othello.util.tuple.Pair;

class GridPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int MIN_CELL_SIZE    = 10;
	private static final int BORDER_THICKNESS = 2;

	private static final int SCROLL_SPEED = 5;

	private Othello game;

	private JScrollPane scrollPane;

	private GridListener listener;

	private Position hoverPos = null;

	private boolean[][] validMoveGrid;

	GridPanel(int gridSize, boolean blackHuman, String blackName, boolean whiteHuman, String whiteName, int cacheDepth, int searchDepth, JScrollPane scrollPane, IntConsumer blackScoreCallback, IntConsumer whiteScoreCallback) {
		super(null, true);

		this.scrollPane = scrollPane;

		setBackground(Colors.BACKGROUND_0);
		setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.CYAN));
		setCursor(Cursors.HAND);

		setup(gridSize, blackHuman, blackName, whiteHuman, whiteName, cacheDepth, searchDepth, blackScoreCallback, whiteScoreCallback);

		new Thread(() -> {
			game.playGame();

			int score1 = game.getBlackScore();
			int score2 = game.getWhiteScore();
			int comp   = Integer.compare(score1, score2);

			String n1 = game.getWhitePlayer().getName();
			String n2 = game.getBlackPlayer().getName();
			if (comp > 0)
				JOptionPane.showMessageDialog(this, n1 + " beat " + n2 + " " + score1 + " to " + score2 + "!", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
			else if (comp < 0)
				JOptionPane.showMessageDialog(this, n2 + " beat " + n1 + " " + score2 + " to " + score1 + "!", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(this, n1 + " and " + n2 + " tied " + score1 + " to " + score2 + "!", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
		}).start();
	}

	void setup(int gridSize, boolean blackHuman, String blackName, boolean whiteHuman, String whiteName, int cacheDepth, int searchDepth, IntConsumer blackScoreCallback, IntConsumer whiteScoreCallback) {
//		Player player1 = blackHuman ? new HumanPlayer(blackName) : new RecursiveComputerPlayer(blackName, cacheDepth, searchDepth);
//		Player player2 = whiteHuman ? new HumanPlayer(whiteName) : new RecursiveComputerPlayer(whiteName, cacheDepth, searchDepth);

		// TODO use recursive computer players.
		game = new Othello(gridSize, blackHuman ? new HumanPlayer(blackName) : new RandomComputerPlayer(blackName), whiteHuman ? new HumanPlayer(whiteName) : new RandomComputerPlayer(whiteName), () -> {
			Point p = getMousePosition();
			listener.update(p != null && listener.clickable(processPos(p)));
		}, blackScoreCallback, whiteScoreCallback);

		scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(SCROLL_SPEED);

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

		boolean isPlayer1 = game.isBlackPlayer();

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

		g2D.setBackground(g2D.getBackground());

		// Reset Anti-aliasing
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		// Reset Stroke
		Stroke stroke = new BasicStroke(1f);
		g2D.setStroke(stroke);

		// Reset Background
		g2D.clearRect(minVisX, minVisY, visWidth, visHeight);

		for (int i = minI; i < maxIP1; i++) {
			int y = BORDER_THICKNESS + cellSize * i;

			for (int j = minJ; j < maxJP1; j++) {
				Position pos = new Position(i, j);

				int x = BORDER_THICKNESS + cellSize * j;

				boolean taken      = game.isTaken(pos);
				boolean blackTaken = game.isBlack(pos);
				boolean hover      = pos.equals(hoverPos);
				boolean valid      = validMoveGrid[i][j];

				Color bg;

				if (taken)
					if (blackTaken)
						bg = Color.BLACK;
					else
						bg = Color.WHITE;
				else if (valid)
					if (hover)
						bg = Colors.ORANGE;
					else if (isPlayer1)
						bg = Colors.BLUE;
					else
						bg = Colors.RED;
				else
					bg = Colors.BACKGROUND_1;

				// Draw Cells
				g2D.setColor(bg);

				g2D.fillRect(x, y, cellSizeNoBorder, cellSizeNoBorder);

			}
		}
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

		int minSize = MIN_CELL_SIZE * game.getGridSize() + BORDER_THICKNESS;

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
