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
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String TITLE = TicTacToe.BOARD_SIZE + "x" + TicTacToe.BOARD_SIZE + " Tic-Tac-Toe";

	// private static final int[] BUTTON_INPUT_IDS = new int[] {KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_RIGHT,
	// KeyEvent.VK_DOWN};
	// private static final boolean[] BUTTON_INPUT_ON_RELEASE = new boolean[] {false, false, false, false};

	// private static final int BUTTON_BORDER_THICKNESS = 2;
	private static final int BOARD_THICKNESS = 1;
	private static final int BUTTON_SIZE = 50;

	private static final Color BACKGROUND_COLOR = Color.WHITE;
	private static final Color FOREGROUND_COLOR = Color.BLACK;
	private static final Color BUTTON_FOCUS_COLOR = Color.ORANGE;
	private static final Color BUTTON_HOVER_COLOR = Color.YELLOW;

	private static final Cursor DISABLED_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private static final Cursor ENABLED_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	private JPanel buttonPanel;

	private final JButton[][] buttons = new JButton[TicTacToe.BOARD_SIZE][TicTacToe.BOARD_SIZE];
	private final BoardListener[][] boardListeners = new BoardListener[TicTacToe.BOARD_SIZE][TicTacToe.BOARD_SIZE];

	private JButton restartButton;
	// private RestartListener restartListener;

	private BoardFocusTraversalPolicy policy;

	public static void main(String[] args) {
		UIManager.put("Button.select", Color.RED);
		new GUI();
	}

	public GUI() {
		super(TITLE);
		initUI();
	}

	private void initUI() {
		Container contentPane = getContentPane();
		BoxLayout layout = new BoxLayout(contentPane, BoxLayout.X_AXIS);
		contentPane.setLayout(layout);

		add(Box.createHorizontalGlue());

		Box yBox = new Box(BoxLayout.Y_AXIS);
		yBox.add(Box.createVerticalGlue());
		yBox.add(initButtonPanel());
		yBox.add(Box.createVerticalStrut(BUTTON_SIZE));
		yBox.add(initRestartButton());
		yBox.add(Box.createVerticalGlue());
		add(yBox);

		add(Box.createHorizontalGlue());

		contentPane.setBackground(BACKGROUND_COLOR);

		policy = new BoardFocusTraversalPolicy(this);
		setFocusCycleRoot(true);
		setFocusTraversalPolicy(policy);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private JPanel initButtonPanel() {
		GridLayout layout = new GridLayout(TicTacToe.BOARD_SIZE, TicTacToe.BOARD_SIZE);
		buttonPanel = new JPanel(layout);
		buttonPanel.setBackground(BACKGROUND_COLOR);
		for (int i = 0; i < TicTacToe.BOARD_SIZE; i++)
			for (int j = 0; j < TicTacToe.BOARD_SIZE; j++)
				initBoardButton(buttonPanel, i, j);
		int width = getMaxWidth();
		Dimension size = new Dimension(width, width);
		buttonPanel.setMaximumSize(size);
		buttonPanel.setPreferredSize(size);
		buttonPanel.setMinimumSize(size);
		return buttonPanel;
	}

	private void initBoardButton(JPanel panel, int i, int j) {
		JButton button = mkButton(null, ENABLED_CURSOR);
		BoardListener listener = new BoardListener(this, panel, button, i, j);
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

	private JButton initRestartButton() {
		int width = getMaxWidth();
		restartButton = mkButton("Restart", DISABLED_CURSOR);
		return restartButton;
	}

	private static int getMaxWidth() {
		return TicTacToe.BOARD_SIZE * BUTTON_SIZE;// + 2 * (TicTacToe.BOARD_SIZE - 1) * BOARD_THICKNESS;
	}

	private JButton mkButton(String text, Cursor cursor) {
		JButton button = new JButton(text == null ? text : "");
		button.setBackground(BACKGROUND_COLOR);
		button.setForeground(FOREGROUND_COLOR);
		button.setBorder(null);
		button.setCursor(cursor);
		return button;
	}

	private static final class BoardFocusTraversalPolicy extends FocusTraversalPolicy {

		private final GUI gui;

		private final int len;
		private final JButton[] buttons;
		private final boolean[] focusable;

		public BoardFocusTraversalPolicy(GUI gui) {
			this.gui = gui;
			len = TicTacToe.CELL_COUNT + 1;
			buttons = new JButton[len];
			focusable = new boolean[len];
			int i = 0;
			for (JButton[] row : gui.buttons)
				for (JButton button : row) {
					this.buttons[i] = button;
					focusable[i++] = true;
				}
			this.buttons[TicTacToe.CELL_COUNT] = gui.restartButton;
		}

		/**
		 * {@inheritDoc}
		 */
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

		/**
		 * {@inheritDoc}
		 */
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Component getFirstComponent(Container aContainer) throws IllegalArgumentException {
			if (aContainer == null)
				throw new IllegalArgumentException();
			for (int i = 0; i < len; i++)
				if (focusable[i])
					return buttons[i];
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Component getLastComponent(Container aContainer) throws IllegalArgumentException {
			if (aContainer == null)
				throw new IllegalArgumentException();
			for (int i = len - 1; i >= 0; i--)
				if (focusable[i])
					return buttons[i];
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Component getDefaultComponent(Container aContainer) throws IllegalArgumentException {
			return getFirstComponent(aContainer);
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

		private int indexOf(JButton button) {
			for (int i = 0; i < len; i++)
				if (buttons[i] == button)
					return i;
			return -1;
		}

	}

	private static final class BoardListener implements ActionListener, MouseListener, FocusListener, KeyListener {

		private final int i, j;

		private final JButton button;
		private final JButton[][] buttons;
		private final GUI gui;
		private final JPanel panel;

		private boolean hover = false;

		public BoardListener(GUI gui, JPanel panel, JButton button, int i, int j) {
			this.gui = gui;
			this.panel = panel;
			this.buttons = gui.buttons;
			this.button = button;
			this.i = i;
			this.j = j;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (!button.hasFocus())
				button.setBackground(BUTTON_HOVER_COLOR);
			hover = true;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!button.hasFocus())
				button.setBackground(BACKGROUND_COLOR);
			hover = false;
		}

		@Override
		public void focusGained(FocusEvent e) {
			gui.getRootPane().setDefaultButton(button);
			button.setBackground(BUTTON_FOCUS_COLOR);
		}

		@Override
		public void focusLost(FocusEvent e) {
			button.setBackground(hover ? BUTTON_HOVER_COLOR : BACKGROUND_COLOR);
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			switch (code) {
				case KeyEvent.VK_LEFT:
					buttons[i][(j + TicTacToe.BOARD_SIZE - 1) % TicTacToe.BOARD_SIZE].requestFocusInWindow();
					break;
				case KeyEvent.VK_UP:
					buttons[(i + TicTacToe.BOARD_SIZE - 1) % TicTacToe.BOARD_SIZE][j].requestFocusInWindow();
					break;
				case KeyEvent.VK_RIGHT:
					buttons[i][(j + 1) % TicTacToe.BOARD_SIZE].requestFocusInWindow();
					break;
				case KeyEvent.VK_DOWN:
					buttons[(i + 1) % TicTacToe.BOARD_SIZE][j].requestFocusInWindow();
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}

	}
}
