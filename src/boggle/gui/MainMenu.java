package boggle.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

class MainMenu extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Cursor HAND = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	private JButton settings;
	private JLabel title;
	private JButton play;

	private GUI gui;

	public MainMenu(GUI gui) {
		super(false);
		this.gui = gui;
		initUI();
	}

	private void initUI() {
		setBackground(Color.WHITE);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		settings = new JButton(GUI.SETTINGS_ICON);
		settings.setForeground(Color.BLACK);
		settings.setBackground(Color.WHITE);
		settings.setPreferredSize(new Dimension(GUI.SETTINGS_ICON.getIconWidth(), GUI.SETTINGS_ICON.getIconHeight()));
		settings.setCursor(HAND);
		settings.setBorder(null);
		settings.setFocusPainted(false);
		settings.addActionListener(new SettingsListener());

		title = new JLabel("BOGGLE");
		title.setFont(GUI.TITLE_FONT);
		title.setForeground(Color.GREEN);

		play = new JButton(" PLAY ");
		play.setFont(GUI.TITLE_FONT);
		play.setForeground(Color.GREEN);
		play.setBackground(Color.BLACK);
		play.setCursor(HAND);
		play.setBorder(null);
		play.setFocusPainted(false);
		play.addActionListener(new PlayListener());

		// @formatter:off
		GUI.setup(this,
				GUI.hBox(
						GUI.hGlue(),
						settings
						),
				GUI.vGlue(),
				GUI.hBox(
						GUI.hGlue(),
						title,
						GUI.hGlue()
						),
				GUI.vGlue(),
				GUI.hBox(
						GUI.hGlue(),
						play,
						GUI.hGlue()
						),
				GUI.vGlue(),
				GUI.vStrut(GUI.SETTINGS_ICON.getIconHeight())
				);
		// @formatter:on
	}

	private class PlayListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			gui.showLevelSelectMenu();
		}

	}

	private class SettingsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			((CardLayout) gui.getContentPane().getLayout()).show(gui.getContentPane(), "gamePanel");
		}

	}

}
