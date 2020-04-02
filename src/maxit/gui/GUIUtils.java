package maxit.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;

public final class GUIUtils {

	private GUIUtils() {}

	public static Box.Filler hGlue() {
		return (Box.Filler) Box.createHorizontalGlue();
	}

	public static Box.Filler vGlue() {
		return (Box.Filler) Box.createVerticalGlue();
	}

	public static Box.Filler glue() {
		return (Box.Filler) Box.createGlue();
	}

	public static Box.Filler hStrut(int size) {
		return (Box.Filler) Box.createHorizontalStrut(size);
	}

	public static Box.Filler vStrut(int size) {
		return (Box.Filler) Box.createVerticalStrut(size);
	}

	public static Box.Filler rArea(int width, int height) {
		return rArea(new Dimension(width, height));
	}

	public static Box.Filler rArea(Dimension dim) {
		return (Box.Filler) Box.createRigidArea(dim);
	}

	public static Box hBox(Component... children) {
		return setup(Box.createHorizontalBox(), children);
	}

	public static Box vBox(Component... children) {
		return setup(Box.createVerticalBox(), children);
	}

	/**
	 * @param children
	 * @return
	 */
	public static Box hCenter(Component... children) {
		return (Box) hSpace(null, children);
	}

	/**
	 * @param parent
	 * @param children
	 * @return
	 */
	public static Container hSpace(Container parent, Component... children) {
		if (parent == null)
			parent = hBox();
		setup(parent, hGlue());
		for (Component child : children)
			setup(parent, child, hGlue());
		return parent;
	}

	/**
	 * @param children
	 * @return
	 */
	public static Box vCenter(Component... children) {
		return (Box) vSpace(null, children);
	}

	/**
	 * @param parent
	 * @param children
	 * @return
	 */
	public static Container vSpace(Container parent, Component... children) {
		if (parent == null)
			parent = vBox();
		setup(parent, vGlue());
		for (Component child : children)
			setup(parent, child, vGlue());
		return parent;
	}

	public static BoxLayout hLayout(Container parent) {
		return layout(parent, BoxLayout.X_AXIS);
	}

	public static BoxLayout vLayout(Container parent) {
		return layout(parent, BoxLayout.Y_AXIS);
	}

	public static BoxLayout layout(Container parent, int axis) {
		BoxLayout layout = new BoxLayout(parent, axis);
		if (parent != null)
			parent.setLayout(layout);
		return layout;
	}

	public static <T extends Container> T setup(T parent, Component... children) {
		if (parent != null)
			for (Component child : children)
				parent.add(child);
		return parent;
	}

}
