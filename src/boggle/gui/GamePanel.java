package boggle.gui;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import boggle.util.OldTrie;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public GamePanel(GUI gui) {
		super(true);
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout());

		WordList list = new WordList();
		JScrollPane listParent = new JScrollPane(list);

		GridPanel gridPanel = new GridPanel();
		JPanel gridParent = new JPanel(new GridBagLayout());
		gridParent.add(gridPanel);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, gridParent, listParent);
		splitPane.setDividerLocation(0.5);

		// @formatter:off
		GUI.setup(this,
				splitPane
				);
		// @formatter:on

		OldTrie testTrie = new OldTrie('A', 'Z');
		testTrie.add("HELLOWORLD");
		testTrie.add("YEET");
		testTrie.add("BIPPITYBOPPITYBOO", 5);
		testTrie.add("EGGYOLKSARETHEWAYFORME", 2);
		list.reset(testTrie);
	}

	public void reset(OldTrie words, char[][] grid) {

	}

}
