package boggle.gui;

import javax.swing.JList;

import boggle.util.OldTrie;

class WordList extends JList<String> {

	private static final long serialVersionUID = 1L;

	public void reset(OldTrie words) {
		setListData(words.toArray());
	}

}
