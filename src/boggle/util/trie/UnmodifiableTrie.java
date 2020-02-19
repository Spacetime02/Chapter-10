package boggle.util.trie;

import java.util.Collection;

final class UnmodifiableTrie extends Trie {

	private final Trie backing;

	UnmodifiableTrie(Trie backing) {
		this.backing = backing;
	}

}