package othello.gui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

import othello.util.tuple.Pair;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int SPINNER_COLUMNS = 7;
	private static final int FIELD_COLUMNS   = 15;

	static final int SCROLLBAR_SIZE = UIManager.getInt("ScrollBar.width");

	private static final String[] NAMES = loadNames();

	private static final Random NAME_RANDY = new Random();

	private static final Font TITLE_FONT  = Fonts.get(Font.MONOSPACED, Font.BOLD, 120f);
	private static final Font BUTTON_FONT = Fonts.get(Font.MONOSPACED, Font.BOLD, 72f);
	private static final Font NORMAL_FONT = Fonts.get(Font.MONOSPACED, Font.PLAIN, 20f);

	private static String[] loadNames() {
		try (Scanner nameScanner = new Scanner(new File("names.txt"))) {
			List<String> nameList = new ArrayList<>();
			while (nameScanner.hasNext())
				nameList.add(nameScanner.next());
			return nameList.toArray(new String[nameList.size()]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new String[] { "ERR_NAME_LOAD_FAILED" };
		}
	}

	private static String getRandomName() {
		return NAMES[NAME_RANDY.nextInt(NAMES.length)];
	}

	private final CardLayout layout;

	public GUI() {
		super("Othello");
		layout = new CardLayout();
		initUI();
	}

	private void initUI() {
		setLayout(layout);

		setBackground(Colors.BACKGROUND_0);

		JPanel sizeSelect = new JPanel();
		GUIUtils.vLayout(sizeSelect);
		sizeSelect.setBackground(Colors.BACKGROUND_0);

		JLabel title = new JLabel("Othello");
		title.setFont(TITLE_FONT);
		title.setForeground(Colors.ON_BACKGROUND);

		String n1 = getRandomName();
		String n2 = getRandomName();

		Pair<JLabel, JSpinner>          sizePair        = mkSpinner("Grid Size", 4, null, Defaults.GRID_SIZE, 2);
		Pair<JLabel, JComboBox<String>> blackTypePair   = mkComboBox("Black Player Type", "Human", "Human", "Computer");
		Pair<JLabel, JTextField>        blackNamePair   = mkField("Black Player Name", n1);
		Pair<JLabel, JComboBox<String>> whiteTypePair   = mkComboBox("White Player Type", "Computer", "Human", "Computer");
		Pair<JLabel, JTextField>        whiteNamePair   = mkField("White Player Name", n2);
		Pair<JLabel, JSpinner>          cacheDepthPair  = mkSpinner("Maximum Cache Depth", 0, null, Defaults.CACHE_DEPTH, 1);
		Pair<JLabel, JSpinner>          searchDepthPair = mkSpinner("Maximum Search Depth", 1, null, Defaults.SEARCH_DEPTH, 1);

		JSpinner          size        = sizePair.second;
		JComboBox<String> blackType   = blackTypePair.second;
		JTextField        blackName   = blackNamePair.second;
		JComboBox<String> whiteType   = whiteTypePair.second;
		JTextField        whiteName   = whiteNamePair.second;
		JSpinner          cacheDepth  = cacheDepthPair.second;
		JSpinner          searchDepth = searchDepthPair.second;

		size.addChangeListener(e -> {
			int value = (int) size.getValue();
			if (value % 2 != 0)
				size.setValue(value - 1);
		});

		JButton playButton = new JButton("PLAY") {

			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				Dimension sup = super.getPreferredSize();
				sup.width += 48;
				return sup;
			}

		};
		playButton.setFont(BUTTON_FONT);
		playButton.setForeground(Colors.ON_BACKGROUND);
		playButton.setBackground(Colors.BACKGROUND_1);
		playButton.setBorder(null);
		playButton.setFocusPainted(false);
		playButton.setCursor(Cursors.HAND);

		GUIUtils.vSpace(sizeSelect,
				GUIUtils.hCenter(title),
				mkInput(sizePair, blackTypePair, blackNamePair, whiteTypePair, whiteNamePair, cacheDepthPair, searchDepthPair),
				GUIUtils.hCenter(playButton));

		add(sizeSelect, "sizeSelect");

		getRootPane().setDefaultButton(playButton);

		GamePanel gamePanel = new GamePanel();
		add(gamePanel, "gamePanel");

		playButton.addActionListener(e -> gamePanel.setup((int) size.getValue(), blackType.getSelectedIndex() == 0, blackName.getText(), whiteType.getSelectedIndex() == 0, whiteName.getText(), (int) cacheDepth.getValue(), (int) searchDepth.getValue(), this));

		layout.show(getContentPane(), "sizeSelect");

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
	}

	void showGamePanel() {
		layout.show(getContentPane(), "gamePanel");
	}

	private static Pair<JLabel, JSpinner> mkSpinner(String name, Integer minimum, Integer maximum, Integer initial, Integer step) {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(initial, minimum, maximum, step));

		setupComponent(spinner);

		JComponent editor = (JSpinner.NumberEditor) spinner.getEditor();

		spinner.setBorder(null);

		editor.setBorder(null);

		JTextField spinnerField = (JTextField) editor.getComponent(0);
		spinnerField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Colors.ON_BACKGROUND));
		spinnerField.setColumns(SPINNER_COLUMNS);
		spinnerField.setCaretColor(Colors.ON_BACKGROUND);
		setupComponent(spinnerField);

		setupButton((JButton) spinner.getComponent(0), 1, 1, 0, 1); // Increment
		setupButton((JButton) spinner.getComponent(1), 1, 1, 1, 1); // Decrement

		return new Pair<>(mkLabel(name), spinner);
	}

	private static Pair<JLabel, JTextField> mkField(String name, String text) {
		JTextField field = new JTextField(text, FIELD_COLUMNS);

		field.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Colors.ON_BACKGROUND));
		setupComponent(field);
		field.setCaretColor(Colors.ON_BACKGROUND);

		return new Pair<>(mkLabel(name), field);
	}

	private static Pair<JLabel, JComboBox<String>> mkComboBox(String name, String initial, String... items) {
		JComboBox<String> comboBox = new JComboBox<>(items);

		comboBox.setSelectedItem(initial);

		comboBox.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Colors.ON_BACKGROUND));
		setupComponent(comboBox);

		String prototype = "";
		for (int i = 0; i < FIELD_COLUMNS; i++)
			prototype += "#";
		comboBox.setPrototypeDisplayValue(prototype);

		return new Pair<>(mkLabel(name), comboBox);
	}

	private static JLabel mkLabel(String name) {
		JLabel label = new JLabel(name + ": ");
		label.setFont(NORMAL_FONT);
		label.setForeground(Colors.ON_BACKGROUND);
		return label;
	}

	private static void setupButton(JButton button, int topBorderThickness, int leftBorderThickness, int bottomBorderThickness, int rightBorderThickness) {
		setupComponent(button);
		button.setBorder(BorderFactory.createMatteBorder(topBorderThickness, leftBorderThickness, bottomBorderThickness, rightBorderThickness, Colors.ON_BACKGROUND));
		button.setCursor(Cursors.HAND);
		button.setFocusPainted(false);
	}

	private static void setupComponent(JComponent comp) {
		comp.setBackground(Colors.BACKGROUND_1);
		comp.setForeground(Colors.ON_BACKGROUND);
		comp.setFont(NORMAL_FONT);
	}

	@SafeVarargs
	private static Box mkInput(Pair<JLabel, ? extends Component>... pairs) {
		Box col1 = GUIUtils.vBox(GUIUtils.vGlue());
		Box col2 = GUIUtils.vBox(GUIUtils.vGlue());
		for (Pair<JLabel, ? extends Component> pair : pairs) {
			// Align Rows
			pair.first.setPreferredSize(new Dimension(pair.first.getPreferredSize().width, pair.second.getPreferredSize().height));
			pair.second.setMaximumSize(pair.second.getPreferredSize());

			col1.add(GUIUtils.hBox(GUIUtils.hGlue(), pair.first));
			col2.add(GUIUtils.hBox(pair.second, GUIUtils.hGlue()));
		}
		col1.add(GUIUtils.vGlue());
		col2.add(GUIUtils.vGlue());
		return GUIUtils.hBox(col1, col2);
	}

}
