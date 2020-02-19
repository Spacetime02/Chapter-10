package boggle.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

class MainMenu extends JPanel {

	private static final long serialVersionUID = 1L;

	// private JButton settings;
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

		// Icon ico = GUI.SETTINGS_ICON;

		// settings = new JButton(ico);
		// settings.setForeground(Color.BLACK);
		// settings.setBackground(Color.WHITE);
		// settings.setPreferredSize(new Dimension(ico.getIconWidth(), ico.getIconHeight()));
		// settings.setCursor(GUI.HAND_CURSOR);
		// settings.setBorder(null);
		// settings.setFocusPainted(false);
		// settings.addActionListener(new SettingsListener());

		title = new JLabel("BOGGLE");
		title.setFont(GUI.TITLE_FONT);
		title.setForeground(Color.GREEN);

		play = new JButton(" PLAY ");
		play.setFont(GUI.TITLE_FONT);
		play.setForeground(Color.GREEN);
		play.setBackground(Color.BLACK);
		play.setCursor(GUI.HAND_CURSOR);
		play.setBorder(null);
		play.setFocusPainted(false);
		play.addActionListener(new PlayListener());

		// @formatter:off
		GUI.setup(this,
//				GUI.hBox(
//						GUI.hGlue(),
//						settings
//						),
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
				GUI.vGlue()//,
//				GUI.vStrut(ico.getIconHeight())
				);
		// @formatter:on
	}

	private class PlayListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			gui.showLevelSelectMenu();
		}

	}

	// private class SettingsListener implements ActionListener {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// gui.showGamePanel();
	// Container cp = gui.getContentPane();
	// CardLayout layout = (CardLayout) cp.getLayout();
	// layout.show(cp, "gamePanel");
	// }
	//
	// }

}
