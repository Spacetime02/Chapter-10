package maxit.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import maxit.util.tuple.Pair;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int SCORE_BOX_SPACING = 10;

	private static final Font NAME_FONT = Fonts.get(Font.MONOSPACED, Font.BOLD, 20);

	private static JLabel scoreLabel1;
	private static JLabel scoreLabel2;

	private JPanel gridPanel;

	public GamePanel() {
		super(new BorderLayout(), true);
		setBackground(Colors.BACKGROUND_0);
	}

	void setup(int gridSize, int minValue, int maxValue, boolean human1, String name1, boolean human2, String name2, boolean horizontal1, int searchDepth, int cacheDepth, GUI gui) {
		if (minValue > maxValue)
			throw new IllegalArgumentException("minValue (" + minValue + ") > maxValue (" + maxValue + ").");

		JPanel parentPanel = new JPanel(new BorderLayout());
		parentPanel.setBackground(Colors.BACKGROUND_0);

		JPanel gridPositioner = new JPanel(new GridBagLayout());
		gridPositioner.setBackground(Colors.BACKGROUND_0);

		JScrollPane scrollPane = new JScrollPane(gridPositioner);
		scrollPane.setBackground(Colors.BACKGROUND_0);
//		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI());

		gridPanel = new GridPanel(gridSize, minValue, maxValue, human1, name1, human2, name2, horizontal1, cacheDepth, searchDepth, scrollPane, score1 -> EventQueue.invokeLater(() -> scoreLabel1.setText(Integer.toString(score1))), score2 -> EventQueue.invokeLater(() -> scoreLabel2.setText(Integer.toString(score2))));

		Pair<JLabel, Box> scoreBox1 = mkScoreBox(name1, Colors.BLUE);
		Pair<JLabel, Box> scoreBox2 = mkScoreBox(name2, Colors.RED);

		scoreLabel1 = scoreBox1.first;
		scoreLabel2 = scoreBox2.first;

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

		Box box = GUIUtils.hBox(
				GUIUtils.hStrut(SCORE_BOX_SPACING),
				GUIUtils.vBox(
						GUIUtils.vStrut(SCORE_BOX_SPACING),
						GUIUtils.hCenter(nameLabel),
						GUIUtils.vStrut(SCORE_BOX_SPACING),
						GUIUtils.hCenter(scoreLabel),
						GUIUtils.vGlue()),
				GUIUtils.hStrut(SCORE_BOX_SPACING));
		return new Pair<>(scoreLabel, box);
	}

}
