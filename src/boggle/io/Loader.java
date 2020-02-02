package boggle.io;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import boggle.util.function.IntBiConsumer;
import boggle.util.tuple.IntPair;

public abstract class Loader<T> {

	private final Readable src;
	private final BlockingQueue<IntPair> progressQueue;

	public Loader(Readable r) {
		src = r;
		progressQueue = new LinkedBlockingQueue<IntPair>();
	}

	public Loader(InputStream is) {
		this(new InputStreamReader(is));
	}

	public Loader(char[] chars) {
		this(new CharArrayReader(chars));
	}

	public Loader(byte[] bytes) {
		this(new ByteArrayInputStream(bytes));
	}

	public Loader(CharSequence cs) {
		this(new StringReader(cs.toString()));
	}

	public Loader(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	public Loader(Path path) throws IOException {
		this(Files.newInputStream(path));
	}

	public Loader(URI uri) throws MalformedURLException, IOException {
		this(uri.toURL());
	}

	public Loader(URL url) throws IOException {
		this(url.openConnection());
	}

	public Loader(URLConnection urlCon) throws IOException {
		this(urlCon.getInputStream());
	}

	protected final void reportProgress(int num, int index) {
		progressQueue.add(new IntPair(num, index));
	}

	protected abstract T load(Readable source);

	public T get(IntBiConsumer progressHandler) {
		
		return load(src);
	}

}
