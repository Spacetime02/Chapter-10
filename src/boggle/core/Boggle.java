package boggle.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import boggle.util.trie.Trie;
import boggle.util.trie.WordTrie;

public class Boggle {

	private static final char[] EMPTY_CHAR_ARR = {};
	private static final int[] EMPTY_INT_ARR = {};

	private static final int[][] ADJ = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };

	private final int height;
	private final int width;

	private char[][] grid;
	private Trie words;

	public Boggle(char[][] grid, Collection<String> words) {
		if (grid == null)
			throw new NullPointerException();
		height = grid.length;
		if (height == 0)
			throw new IllegalArgumentException();
		width = grid[0].length;
		if (width == 0)
			throw new IllegalArgumentException();
		this.grid = new char[height][width];
		for (int i = 0; i < height; i++) {
			char[] row = grid[i];
			if (row.length != width)
				throw new IllegalArgumentException();
			System.arraycopy(row, 0, this.grid[i], 0, width);
		}
		if (words.isEmpty())
			throw new IllegalArgumentException();
		this.words = new WordTrie(words);
	}

	public void solve(List<int[]> iPaths, List<int[]> jPaths, List<char[]> found) {
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				solve(i, j, 0, EMPTY_INT_ARR, EMPTY_INT_ARR, words, iPaths, jPaths, EMPTY_CHAR_ARR, found);
	}

	private void solve(int i, int j, int depth, int[] iPath, int[] jPath, Trie filteredWords, List<int[]> iPaths, List<int[]> jPaths, char[] path, List<char[]> found) {
		if (i < 0 || i >= height || j < 0 || j >= width || filteredWords == null || filteredWords.isEmpty())
			return;
		char c = grid[i][j];
		grid[i][j] = '\0';
		Trie nextTrie = filteredWords.after(c);
		int nextDepth = depth + 1;
		iPath = Arrays.copyOf(iPath, nextDepth);
		iPath[depth] = i;
		jPath = Arrays.copyOf(jPath, nextDepth);
		jPath[depth] = j;
		path = Arrays.copyOf(path, nextDepth);
		path[depth] = c;
		if (nextTrie != null && nextTrie.containsEmpty()) {
			iPaths.add(iPath);
			jPaths.add(jPath);
			found.add(path);
		}
		for (int[] adj : ADJ)
			solve(i + adj[0], j + adj[1], nextDepth, iPath, jPath, nextTrie, iPaths, jPaths, path, found);
		grid[i][j] = c;
	}

}
