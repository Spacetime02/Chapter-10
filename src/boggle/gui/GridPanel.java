package boggle.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;

import boggle.util.tuple.IntPair;

class GridPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int SCROLLBAR_WIDTH = UIManager.getInt("ScrollBar.width");

	private static final float MAX_FONT_SIZE = 36f;
	private static final float FONT_SIZE_FACTOR = 0.5f;
	private static final int BORDER_THICKNESS = 2;
	private static final int MIN_CELL_SIZE = 30;

	private JLabel[][] labels = new JLabel[0][0];

	private int height = 0;
	private int width = 0;

	private final Container contentPane;

	public GridPanel(Container contentPane) {
		super(true);
		this.contentPane = contentPane;
		initUI();
	}

	private void initUI() {
		setPreferredSize(new Dimension(10, 10));
		setGrid(new char[][] { { 'A' } }, 1, 1, false, null);
	}

	public void setGrid(char[][] grid, int height, int width, boolean show, String fileName) {
		this.height = height;
		this.width = width;
		if (height == 0)
			throw new IllegalArgumentException("Empty grid.");
		if (width == 0)
			throw new IllegalArgumentException("Empty grid.");
		setLayout(new GridLayout(height, width));
		removeAll();
		labels = new JLabel[height][width];
		ProgressMonitor monitor = new ProgressMonitor(this, fileName == null ? "Loading" : "Loading " + fileName, "Loading game", 0, height);
		new LoadTask(monitor, grid, show).execute();
	}

	void setHighlight(int i, int j, boolean highlight) {
		labels[i][j].setBackground(highlight ? Color.BLUE : Color.WHITE);
	}

	void showCells(List<Integer> iList, List<Integer> jList) {
		if (iList.isEmpty())
			return;
		Rectangle rect = labels[iList.get(0)][jList.get(0)].getBounds();
		for (int n = 1; n < iList.size(); n++) {
			rect = rect.union(labels[iList.get(n)][jList.get(n)].getBounds());
		}
		((JComponent) getParent()).scrollRectToVisible(rect);
	}

	@Override
	public Dimension getPreferredSize() {
		Container parent = getParent().getParent();
		int size = Math.max(MIN_CELL_SIZE, Math.min((parent.getWidth() - SCROLLBAR_WIDTH) / width, (parent.getHeight() - SCROLLBAR_WIDTH) / height));
		return new Dimension(size * width, size * height);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension();
	}

	private class LoadTask extends SwingWorker<Void, IntPair> /* implements PropertyChangeListener */ {

		private final ProgressMonitor monitor;
		private final char[][] grid;
		private final boolean show;
		private volatile int added = 0;

		private LoadTask(ProgressMonitor monitor, char[][] grid, boolean show) {
			this.monitor = monitor;
			this.grid = grid;
			this.show = show;
		}

		@Override
		protected Void doInBackground() {
			// double pos = (double) splitPane.getDividerLocation() / (splitPane.getWidth() - splitPane.getDividerSize());
			// this.pos = 0d;
			// publish((IntPair) null);
			for (int i = 0; i < height; i++) {
				if (monitor.isCanceled())
					return null;
				while (added < i - 1);
				for (int j = 0; j < width; j++) {
					JLabel cell = new JLabel(Character.toString(grid[i][j]), SwingConstants.CENTER);
					cell.setOpaque(true);
					cell.setBackground(Color.WHITE);
					// @formatter:off
							Border b = BorderFactory.createMatteBorder(
									i == 0 ? BORDER_THICKNESS : 0,
									j == 0 ? BORDER_THICKNESS : 0,
									BORDER_THICKNESS,
									BORDER_THICKNESS,
									Color.BLACK
									);
							// @formatter:on
					cell.setBorder(b);
					cell.addComponentListener(new ComponentAdapter() {

						@Override
						public void componentResized(ComponentEvent e) {
							cell.setFont(GUI.getFont(Font.DIALOG, Font.PLAIN, Math.min(MAX_FONT_SIZE, FONT_SIZE_FACTOR * cell.getHeight())));
						}

					});
					labels[i][j] = cell;
					publish(new IntPair(i, j));
				}
			}
			publish((IntPair) null);
			return null;
		}

		@Override
		protected void process(List<IntPair> chunks) {
			for (IntPair chunk : chunks) {
				if (chunk == null) {
					if (show)
						((CardLayout) contentPane.getLayout()).show(contentPane, "gamePanel");
					monitor.close();
				} else {
					int i = chunk.value1;
					int j = chunk.value2;
					add(labels[i][j]);
					setProgress(i * 100 / height);
					monitor.setProgress(i);
					monitor.setNote("Row " + (i + 1) + "/" + height);
					added = i;
				}
			}
		}

		// @Override
		// public void propertyChange(PropertyChangeEvent evt) {
		// if ("progress".equals(evt.getPropertyName())) {
		// int progress = (int) evt.getNewValue();
		// monitor.setProgress(progress);
		// }
		// }
	}

}
