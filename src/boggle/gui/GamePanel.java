package boggle.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import boggle.core.Boggle;

class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private int width;
	private int height;

	private Boggle boggle;

	private JList<String> list;
	private GridPanel gridPanel;

	private Map<String, Integer> solution;

	private ArrayList<int[]> iPaths;
	private ArrayList<int[]> jPaths;

	public GamePanel(GUI gui) {
		super(true);
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout());

		list = new JList<String>();
		JScrollPane listScroll = new JScrollPane(list);

		gridPanel = new GridPanel();
		JPanel gridParent = new JPanel(new GridBagLayout());
		gridParent.setBackground(Color.WHITE);
		gridParent.add(gridPanel);
		JScrollPane gridScroll = new JScrollPane(gridParent);
		// gridScroll.

		JButton solveButton = new JButton("SOLVE");
		solveButton.setFont(GUI.getFont(Font.DIALOG, Font.BOLD, 40f));
		solveButton.setBackground(GUI.SKY_BLUE);
		solveButton.setCursor(GUI.HAND_CURSOR);
		solveButton.setFocusPainted(false);
		solveButton.setBorder(null);
		solveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				solve();
			}

		});

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, gridScroll, listScroll);

		add(solveButton, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				splitPane.setDividerLocation(0.5);
			}

		});
	}

	void setup(char[][] grid, int height, int width, Collection<String> words) {
		this.height = height;
		this.width = width;
		list.setListData(words.toArray(new String[words.size()]));
		gridPanel.setGrid(grid, height, width);
		height = grid.length;
		width = grid[0].length;
		boggle = new Boggle(grid, words);
	}

	private void solve() {
		List<int[]> iPaths = new ArrayList<>();
		List<int[]> jPaths = new ArrayList<>();
		List<char[]> found = new ArrayList<>();
		boggle.solve(iPaths, jPaths, found);
		int numFound = found.size();
		solution = new HashMap<String, Integer>();
		this.iPaths = new ArrayList<>();
		this.jPaths = new ArrayList<>();
		int ct = 0;
		for (int n = 0; n < numFound; n++) {
			String str = String.valueOf(found.get(n));
			if (solution.get(str) == null) {
				solution.put(str, ct++);
				this.iPaths.add(iPaths.get(n));
				this.jPaths.add(jPaths.get(n));
				// if (this.iPaths.size() != ct)
				// System.err.println(ct + "!=" + this.iPaths.size());
			}
		}
		ListModel<String> model = list.getModel();
		int wordCt = model.getSize();
		String[] words = new String[wordCt];
		for (int i = 0; i < wordCt; i++)
			words[i] = model.getElementAt(i);
		Arrays.sort(words, Comparator.comparingInt((String word) -> solution.get(word) == null ? 1 : 0)/* .thenComparing(String.CASE_INSENSITIVE_ORDER) */); // thenComparing is unnecessary as TimSort
																																								// is stable
		list.setListData(words);

		list.setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				comp.setBackground(solution.get(value) == null ? Color.RED : Color.GREEN);
				return comp;
			}

		});

		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				List<Integer> iList = new ArrayList<>();
				List<Integer> jList = new ArrayList<>();
				int first = e.getFirstIndex();
				int last = e.getLastIndex();
				ListModel<String> model = list.getModel();
				boolean[][] highlight = new boolean[height][width];
				for (int m = first; m <= last; m++) {
					if (list.isSelectedIndex(m)) {
						Integer index = solution.get(model.getElementAt(m));
						if (index != null) {
							int[] iPath = GamePanel.this.iPaths.get(index);
							int[] jPath = GamePanel.this.jPaths.get(index);
							for (int n = 0; n < iPath.length; n++) {
								int i = iPath[n];
								int j = jPath[n];
								iList.add(i);
								jList.add(j);
								highlight[i][j] = true;
							}
						}
					}
				}
				for (int i = 0; i < height; i++)
					for (int j = 0; j < width; j++)
						gridPanel.setHighlight(i, j, highlight[i][j]);
				gridPanel.showCells(iList, jList);
			}

		});
		// System.out.println(solution);
		// for (int i = 0; i < iPaths.size(); i++) {
		// System.out.println(i + ":");
		// System.out.println(Arrays.toString(iPaths.get(i)));
		// System.out.println(Arrays.toString(jPaths.get(i)));
		// System.out.println();
		// }
	}

	// public void reset(Trie words, char[][] grid) {
	//
	// }

}
