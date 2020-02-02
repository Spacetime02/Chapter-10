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
import java.util.function.BiConsumer;

import boggle.util.Trie;
import boggle.util.function.IntBiConsumer;

public class WordListLoader extends Loader<Trie> {

	public WordListLoader(byte[] bytes) {
		super(bytes);
	}

	public WordListLoader(char[] chars) {
		super(chars);
	}

	public WordListLoader(CharSequence cs) {
		super(cs);
	}

	public WordListLoader(File file) throws FileNotFoundException {
		super(file);
	}

	public WordListLoader(InputStream is) {
		super(is);
	}

	public WordListLoader(Path path) throws IOException {
		super(path);
	}

	public WordListLoader(Readable r) {
		super(r);
	}

	public WordListLoader(URI uri) throws MalformedURLException, IOException {
		super(uri);
	}

	public WordListLoader(URL url) throws IOException {
		super(url);
	}

	public WordListLoader(URLConnection urlCon) throws IOException {
		super(urlCon);
	}

	@Override
	protected Trie load(Readable source, IntBiConsumer progressHandler) {
		try (Scanner sc = new Scanner(source)) {
			Trie trie = new Trie('A', 'Z');
			int num = sc.nextInt();
			progressHandler.accept(num, 0);
			for (int i = 0; i < num;) {
				trie.add(sc.next());
				progressHandler.accept(num, ++i);
			}
			return trie;
		}
	}

}
