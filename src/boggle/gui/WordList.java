package boggle.gui;

import javax.swing.JList;

import boggle.util.Trie;

public class WordList extends JList<String> {

	private static final long serialVersionUID = 1L;

	public WordList(Trie words) {
		super(words.toArray());
	}

}
