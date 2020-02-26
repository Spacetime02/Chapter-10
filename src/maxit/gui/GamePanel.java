package maxit.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Random randy = new Random();

	private int n;
//	private int nSqr;

	public GamePanel() {
		super(new BorderLayout(), true);
		setBackground(Color.WHITE);
	}

	// FIXME
	void setup(int n) {
		this.n = n;
//		nSqr = n * n;
		JScrollPane scroll = new JScrollPane();
		JPanel gridPositioner = new JPanel(new GridBagLayout());
		JPanel gridPanel = new JPanel(new GridLayout(n, n)) {

			@Override
			public Dimension getPreferredSize() {
				Dimension dim = getSize();
				int size = Math.max(20 * n, Math.min(dim.height, dim.width));
				return new Dimension(size, size);
			}

		};

		int[][] grid = new int[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				grid[i][j] = randy.nextInt(20); // TODO remove hardcoded maximum
		new LoadTask(grid, gridPanel).execute();

		// @formatter:off
		GUI.setup(
				this,
				GUI.setup(
						scroll,
						GUI.setup(
								gridPositioner,
								gridPanel
								)
						)
				);
		// @formatter:on

	}

	private class LoadTask extends SwingWorker<Void, Runnable> {

		private final int[][] grid;

		private JPanel gridPanel;

		private ProgressMonitor monitor;

		private volatile int added = 0;

		private LoadTask(int[][] grid, JPanel gridPanel) {
			this.grid = grid;
			this.gridPanel = gridPanel;
		}

		@Override
		protected Void doInBackground() {
			publish(() -> {
				monitor = new ProgressMonitor(GamePanel.this, String.format("Loading %dx%<d Grid", n), null, 0, n);
				monitor.setProgress(0);
			});
			for (int i = 0; i < n;) {
				while (added + 1 < i)
					;
				int[] row = grid[i];
				for (int j = 0; j < n; j++) {
					final JButton cell = new JButton(Integer.toString(row[j]));
					cell.setBorder(BorderFactory.createMatteBorder(i == 0 ? 1 : 0, j == 0 ? 1 : 0, 1, 1, Color.BLACK));
					cell.setForeground(GUI.DEEP_SKY_BLUE);
					publish(() -> gridPanel.add(cell));
				}
				final int finalI = ++i;
				final String note = String.format("Row %d/%d", finalI, n);
				publish(() -> {
					monitor.setProgress(finalI);
					monitor.setNote(note);
				});

			}
			return null;
		}

		@Override
		protected void process(List<Runnable> chunks) {
			for (Runnable r : chunks)
				if (r != null)
					r.run();
				else
					throw new Error("Null chunk! This should not happen!");
		}

	}

}
