package maxit.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import maxit.util.tuple.Pair;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	static final Color DEEP_SKY_BLUE = new Color(0, 191, 255);

	private static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	private static final Map<String, Pair<Font, Map<Integer, Map<Float, Font>>>> FONT_CACHE = new HashMap<>();
	private static final Font TITLE_FONT = getFont(Font.MONOSPACED, Font.BOLD, 96f);
	private static final Font NORMAL_FONT = getFont(Font.MONOSPACED, Font.PLAIN, 20f);

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

	private final CardLayout layout;

	public GUI() {
		super("MAXIT");
		layout = new CardLayout();
		initUI();
	}

	private void initUI() {
		setLayout(layout);

		JPanel sizeSelect = new JPanel();
		vLayout(sizeSelect);
		sizeSelect.setBackground(Color.WHITE);

		JLabel title = new JLabel("MAXIT");
		title.setFont(TITLE_FONT);
		title.setForeground(DEEP_SKY_BLUE);

		JLabel label = new JLabel("Grid Size:");
		label.setFont(NORMAL_FONT);
		label.setMaximumSize(label.getPreferredSize());

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(5, 1, null, 1));

		spinner.setFont(NORMAL_FONT);
		spinner.setMaximumSize(spinner.getPreferredSize());

		JComponent editor = (JSpinner.NumberEditor) spinner.getEditor();
		JTextField spinnerField = (JTextField) editor.getComponent(0);
		JButton incButton = (JButton) spinner.getComponent(0);
		JButton decButton = (JButton) spinner.getComponent(1);

		spinner.setBorder(null);

		editor.setBorder(null);

		spinnerField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK));
		spinnerField.setColumns(3);

		incButton.setBackground(Color.WHITE);
		incButton.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.BLACK));
		incButton.setCursor(HAND_CURSOR);
		incButton.setFocusPainted(false);

		decButton.setBackground(Color.WHITE);
		decButton.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		decButton.setCursor(HAND_CURSOR);
		decButton.setFocusPainted(false);

		JButton playButton = new JButton("PLAY");
		playButton.setFont(TITLE_FONT);
		playButton.setForeground(DEEP_SKY_BLUE);
		playButton.setBackground(Color.BLACK);
		playButton.setFocusPainted(false);
		playButton.setCursor(HAND_CURSOR);

		// @formatter:off
		setup(
				sizeSelect,
				vGlue(),
				hBox(
						hGlue(),
						title,
						hGlue()
						),
				vGlue(),
				hBox(
						hGlue(),
						label,
						spinner,
						hGlue()
						),
				vGlue(),
				hBox(
						hGlue(),
						playButton,
						hGlue()
						),
				vGlue()
				);
		// @formatter:on

		add(sizeSelect, "sizeSelect");

		GamePanel gamePanel = new GamePanel();
		add(gamePanel, "gamePanel");

		playButton.addActionListener(e -> {
			gamePanel.setup((int) spinner.getValue());
			layout.show(getContentPane(), "gamePanel");
		});

		layout.show(getContentPane(), "sizeSelect");

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
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

	static BoxLayout hLayout(Container parent) {
		return layout(parent, BoxLayout.X_AXIS);
	}

	static BoxLayout vLayout(Container parent) {
		return layout(parent, BoxLayout.Y_AXIS);
	}

	static BoxLayout layout(Container parent, int axis) {
		BoxLayout layout = new BoxLayout(parent, axis);
		if (parent != null)
			parent.setLayout(layout);
		return layout;
	}

	static <T extends Container> T setup(T parent, Component... children) {
		if (parent != null)
			for (Component child : children)
				parent.add(child);
		return parent;
	}

}
