package boggle.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import boggle.io.Loader;
import boggle.util.tuple.Pair;

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

	// static final Icon SETTINGS_ICON = new Icon() {
	//
	// @Override
	// public void paintIcon(Component c, Graphics g, int x, int y) {
	// Color bg = c.getBackground();
	// Color fg = c.getForeground();
	// Graphics2D g2d = (Graphics2D) g;
	// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	// g2d.translate(x, y);
	// g2d.setColor(fg);
	// g2d.fillOval(0, 0, 48, 48);
	// g2d.setColor(bg);
	// g2d.fillOval(12, 12, 24, 24);
	// g2d.fillOval(42, 18, 12, 12);
	// g2d.fillOval(30, -3, 12, 12);
	// g2d.fillOval(6, -3, 12, 12);
	// g2d.fillOval(-6, 18, 12, 12);
	// g2d.fillOval(30, 39, 12, 12);
	// g2d.fillOval(6, 39, 12, 12);
	// }
	//
	// @Override
	// public int getIconWidth() {
	// return 48;
	// }
	//
	// @Override
	// public int getIconHeight() {
	// return 48;
	// }
	// };

	static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	static final Color SKY_BLUE = new Color(135, 206, 235);
	private static final Map<String, Pair<Font, Map<Integer, Map<Float, Font>>>> FONT_CACHE = new HashMap<>();
	static final Font TITLE_FONT = getFont(Font.DIALOG, Font.PLAIN, 64f);

	static Font getFont(String name, int style, float size) {
		Pair<Font, Map<Integer, Map<Float, Font>>> basedFont = FONT_CACHE.get(name);
		if (basedFont == null)
			FONT_CACHE.put(name, basedFont = new Pair<>(new Font(name, 0, 1), new HashMap<>()));
		Font base = basedFont.first;
		Map<Integer, Map<Float, Font>> namedFont = basedFont.second;
		Map<Float, Font> styledFont = namedFont.get(style);
		if (styledFont == null)
			namedFont.put(style, styledFont = new HashMap<>());
		Font font = styledFont.get(size);
		if (font == null)
			styledFont.put(size, font = base.deriveFont(style, size));
		return font;
	}

	// private MainMenu mainMenu;
	private GamePanel gamePanel;

	// private JPanel mainMenu;

	// private char[][] grid;
	// private OldTrie words;

	public GUI() {
		super("Boggle");
		initUI();
	}

	private void initUI() {
		setLayout(new CardLayout());

		// mainMenu = new MainMenu(this);
		add(new MainMenu(this), "mainMenu");
		add((gamePanel = new GamePanel(this)), "gamePanel");

		setBackground(Color.WHITE);

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		pack();
		setExtendedState(MAXIMIZED_BOTH);

		setVisible(true);
	}

	// void showMainMenu() {}

	void showGamePanel() {
		JFileChooser chooser;
		try {
			File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			File parent = file.getParentFile();
			File[] children = parent == null ? null : parent.listFiles();
			File boggleDir = null;
			if (children != null)
				for (int i = 0; i < children.length; i++) {
					File child = children[i];
					if (child.isDirectory() && child.getName().toLowerCase().equals("boggle levels")) {
						boggleDir = child;
						break;
					}
				}
			chooser = new JFileChooser(boggleDir == null ? parent == null ? file : parent : boggleDir);
		}
		catch (URISyntaxException e) {
			chooser = new JFileChooser();
		}
		FileFilter filter = new FileNameExtensionFilter("Boggle files (*.bgl)", "bgl");
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		try {
			File file = chooser.getSelectedFile();
			Loader loader = new Loader(file);
			loader.load();
			gamePanel.setup(loader.getGrid(), loader.getHeight(), loader.getWidth(), loader.getWords(), file.getName());
			// Boggle boggle = new Boggle(loader.getGrid(), loader.getWords());
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Unable to open file. Please try again.", "Error opening file", JOptionPane.ERROR_MESSAGE);
			return;
		}
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
