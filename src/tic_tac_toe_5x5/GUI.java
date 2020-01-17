package tic_tac_toe_5x5;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String TITLE = TicTacToe.BOARD_SIZE + "x" + TicTacToe.BOARD_SIZE + " Tic-Tac-Toe";

	// private static final int BUTTON_BORDER_THICKNESS = 2;
	private static final int BOARD_HALF_THICKNESS = 3;
	private static final int BUTTON_SIZE = 50;

	private static final Color BACKGROUND_COLOR = Color.WHITE;
	private static final Color FOREGROUND_COLOR = Color.BLACK;
	
	private static final Cursor BUTTON_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	private final JButton[][] board = new JButton[TicTacToe.BOARD_SIZE][TicTacToe.BOARD_SIZE];

	public static void main(String[] args) {
		new GUI();
	}

	public GUI() {
		super(TITLE);
		initUI();
	}

	private void initUI() {
		Container contentPane = getContentPane();
		BoxLayout layout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
		contentPane.setLayout(layout);

		add(Box.createVerticalGlue());

		Box xBox = new Box(BoxLayout.X_AXIS);
		xBox.add(Box.createHorizontalGlue());
		xBox.add(initButtonPanel());
		xBox.add(Box.createHorizontalGlue());
		add(xBox);

		add(Box.createVerticalGlue());

		contentPane.setBackground(BACKGROUND_COLOR);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private JPanel initButtonPanel() {
		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);
		panel.setBackground(FOREGROUND_COLOR);
		for (int i = 0; i < TicTacToe.BOARD_SIZE; i++)
			for (int j = 0; j < TicTacToe.BOARD_SIZE; j++)
				mkBoardButton(panel, i, j);
		int dim;
		if (TicTacToe.BOARD_SIZE > 1)
			dim = TicTacToe.BOARD_SIZE * BUTTON_SIZE + 2 * (TicTacToe.BOARD_SIZE - 1) * BOARD_HALF_THICKNESS;
		else
			dim = BUTTON_SIZE;
		panel.setMaximumSize(new Dimension(dim, dim));
		return panel;
	}

	private void mkBoardButton(JPanel panel, int i, int j) {
		BoardListener listener = new BoardListener(i, j);
		JButton button = mkButton(null, listener);
		listener.init(button);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = j;
		constraints.gridy = i;
		if (TicTacToe.BOARD_SIZE > 1) {
			// @formatter:off
			if (i != 0)                        constraints.insets.top    = BOARD_HALF_THICKNESS;
			if (i != TicTacToe.BOARD_SIZE - 1) constraints.insets.bottom = BOARD_HALF_THICKNESS;
			if (j != 0)                        constraints.insets.left   = BOARD_HALF_THICKNESS;
			if (j != TicTacToe.BOARD_SIZE - 1) constraints.insets.right  = BOARD_HALF_THICKNESS;
			// @formatter:on
		}
		panel.add(button, constraints);
	}

	private static final class BoardListener extends MouseAdapter {

		private final int i, j;

		private JButton button = null;
		
		private JLabel x, o;

		public BoardListener(int i, int j) {
			this.i = i;
			this.j = j;
			x = new JLabel("X", JLabel.CENTER);
			x.setForeground(Color.BLACK);
			x.setBackground(Color.YELLOW);
			o = new JLabel("O");
		}

		public void init(JButton button) {
			if (button == null)
				throw new NullPointerException();
			else if (this.button == null)
				this.button = button;
			else
				throw new IllegalStateException();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			button.add(x);
			System.out.println(x);
//			button.setEnabled(false);
		}

	}

	private JButton mkButton(String text, MouseListener ml) {
		JButton button = new JButton(text == null ? text : "");
		button.setBackground(BACKGROUND_COLOR);
		button.setForeground(FOREGROUND_COLOR);
		button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
		if (ml != null)
			button.addMouseListener(ml);
		button.setBorder(null);
		button.setCursor(BUTTON_CURSOR);
		button.setLayout(new FlowLayout());
		// button.setBorder(BorderFactory.createLineBorder(BUTTON_BORDER_COLOR, BUTTON_BORDER_THICKNESS,
		// BUTTON_BORDER_ROUNDED));
		return button;
	}
}
