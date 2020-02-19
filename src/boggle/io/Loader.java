package boggle.io;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Loader {

	private final Random randy = new Random();

	private final Readable source;

	private static final Pattern GRID_DELIM = Pattern.compile("[^A-Za-z]*");

	private List<String> words = null;
	private char[][] grid = null;

	private int height;
	private int width;

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

	public List<String> getWords() {
		if (words == null)
			throw new IllegalStateException();
		return words;
	}

	public char[][] getGrid() {
		if (grid == null)
			throw new IllegalStateException();
		return grid;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void load() throws IOException {
		try (Scanner sc = new Scanner(source)) {
			int wordCount = sc.nextInt();
			words = new ArrayList<>(wordCount);
			String word;
			for (int i = 0; i < wordCount; i++) {
				word = sc.next().toUpperCase();
				words.add(word);
			}
			words.sort(null);
			height = sc.nextInt();
			width = sc.nextInt();
			grid = new char[height][width];
			sc.useDelimiter(GRID_DELIM);
			char c;
			if (sc.hasNext())
				for (int i = 0; i < height; i++)
					for (int j = 0; j < width; j++) {
						c = Character.toUpperCase(sc.next().charAt(0));
						if (c < 'A' || c > 'Z')
							c = (char) (randy.nextInt(26) + 'A');
						grid[i][j] = c;
					}
			else
				for (int i = 0; i < height; i++)
					for (int j = 0; j < width; j++)
						grid[i][j] = (char) (randy.nextInt(26) + 'A');
		}
		catch (NoSuchElementException e) {
			throw new IOException("Illegal file format.");
		}
	}

}
