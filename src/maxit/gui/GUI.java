package maxit.gui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	public GUI() {
		super("MAXIT");
		setLayout(new CardLayout());

		JPanel sizeSelect = new JPanel();
		vLayout(sizeSelect);

		JLabel label = new JLabel("Grid size:");

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(5, 1, null, 1));

		// @formatter:off
		setup(
				sizeSelect,
				vGlue(),
				setup(
						hGlue(),
						label,
						spinner,
						hGlue()
						),
				vGlue()
				);
		// @formatter:on

		add(sizeSelect, "sizeSelect");

		pack();
		setVisible(true);
	}

	static Box.Filler hGlue() {
		return (Box.Filler) Box.createHorizontalGlue();
	}

	static Box.Filler vGlue() {
		return (Box.Filler) Box.createVerticalGlue();
	}

	static Box.Filler glue() {
		return (Box.Filler) Box.createGlue();
	}

	static Box.Filler hStrut(int size) {
		return (Box.Filler) Box.createHorizontalStrut(size);
	}

	static Box.Filler vStrut(int size) {
		return (Box.Filler) Box.createVerticalStrut(size);
	}

	static Box.Filler rigidArea(int width, int height) {
		return rArea(new Dimension(width, height));
	}

	static Box.Filler rArea(Dimension dim) {
		return (Box.Filler) Box.createRigidArea(dim);
	}

	static Box hBox(Component... children) {
		return setup(Box.createHorizontalBox(), children);
	}

	static Box vBox(Component... children) {
		return setup(Box.createVerticalBox(), children);
	}

	static BoxLayout hLayout(Container parent) {
		return layout(parent, BoxLayout.X_AXIS);
	}

	static BoxLayout vLayout(Container parent) {
		return layout(parent, BoxLayout.Y_AXIS);
	}

	static BoxLayout layout(Container parent, int axis) {
		BoxLayout layout = new BoxLayout(parent, axis);
		if (parent != null)
			parent.setLayout(layout);
		return layout;
	}

	static <T extends Container> T setup(T parent, Component... children) {
		if (parent != null)
			for (Component child : children)
				parent.add(child);
		return parent;
	}

}
