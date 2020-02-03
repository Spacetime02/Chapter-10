package boggle.gui;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import boggle.util.Trie;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public GamePanel(GUI gui) {
		super(true);
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout());

		String[] data = new String[1000];
		for (int i = 0; i < 1000; i++) {
			data[i] = "";
			for (int j = i; j < 1000; j++)
				data[i] += "-";
		}

		Trie testTrie = new Trie('A', 'Z');
		testTrie.add("HELLOWORLD");
		testTrie.add("YEET");
		testTrie.add("BIPPITYBOPPITYBOO", 5);
		testTrie.add("EGGYOLKSARETHEWAYFORME", 2);

		JList<String> list = new WordList(testTrie);

		JPanel gridPanel = new JPanel();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, gridPanel, new JScrollPane(list));
		splitPane.setDividerLocation(0.5);

		// @formatter:off
		GUI.setup(this,
				splitPane
				);
		// @formatter:on
	}

}
