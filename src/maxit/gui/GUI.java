package maxit.gui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import maxit.util.tuple.Pair;

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
		super("MAXIT");
		layout = new CardLayout();
		initUI();
	}

	private void initUI() {
		setLayout(layout);

		setBackground(Colors.BACKGROUND_0);

		JPanel sizeSelect = new JPanel();
		GUIUtils.vLayout(sizeSelect);
		sizeSelect.setBackground(Colors.BACKGROUND_0);

		JLabel title = new JLabel("MAXIT");
		title.setFont(TITLE_FONT);
		title.setForeground(Colors.ON_BACKGROUND);

		String n1 = getRandomName();
		String n2 = getRandomName();

		Pair<JLabel, JSpinner>          sizePair        = mkSpinner("Grid Size", 1, null, Defaults.GRID_SIZE, 1);
		Pair<JLabel, JSpinner>          minPair         = mkSpinner("Minimum Value", null, 0, Defaults.MIN_VALUE, 1);
		Pair<JLabel, JSpinner>          maxPair         = mkSpinner("Maximum Value", 0, null, Defaults.MAX_VALUE, 1);
		Pair<JLabel, JComboBox<String>> type1Pair       = mkComboBox("Player 1 Type", "Human", "Human", "Computer");
		Pair<JLabel, JTextField>        name1Pair       = mkField("Player 1 Name", n1);
		Pair<JLabel, JComboBox<String>> type2Pair       = mkComboBox("Player 2 Type", "Computer", "Human", "Computer");
		Pair<JLabel, JTextField>        name2Pair       = mkField("Player 2 Name", n2);
		Pair<JLabel, JComboBox<String>> horizontalPair  = mkComboBox("Horizontal Player", n1, n1, n2);
		Pair<JLabel, JSpinner>          cacheDepthPair  = mkSpinner("Maximum Cache Depth", 0, null, Defaults.CACHE_DEPTH, 1);
		Pair<JLabel, JSpinner>          searchDepthPair = mkSpinner("Maximum Search Depth", 1, null, Defaults.SEARCH_DEPTH, 1);

		JSpinner          size        = sizePair.second;
		JSpinner          min         = minPair.second;
		JSpinner          max         = maxPair.second;
		JComboBox<String> type1       = type1Pair.second;
		JTextField        name1       = name1Pair.second;
		JComboBox<String> type2       = type2Pair.second;
		JTextField        name2       = name2Pair.second;
		JComboBox<String> horizontal  = horizontalPair.second;
		JSpinner          cacheDepth  = cacheDepthPair.second;
		JSpinner          searchDepth = searchDepthPair.second;

		SpinnerNumberModel minModel = (SpinnerNumberModel) min.getModel();
		SpinnerNumberModel maxModel = (SpinnerNumberModel) max.getModel();

		min.addChangeListener(e -> maxModel.setMinimum((Integer) minModel.getNumber()));
		max.addChangeListener(e -> minModel.setMaximum((Integer) maxModel.getNumber()));

		min.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				minModel.setValue(Math.min((Integer) minModel.getNumber(), (Integer) maxModel.getNumber()));
			}

		});
		max.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				maxModel.setValue(Math.max((Integer) minModel.getNumber(), (Integer) maxModel.getNumber()));
			}

		});

		class NameListener implements DocumentListener {

			private int index;

			private JTextField field;

			public NameListener(int index, JTextField field) {
				this.index = index;
				this.field = field;
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			private void update() {
				int selIndex = horizontal.getSelectedIndex();
				horizontal.removeItemAt(index);
				horizontal.insertItemAt(field.getText(), index);
				horizontal.setSelectedIndex(selIndex);
			}

		}

		name1.getDocument().addDocumentListener(new NameListener(0, name1));
		name2.getDocument().addDocumentListener(new NameListener(1, name2));

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
				mkInput(sizePair, minPair, maxPair, type1Pair, name1Pair, type2Pair, name2Pair, horizontalPair, cacheDepthPair, searchDepthPair),
				GUIUtils.hCenter(playButton));

		add(sizeSelect, "sizeSelect");

		getRootPane().setDefaultButton(playButton);

		GamePanel gamePanel = new GamePanel();
		add(gamePanel, "gamePanel");

		playButton.addActionListener(e -> {
			if (min.hasFocus())
				minModel.setValue(Math.min((Integer) minModel.getNumber(), (Integer) maxModel.getNumber()));
			else if (max.hasFocus())
				maxModel.setValue(Math.max((Integer) minModel.getNumber(), (Integer) maxModel.getNumber()));
			gamePanel.setup((int) size.getValue(), (int) min.getValue(), (int) max.getValue(), type1.getSelectedIndex() == 0, name1.getText(), type2.getSelectedIndex() == 0, name2.getText(), horizontal.getSelectedIndex() == 0, (int) cacheDepth.getValue(), (int) searchDepth.getValue(), this);
		});

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
		spinner.setBorder(null);

		setupComponent(spinner);

		JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
		editor.setBorder(null);
		editor.getFormat().setGroupingUsed(false);

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
//		field.setMaximumSize(field.getPreferredSize());
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
