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
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import boggle.main.Main;
import boggle.util.function.IntBiConsumer;
import boggle.util.tuple.IntPair;

public abstract class Loader<T> {

	private final Readable src;
	private final BlockingQueue<Optional<IntPair>> progressQueue;

	public Loader(Readable r) {
		src = r;
		progressQueue = new LinkedBlockingQueue<>();
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
		progressQueue.add(Optional.of(new IntPair(num, index)));
	}

	protected abstract T load(Readable source);

	public T get(IntBiConsumer progressHandler) {
		ProgressHandler ph = new ProgressHandler(progressHandler);
		ph.start();
		T loaded = load(src);
		progressQueue.add(Optional.empty());
		try {
			ph.join();
			if (ph.exception != null)
				throw ph.exception;
		}
		catch (InterruptedException e) {
			if (Main.DEBUG)
				e.printStackTrace(System.err);
			return null;
		}

		return loaded;
	}

	private class ProgressHandler extends Thread {

		private final IntBiConsumer handler;
		private InterruptedException exception;

		private ProgressHandler(IntBiConsumer handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			try {
				handle();
				exception = null;
			}
			catch (InterruptedException exc) {
				exception = exc;
			}
		}

		private void handle() throws InterruptedException {
			Optional<IntPair> opt = progressQueue.take();
			IntPair pair;
			while (opt.isPresent()) {
				pair = opt.orElseThrow();
				handler.accept(pair.value1, pair.value2);
				opt = progressQueue.take();
			}
		}

	}

}
