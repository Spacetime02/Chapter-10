package maxit.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import boggle.util.tuple.Pair;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Map<String, Pair<Font, Map<Integer, Map<Float, Font>>>> FONT_CACHE = new HashMap<>();
	private static final Font TITLE_FONT = getFont(Font.MONOSPACED, Font.BOLD, 64f);

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

		JLabel label = new JLabel("Grid size:");
		label.setFont(getFont(Font.MONOSPACED, Font.PLAIN, label.getFont().getSize2D()));
		label.setMaximumSize(label.getPreferredSize());

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(5, 1, null, 1));
		JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
		editor.get.setBorder(null);
		((JSpinner.NumberEditor) spinner.getEditor()).getTextField().setColumns(3);

		spinner.setFont(getFont(Font.MONOSPACED, Font.PLAIN, spinner.getFont().getSize2D()));
		spinner.setMaximumSize(spinner.getPreferredSize());

		// @formatter:off
		setup(
				sizeSelect,
				vGlue(),
				hBox(
						hGlue(),
						title,
						hGlue()
						),
//				vGlue(),
				hBox(
						hGlue(),
						label,
						spinner,
						hGlue()
						),
				vGlue()
				);
		// @formatter:on

		add(sizeSelect, "sizeSelect");
		layout.show(getContentPane(), "sizeSelect");

		pack();
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
