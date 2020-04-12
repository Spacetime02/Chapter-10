package othello.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.IntConsumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import maxit.util.function.BooleanConsumer;
import othello.util.tuple.Pair;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int SIDE_SPACING = 10;

	private static final Font NAME_FONT  = Fonts.get(Font.MONOSPACED, Font.BOLD, 20);
	private static final Font SCORE_FONT = Fonts.get(Font.MONOSPACED, Font.BOLD | Font.ITALIC, 16);
	private static final Font SKIP_FONT  = Fonts.get(Font.MONOSPACED, Font.BOLD, 32);

	private static JLabel blackScoreLabel;
	private static JLabel whiteScoreLabel;

	private GridPanel gridPanel;

	public GamePanel() {
		super(new BorderLayout(), true);
		setBackground(Colors.BACKGROUND_0);
	}

	void setup(int gridSize, int blackType, String blackName, int whiteType, String whiteName, int cacheDepth, int searchDepth, GUI gui) {
		JPanel parentPanel = new JPanel(new BorderLayout());
		parentPanel.setBackground(Colors.BACKGROUND_0);

		JPanel gridPositioner = new JPanel(new GridBagLayout());
		gridPositioner.setBackground(Colors.BACKGROUND_0);

		JScrollPane scrollPane = new JScrollPane(gridPositioner);
		scrollPane.setBackground(Colors.NEUTRAL);

		Pair<Pair<Pair<JLabel, JButton>, Box>, BooleanConsumer> blackScoreBox = mkScoreBox(blackName, Color.BLUE);
		Pair<Pair<Pair<JLabel, JButton>, Box>, BooleanConsumer> whiteScoreBox = mkScoreBox(whiteName, Color.RED);

		IntConsumer blackScoreCallback = blackScore -> EventQueue.invokeLater(() -> blackScoreLabel.setText(Integer.toString(blackScore)));
		IntConsumer whiteScoreCallback = whiteScore -> EventQueue.invokeLater(() -> whiteScoreLabel.setText(Integer.toString(whiteScore)));

		gridPanel = new GridPanel(gridSize, blackType, blackName, whiteType, whiteName, cacheDepth, searchDepth, scrollPane, blackScoreBox.second, whiteScoreBox.second, blackScoreCallback, whiteScoreCallback);

		blackScoreBox.first.first.second.addActionListener(e -> gridPanel.forfeit(true));
		whiteScoreBox.first.first.second.addActionListener(e -> gridPanel.forfeit(false));

		blackScoreLabel = blackScoreBox.first.first.first;
		whiteScoreLabel = whiteScoreBox.first.first.first;

		add(parentPanel);
		parentPanel.add(scrollPane, BorderLayout.CENTER);
		parentPanel.add(blackScoreBox.first.second, BorderLayout.WEST);
		parentPanel.add(whiteScoreBox.first.second, BorderLayout.EAST);
		gridPositioner.add(gridPanel);

		gui.showGamePanel();
	}

	private static Pair<Pair<Pair<JLabel, JButton>, Box>, BooleanConsumer> mkScoreBox(String name, Color color) {
		JLabel nameLabel  = new JLabel(name);
		JLabel scoreLabel = new JLabel("0");

		nameLabel.setForeground(color);
		scoreLabel.setForeground(color);

		nameLabel.setFont(NAME_FONT);
		scoreLabel.setFont(SCORE_FONT);

		JButton skipButton = new JButton("SKIP") {

			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				Dimension sup = super.getPreferredSize();
				sup.width += 48 + GridPanel.BORDER_THICKNESS;
				sup.height += 48 + GridPanel.BORDER_THICKNESS;
				return sup;
			}

		};
		skipButton.setBackground(Colors.NEUTRAL);
		skipButton.setCursor(Cursors.DEFAULT);
		skipButton.setBorder(BorderFactory.createMatteBorder(GridPanel.BORDER_THICKNESS, GridPanel.BORDER_THICKNESS, GridPanel.BORDER_THICKNESS, GridPanel.BORDER_THICKNESS, Color.GREEN));
		skipButton.setFont(SKIP_FONT);
		skipButton.setForeground(color);
		skipButton.setFocusPainted(false);
		skipButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				UIManager.put("Button.select", skipButton.getBackground());
			}

		});

		Box box = GUIUtils.hBox(
				GUIUtils.hStrut(SIDE_SPACING),
				GUIUtils.vBox(
						GUIUtils.vStrut(SIDE_SPACING),
						GUIUtils.hCenter(nameLabel),
						GUIUtils.vStrut(SIDE_SPACING),
						GUIUtils.hCenter(scoreLabel),
						GUIUtils.vGlue(),
						GUIUtils.hCenter(skipButton),
						GUIUtils.vStrut(SIDE_SPACING)),
				GUIUtils.hStrut(SIDE_SPACING));

		BooleanConsumer forfeitCallback = enabled -> {
			skipButton.setBackground(enabled ? color : Colors.NEUTRAL);
			skipButton.setForeground(enabled ? Color.BLACK : color);
			skipButton.setCursor(enabled ? Cursors.HAND : Cursors.DEFAULT);
		};

		return new Pair<>(new Pair<>(new Pair<>(scoreLabel, skipButton), box), forfeitCallback);
	}

}
