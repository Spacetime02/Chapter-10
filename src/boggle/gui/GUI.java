package boggle.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GUI extends JFrame {

	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	private static final long serialVersionUID = 1L;

	static final Icon SETTINGS_ICON = new Icon() {

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Color bg = c.getBackground();
			Color fg = c.getForeground();
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.translate(x, y);
			g2d.setColor(fg);
			g2d.fillOval(0, 0, 48, 48);
			g2d.setColor(bg);
			g2d.fillOval(12, 12, 24, 24);
			g2d.fillOval(42, 18, 12, 12);
			g2d.fillOval(30, -3, 12, 12);
			g2d.fillOval(6, -3, 12, 12);
			g2d.fillOval(-6, 18, 12, 12);
			g2d.fillOval(30, 39, 12, 12);
			g2d.fillOval(6, 39, 12, 12);
		}

		@Override
		public int getIconWidth() {
			return 48;
		}

		@Override
		public int getIconHeight() {
			return 48;
		}
	};

	static final Font BASE_FONT = new Font("Dialog", 0, 1);
	static final Font TITLE_FONT = BASE_FONT.deriveFont(Font.BOLD, 64f);

	// private JPanel mainMenu;

	// private char[][] grid;
	// private Trie words;

	public GUI() {
		super("Boggle");
		initUI();
	}

	private void initUI() {
		setLayout(new CardLayout());

		// mainMenu = new MainMenu(this);
		add(new MainMenu(this), "mainMenu");
		add(new GamePanel(this), "gamePanel");

		setBackground(Color.WHITE);

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		pack();
		setExtendedState(MAXIMIZED_BOTH);

		setVisible(true);
	}

	void showMainMenu() {
		// TODO
	}

	void showSettingsMenu() {

	}

	void showLevelSelectMenu() {

	}

	void showPlayMenu() {

	}

	static Box.Filler hGlue() {
		return (Box.Filler) Box.createHorizontalGlue();
	}

	static Box.Filler vGlue() {
		return (Box.Filler) Box.createVerticalGlue();
	}

	static Box.Filler glue() {
		return (Box.Filler) Box.createGlue();
	}

	static Box.Filler hStrut(int size) {
		return (Box.Filler) Box.createHorizontalStrut(size);
	}

	static Box.Filler vStrut(int size) {
		return (Box.Filler) Box.createVerticalStrut(size);
	}

	static Box.Filler rigidArea(int width, int height) {
		return rArea(new Dimension(width, height));
	}

	static Box.Filler rArea(Dimension dim) {
		return (Box.Filler) Box.createRigidArea(dim);
	}

	static Box hBox(Component... children) {
		return setup(Box.createHorizontalBox(), children);
	}

	static Box vBox(Component... children) {
		return setup(Box.createVerticalBox(), children);
	}

	static <T extends Container> T setup(T parent, Component... children) {
		for (Component child : children)
			parent.add(child);
		return parent;
	}
}
