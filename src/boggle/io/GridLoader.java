package boggle.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.regex.Pattern;

import boggle.util.function.IntBiConsumer;

public class GridLoader extends Loader<char[][]> {

	private static final Pattern DELIM = Pattern.compile("\\p{javaWhitespace}");

	public GridLoader(Readable r) {
		super(r);
	}

	public GridLoader(InputStream is) {
		super(is);
	}

	public GridLoader(char[] chars) {
		super(chars);
	}

	public GridLoader(byte[] bytes) {
		super(bytes);
	}

	public GridLoader(CharSequence cs) {
		super(cs);
	}

	public GridLoader(File file) throws FileNotFoundException {
		super(file);
	}

	public GridLoader(Path path) throws IOException {
		super(path);
	}

	public GridLoader(URI uri) throws MalformedURLException, IOException {
		super(uri);
	}

	public GridLoader(URL url) throws IOException {
		super(url);
	}

	public GridLoader(URLConnection urlCon) throws IOException {
		super(urlCon);
	}

	@Override
	protected char[][] load(Readable source) {
		try (Scanner sc = new Scanner(source)) {
			int w = sc.nextInt();
			int h = sc.nextInt();
			int num = w * h;
			char[][] grid = new char[w][h];
			int index = 0;
			sc.useDelimiter(DELIM);
			reportProgress(num, 0);
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					grid[i][j] = sc.next().charAt(0);
					reportProgress(num, ++index);
				}
			}
			return grid;
		}
	}

}
