package boggle.io;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.regex.Pattern;

import boggle.util.OldTrie;

public class Loader {

	private final Readable source;

	private static final Pattern GRID_DELIM = Pattern.compile("[^A-Za-z]*");

	private OldTrie words = null;
	private char[][] grid = null;

	public Loader(Readable r) {
		source = r;
	}

	public Loader(CharSequence cs) {
		this(new StringReader(cs.toString()));
	}

	public Loader(char[] chars) {
		this(new CharArrayReader(chars));
	}

	public Loader(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	public Loader(Path path) throws IOException {
		this(Files.newBufferedReader(path));
	}

	public OldTrie getWords() {
		if (words == null)
			throw new IllegalStateException();
		return words;
	}

	public char[][] getGrid() {
		if (grid == null)
			throw new IllegalStateException();
		return grid;
	}

	public void load() {
		try (Scanner sc = new Scanner(source)) {
			int wordCount = sc.nextInt();
			int gridWidth = sc.nextInt();
			int gridHeight = sc.nextInt();
			words = new OldTrie('A', 'Z');
			grid = new char[gridWidth][gridHeight];
			String word;
			int multiplicity;
			for (int i = 0; i < wordCount; i++) {
				word = sc.next();
				multiplicity = sc.nextInt();
				words.add(word, multiplicity);
			}
			sc.useDelimiter(GRID_DELIM);
			for (int i = 0; i < gridWidth; i++)
				for (int j = 0; j < gridHeight; j++)
					grid[i][j] = sc.next().charAt(0);
		}
	}

}
