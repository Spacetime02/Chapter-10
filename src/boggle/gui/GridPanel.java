package boggle.gui;

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
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

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

	public GridPanel() {
		super(true);
		initUI();
	}

	private void initUI() {
		setPreferredSize(new Dimension(10, 10));
		setGrid(new char[][] { { 'A' } }, 1, 1);
	}

	public void setGrid(char[][] grid, int height, int width) {
		this.height = height;
		this.width = width;
		if (height == 0)
			throw new IllegalArgumentException("Empty grid.");
		if (width == 0)
			throw new IllegalArgumentException("Empty grid.");
		// this.grid = new char[size][];
		// for (int row = 0; row < size; row++)
		// this.grid[row] = Arrays.copyOf(grid[row], size);
		setLayout(new GridLayout(height, width));
		removeAll();
		// for (JLabel[] row : labels)
		// for (JLabel label : row)
		// remove(label);
		labels = new JLabel[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				JLabel cell = new JLabel(Character.toString(grid[i][j]), SwingConstants.CENTER);
				cell.setOpaque(true);
				cell.setBackground(Color.WHITE);
				// cell.setMinimumSize(new Dimension(15, 15));
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
				add(cell);
			}
			System.out.println(i);
		}
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

}
