package othello.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import othello.util.tuple.Pair;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int SIDE_SPACING = 10;

	private static final Font NAME_FONT  = Fonts.get(Font.MONOSPACED, Font.BOLD, 20);
	private static final Font SCORE_FONT = Fonts.get(Font.MONOSPACED, Font.BOLD | Font.ITALIC, 16);
	private static final Font SKIP_FONT  = Fonts.get(Font.MONOSPACED, Font.BOLD, 32);

	private static JLabel blackScoreLabel;
	private static JLabel whiteScoreLabel;

	private JPanel gridPanel;

	public GamePanel() {
		super(new BorderLayout(), true);
		setBackground(Colors.BACKGROUND_0);
	}

	void setup(int gridSize, boolean blackHuman, String blackName, boolean whiteHuman, String whiteName, int cacheDepth, int searchDepth, GUI gui) {
		JPanel parentPanel = new JPanel(new BorderLayout());
		parentPanel.setBackground(Colors.BACKGROUND_0);

		JPanel gridPositioner = new JPanel(new GridBagLayout());
		gridPositioner.setBackground(Colors.BACKGROUND_0);

		JScrollPane scrollPane = new JScrollPane(gridPositioner);
		scrollPane.setBackground(Colors.NEUTRAL);

		gridPanel = new GridPanel(gridSize, blackHuman, blackName, whiteHuman, whiteName, cacheDepth, searchDepth, scrollPane, blackScore -> EventQueue.invokeLater(() -> blackScoreLabel.setText(Integer.toString(blackScore))), score2 -> EventQueue.invokeLater(() -> whiteScoreLabel.setText(Integer.toString(score2))));

		Pair<JLabel, Box> scoreBox1 = mkScoreBox(blackName, Color.BLUE);
		Pair<JLabel, Box> scoreBox2 = mkScoreBox(whiteName, Color.RED);

		blackScoreLabel = scoreBox1.first;
		whiteScoreLabel = scoreBox2.first;

		add(parentPanel);
		parentPanel.add(scrollPane, BorderLayout.CENTER);
		parentPanel.add(scoreBox1.second, BorderLayout.WEST);
		parentPanel.add(scoreBox2.second, BorderLayout.EAST);
		gridPositioner.add(gridPanel);

		gui.showGamePanel();
	}

	private Pair<JLabel, Box> mkScoreBox(String name, Color color) {
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
				sup.width += 48;
				sup.height += 48;
				return sup;
			}

		};
		skipButton.setBackground(Colors.NEUTRAL);
		skipButton.setCursor(Cursors.DEFAULT);
		skipButton.setBorder(BorderFactory.createMatteBorder(GridPanel.BORDER_THICKNESS, GridPanel.BORDER_THICKNESS, GridPanel.BORDER_THICKNESS, GridPanel.BORDER_THICKNESS, Color.GREEN));
		skipButton.setFont(SKIP_FONT);
		skipButton.setForeground(color);

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
		return new Pair<>(scoreLabel, box);
	}

}
