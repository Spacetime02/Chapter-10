package maxit.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

//	private int n;
//	private int nSqr;

	public GamePanel() {
		super(new BorderLayout(), true);
		setBackground(Color.WHITE);
	}

	// FIXME
	void setup(int size, int max) {
//		this.n = n;
//		nSqr = n * n;
		JPanel gridPositioner = new JPanel(new GridBagLayout());
		gridPositioner.setBackground(Colors.LIGHT_GREEN_1);

		JScrollPane scroll = new JScrollPane(gridPositioner);

		JPanel gridPanel = new GridPanel(size, max, scroll.getViewport());

		add(scroll);
		gridPositioner.add(gridPanel);
		gridPanel.setBackground(Color.RED);
//		gridPositioner.setBackground(Color.RED);

		int[][] grid = new int[size][size];
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
//				grid[i][j] = randy.nextInt(20); // TODO remove hardcoded maximum
				grid[i][j] = i * 10 + j;

		((GUI) getTopLevelAncestor()).showGamePanel();

		// @formatter:off
		
//		GUI.setup(
//				this,
//				GUI.setup(
//						scroll,
//						GUI.setup(
//								gridPositioner//,
//								gridPanel
//								)
//						)
//				);
//		new LoadTask(grid, gridPanel).execute();
//		System.out.println(((GridLayout)gridPanel.getLayout()).getColumns());
		// @formatter:on

	}

	/*
	@formatter:off
	private class LoadTask extends SwingWorker<Void, Runnable> {

		private final int[][] grid;

		private JPanel gridPanel;

		private volatile ProgressMonitor monitor;

		private volatile int added = 0;

		private LoadTask(int[][] grid, JPanel gridPanel) {
			this.grid = grid;
			this.gridPanel = gridPanel;
		}

		@Override
		protected Void doInBackground() {

			try {
				publish(() -> {
					monitor = new ProgressMonitor(GamePanel.this, String.format("Loading %dx%<d Grid", n), null, 0, n);
					monitor.setProgress(0);
					System.out.println("init");
				});
				for (int i = 0; i < n;) {
					while (added < i - 1)
						System.out.println(added + "<" + (i - 1));
					int[] row = grid[i];
					final int fi = i;
					for (int j = 0; j < n; j++) {
						final int fj = j;
						final String val = Integer.toString(row[j]);
						publish(() -> {
							final JButton cell = new JButton(val);
							cell.setBorder(BorderFactory.createMatteBorder(fi == 0 ? 1 : 0, fj == 0 ? 1 : 0, 1, 1,
									Color.BLACK));
							cell.setForeground(GUI.DEEP_SKY_BLUE);
							gridPanel.add(cell);
							System.out.println("Row " + fi + " Col " + fj);
						});
					}
					publish(() -> {
						added = fi;
						System.out.println("added -> " + added);
					});
					final int finalI = ++i;
					final String note = String.format("Row %d/%d", finalI, n);
					publish(() -> {
						monitor.setProgress(finalI);
						monitor.setNote(note);
						((GUI) getTopLevelAncestor()).showGamePanel();
						System.out.println("end");
					});
				}
				Thread.sleep(15);
//					publish(gridPanel.getLayout().);
				publish(() -> System.out.println("Monitor: " + monitor));
				System.out.println(monitor);
				Runnable r = monitor::close;
				publish(r);
			} catch (Throwable e) {
				e.printStackTrace();
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
	@formatter:on
	*/
}
