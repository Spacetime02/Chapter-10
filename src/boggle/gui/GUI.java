package boggle.gui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import boggle.util.Trie;

public class GUI {

	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	private char[][] grid;
	private Trie words;

	public GUI() {
		// grid = loadGrid();
	}

}
