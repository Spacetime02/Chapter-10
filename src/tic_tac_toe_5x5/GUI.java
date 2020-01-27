package tic_tac_toe_5x5;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class GUI extends JFrame {

	private static final class BoardFocusTraversalPolicy extends FocusTraversalPolicy {

		private final GUI gui;

		private final int len;
		private final JButton[] buttons;
		private final boolean[] focusable;

		public BoardFocusTraversalPolicy(GUI gui) {
			this.gui = gui;
			len = Game.CELL_COUNT + 1;
			buttons = new JButton[len];
			focusable = new boolean[len];
			int i = 0;
			for (JButton[] row : gui.buttons)
				for (JButton button : row) {
					this.buttons[i] = button;
					focusable[i++] = true;
				}
			this.buttons[Game.CELL_COUNT] = gui.restartButton;
		}

		private int findIndex(Container aContainer, Component aComponent) throws IllegalArgumentException {
			if (aContainer == null)
				throw new IllegalArgumentException("aContainer is null.");
			if (aComponent == null)
				throw new IllegalArgumentException("aComponent is null.");
			if (aContainer != gui)
				throw new IllegalArgumentException("Wrong aContainer.");
			if (!(aComponent instanceof JButton))
				throw new IllegalArgumentException("Wrong aComponent.");
			JButton button = (JButton) aComponent;
			int index = indexOf(button);
			if (index < 0)
				throw new IllegalArgumentException("Wrong aComponent.");
			return index;
		}

		@Override
		public Component getComponentAfter(Container aContainer, Component aComponent) throws IllegalArgumentException {
			int index = findIndex(aContainer, aComponent);
			int max = len + index;
			for (int i = index + 1, iModLen; i <= max; i++) {
				iModLen = i % len;
				if (focusable[iModLen])
					return buttons[iModLen];
			}
			return null;
		}

		@Override
		public Component getComponentBefore(Container aContainer, Component aComponent) throws IllegalArgumentException {
			int index = findIndex(aContainer, aComponent);
			int max = len + index;
			for (int i = max - 1, iModLen; i >= index; i--) {
				iModLen = i % len;
				if (focusable[iModLen])
					return buttons[iModLen];
			}
			return null;
		}

		@Override
		public Component getDefaultComponent(Container aContainer) throws IllegalArgumentException {
			return getFirstComponent(aContainer);
		}

		@Override
		public Component getFirstComponent(Container aContainer) throws IllegalArgumentException {
			if (aContainer == null)
				throw new IllegalArgumentException();
			for (int i = 0; i < len; i++)
				if (focusable[i])
					return buttons[i];
			return null;
		}

		@Override
		public Component getLastComponent(Container aContainer) throws IllegalArgumentException {
			if (aContainer == null)
				throw new IllegalArgumentException();
			for (int i = len - 1; i >= 0; i--)
				if (focusable[i])
					return buttons[i];
			return null;
		}

		private int indexOf(JButton button) {
			for (int i = 0; i < len; i++)
				if (buttons[i] == button)
					return i;
			return -1;
		}

	}

	private static final class BoardListener implements ActionListener, MouseListener, FocusListener, KeyListener {

		private final int i, j;
		private final int index;

		private int state;

		private final JButton button;
		private final JButton[][] buttons;
		private final GUI gui;

		private boolean hover = false;

		public BoardListener(GUI gui, JButton button, int i, int j) {
			this.gui = gui;
			this.buttons = gui.buttons;
			this.button = button;
			this.i = i;
			this.j = j;
			this.index = BoardState.index(i, j);
			this.state = BoardState.EMPTY;
		}

		// public void reset() {
		// button.setText("");
		// button.setBackground(BACKGROUND_COLOR);
		// gui.policy.focusable[index] = true;
		// }

		@Override
		public void actionPerformed(ActionEvent e) {
			if (gui.policy.focusable[index])
				gui.game.queueMove(i, j);
		}

		@Override
		public void focusGained(FocusEvent e) {
			gui.getRootPane().setDefaultButton(button);
			button.setBackground(BUTTON_FOCUS_COLOR);
		}

		@Override
		public void focusLost(FocusEvent e) {
			button.setBackground(gui.policy.focusable[index] ? hover ? BUTTON_HOVER_COLOR : BACKGROUND_COLOR : BUTTON_DISABLED_COLOR);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			switch (code) {
				case KeyEvent.VK_LEFT:
					for (int num = 1; num <= Game.BOARD_SIZE; num++) {
						int newJ = (j + Game.BOARD_SIZE - num) % Game.BOARD_SIZE;
						if (gui.policy.focusable[gui.boardListeners[i][newJ].index]) {
							buttons[i][newJ].requestFocusInWindow();
							break;
						}
					}
					break;
				case KeyEvent.VK_UP:
					for (int num = 1; num <= Game.BOARD_SIZE; num++) {
						int newI = (i + Game.BOARD_SIZE - num) % Game.BOARD_SIZE;
						if (gui.policy.focusable[gui.boardListeners[newI][j].index]) {
							buttons[newI][j].requestFocusInWindow();
							break;
						}
					}
					break;
				case KeyEvent.VK_RIGHT:
					for (int num = 1; num <= Game.BOARD_SIZE; num++) {
						int newJ = (j + num) % Game.BOARD_SIZE;
						if (gui.policy.focusable[gui.boardListeners[i][newJ].index]) {
							buttons[i][newJ].requestFocusInWindow();
							break;
						}
					}
					break;
				case KeyEvent.VK_DOWN:
					for (int num = 1; num <= Game.BOARD_SIZE; num++) {
						int newI = (i + num) % Game.BOARD_SIZE;
						if (gui.policy.focusable[gui.boardListeners[newI][j].index]) {
							buttons[newI][j].requestFocusInWindow();
							break;
						}
					}
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {
			// System.out.println(gui.game.get(i, j));
			if (!button.hasFocus() && gui.policy.focusable[index])
				button.setBackground(BUTTON_HOVER_COLOR);
			hover = true;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!button.hasFocus())
				button.setBackground(gui.policy.focusable[index] ? BACKGROUND_COLOR : BUTTON_DISABLED_COLOR);
			hover = false;
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		public void setState(int state) {
			if (this.state == state)
				return;
			this.state = state;
			boolean empty = state == BoardState.EMPTY;
			boolean[] focusable = gui.policy.focusable;
			focusable[index] = empty;
			button.setFocusable(empty);
			if (gui.game.getCurrent() == BoardState.HUMAN)
				button.transferFocus();
			button.setBackground(empty ? BACKGROUND_COLOR : BUTTON_DISABLED_COLOR);
			if (empty) {
				button.setText("");
				button.setCursor(ENABLED_CURSOR);
			} else {
				if (state == gui.game.getX())
					button.setText("X");
				else if (state == gui.game.getO())
					button.setText("O");
				button.setCursor(DISABLED_CURSOR);
			}
		}

	}

	private static final long serialVersionUID = 1L;

	public static final boolean LOG = false;

	public static final String TITLE = Game.BOARD_SIZE + "x" + Game.BOARD_SIZE + " Tic-Tac-Toe";

	private static final int BOARD_THICKNESS = 1;
	private static final int BUTTON_SIZE = 50;
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	private static final Color FOREGROUND_COLOR = Color.BLACK;
	private static final Color BUTTON_FOCUS_COLOR = Color.ORANGE;
	private static final Color BUTTON_HOVER_COLOR = Color.YELLOW;

	private static final Color BUTTON_CLICK_COLOR = Color.red;
	private static final Color BUTTON_DISABLED_COLOR = Color.GRAY;

	private static final Cursor DISABLED_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	private static final Cursor ENABLED_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	public static void main(String[] args) {
		UIManager.put("Button.select", BUTTON_CLICK_COLOR);
		new GUI();
	}

	private static Dimension panelSize() {
		int width = Game.BOARD_SIZE * BUTTON_SIZE - 1;
		return new Dimension(width, width);
	}

	private JPanel buttonPanel;

	private final JButton[][] buttons = new JButton[Game.BOARD_SIZE][Game.BOARD_SIZE];
	private final BoardListener[][] boardListeners = new BoardListener[Game.BOARD_SIZE][Game.BOARD_SIZE];

	private JButton restartButton;

	private BoardFocusTraversalPolicy policy;

	private Game game;

	public GUI() {
		initUI();
		runGame();
	}

	private void initBoardButton(JPanel panel, int i, int j) {
		JButton button = mkButton(null, ENABLED_CURSOR);
		BoardListener listener = new BoardListener(this/* , panel */, button, i, j);
		button.addMouseListener(listener);
		button.addFocusListener(listener);
		button.addActionListener(listener);
		button.addKeyListener(listener);
		if (i > 0 || j > 0) {
			Border border = BorderFactory.createMatteBorder(i > 0 ? BOARD_THICKNESS : 0, j > 0 ? BOARD_THICKNESS : 0, 0, 0, FOREGROUND_COLOR);
			button.setBorder(border);
		}
		panel.add(button);
		buttons[i][j] = button;
		boardListeners[i][j] = listener;
	}

	private JPanel initButtonPanel() {
		GridLayout layout = new GridLayout(Game.BOARD_SIZE, Game.BOARD_SIZE);
		buttonPanel = new JPanel(layout);
		buttonPanel.setBackground(BACKGROUND_COLOR);
		for (int i = 0; i < Game.BOARD_SIZE; i++)
			for (int j = 0; j < Game.BOARD_SIZE; j++)
				initBoardButton(buttonPanel, i, j);
		Dimension size = panelSize();
		buttonPanel.setMaximumSize(size);
		buttonPanel.setPreferredSize(size);
		buttonPanel.setMinimumSize(size);
		return buttonPanel;
	}

	// private JButton initRestartButton() {
	// restartButton = mkButton(null, DISABLED_CURSOR);
	// Dimension size = new Dimension(Game.BOARD_SIZE * BUTTON_SIZE, BUTTON_SIZE);
	// restartButton.setPreferredSize(size);
	// restartButton.setMaximumSize(size);
	// restartButton.setMinimumSize(size);
	// restartButton.setAlignmentX(0.5f);
	// return restartButton;
	// }

	private void initUI() {
		Container contentPane = getContentPane();
		BoxLayout layout = new BoxLayout(contentPane, BoxLayout.X_AXIS);
		contentPane.setLayout(layout);

		add(Box.createHorizontalGlue());

		Box yBox = new Box(BoxLayout.Y_AXIS);
		yBox.setAlignmentX(0f);
		yBox.add(Box.createVerticalGlue());
		yBox.add(initButtonPanel());
		yBox.add(Box.createVerticalGlue());
		add(yBox);

		add(Box.createHorizontalGlue());

		contentPane.setBackground(BACKGROUND_COLOR);

		policy = new BoardFocusTraversalPolicy(this);
		setFocusCycleRoot(true);
		setFocusTraversalPolicy(policy);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
	}

	private JButton mkButton(String text, Cursor cursor) {
		JButton button = new JButton(text == null ? "" : text);
		button.setBackground(BACKGROUND_COLOR);
		button.setForeground(FOREGROUND_COLOR);
		button.setBorder(null);
		button.setFocusPainted(false);
		button.setCursor(cursor);
		return button;
	}

	private void runGame() {
		game = new Game(this);
		while (true) {
			setTitle(TITLE + " - " + (game.getCurrent() == game.getX() ? 'X' : 'O'));
			game.start();
			Integer value;
			while ((value = game.evaluateImmediate()) == null)
				game.doTurn();
			if (value < 0)
				JOptionPane.showMessageDialog(this, "You Won!", "Victory!", JOptionPane.PLAIN_MESSAGE);
			else if (value == 0)
				JOptionPane.showMessageDialog(this, "You Tied!", "Tie!", JOptionPane.PLAIN_MESSAGE);
			else
				JOptionPane.showMessageDialog(this, "You Lost!", "Defeat!", JOptionPane.PLAIN_MESSAGE);
			for (BoardListener[] row : boardListeners)
				for (BoardListener listener : row)
					listener.setState(BoardState.EMPTY);
			buttons[0][0].requestFocusInWindow();
		}
	}

	public void setState(int i, int j, int state) {
		boardListeners[i][j].setState(state);
	}
}
