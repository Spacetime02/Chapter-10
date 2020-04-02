package maxit.gui;

import java.awt.Color;

public final class Colors {

	private Colors() {}

	private static final double[] levels = { 0.00, 0.05, 0.07, 0.08, 0.09, 0.11, 0.12, 0.14, 0.15, 0.16 };

	// Basic
	public static final Color BACKGROUND    = makeGray(0x12);
	public static final Color ON_BACKGROUND = makeGray(0xff);

	// Primary
	public static Color RED         = new Color(0xef9a9a); // Red
	public static Color ORANGE      = new Color(0xffcc80); // Orange
	public static Color YELLOW      = new Color(0xfff59d); // Yellow
	public static Color LIGHT_GREEN = new Color(0xc5e1a5); // Light Green
	public static Color BLUE        = new Color(0x90caf9); // Blue
	public static Color PURPLE      = new Color(0xce93d8); // Purple

	// Variants
	public static final Color BACKGROUND_0 = darken(Color.WHITE, 0);
	public static final Color BACKGROUND_1 = darken(Color.WHITE, 1);
	public static final Color BACKGROUND_2 = darken(Color.WHITE, 2);
	public static final Color BACKGROUND_3 = darken(Color.WHITE, 3);
	public static final Color BACKGROUND_4 = darken(Color.WHITE, 4);
	public static final Color BACKGROUND_5 = darken(Color.WHITE, 5);
	public static final Color BACKGROUND_6 = darken(Color.WHITE, 6);
	public static final Color BACKGROUND_7 = darken(Color.WHITE, 7);
	public static final Color BACKGROUND_8 = darken(Color.WHITE, 8);
	public static final Color BACKGROUND_9 = darken(Color.WHITE, 9);

	public static final Color RED_0 = darken(RED, 0);
	public static final Color RED_1 = darken(RED, 1);
	public static final Color RED_2 = darken(RED, 2);
	public static final Color RED_3 = darken(RED, 3);
	public static final Color RED_4 = darken(RED, 4);
	public static final Color RED_5 = darken(RED, 5);
	public static final Color RED_6 = darken(RED, 6);
	public static final Color RED_7 = darken(RED, 7);
	public static final Color RED_8 = darken(RED, 8);
	public static final Color RED_9 = darken(RED, 9);

	public static final Color ORANGE_0 = darken(ORANGE, 0);
	public static final Color ORANGE_1 = darken(ORANGE, 1);
	public static final Color ORANGE_2 = darken(ORANGE, 2);
	public static final Color ORANGE_3 = darken(ORANGE, 3);
	public static final Color ORANGE_4 = darken(ORANGE, 4);
	public static final Color ORANGE_5 = darken(ORANGE, 5);
	public static final Color ORANGE_6 = darken(ORANGE, 6);
	public static final Color ORANGE_7 = darken(ORANGE, 7);
	public static final Color ORANGE_8 = darken(ORANGE, 8);
	public static final Color ORANGE_9 = darken(ORANGE, 9);

	public static final Color YELLOW_0 = darken(YELLOW, 0);
	public static final Color YELLOW_1 = darken(YELLOW, 1);
	public static final Color YELLOW_2 = darken(YELLOW, 2);
	public static final Color YELLOW_3 = darken(YELLOW, 3);
	public static final Color YELLOW_4 = darken(YELLOW, 4);
	public static final Color YELLOW_5 = darken(YELLOW, 5);
	public static final Color YELLOW_6 = darken(YELLOW, 6);
	public static final Color YELLOW_7 = darken(YELLOW, 7);
	public static final Color YELLOW_8 = darken(YELLOW, 8);
	public static final Color YELLOW_9 = darken(YELLOW, 9);

	public static final Color LIGHT_GREEN_0 = darken(LIGHT_GREEN, 0);
	public static final Color LIGHT_GREEN_1 = darken(LIGHT_GREEN, 1);
	public static final Color LIGHT_GREEN_2 = darken(LIGHT_GREEN, 2);
	public static final Color LIGHT_GREEN_3 = darken(LIGHT_GREEN, 3);
	public static final Color LIGHT_GREEN_4 = darken(LIGHT_GREEN, 4);
	public static final Color LIGHT_GREEN_5 = darken(LIGHT_GREEN, 5);
	public static final Color LIGHT_GREEN_6 = darken(LIGHT_GREEN, 6);
	public static final Color LIGHT_GREEN_7 = darken(LIGHT_GREEN, 7);
	public static final Color LIGHT_GREEN_8 = darken(LIGHT_GREEN, 8);
	public static final Color LIGHT_GREEN_9 = darken(LIGHT_GREEN, 9);

	public static final Color BLUE_0 = darken(BLUE, 0);
	public static final Color BLUE_1 = darken(BLUE, 1);
	public static final Color BLUE_2 = darken(BLUE, 2);
	public static final Color BLUE_3 = darken(BLUE, 3);
	public static final Color BLUE_4 = darken(BLUE, 4);
	public static final Color BLUE_5 = darken(BLUE, 5);
	public static final Color BLUE_6 = darken(BLUE, 6);
	public static final Color BLUE_7 = darken(BLUE, 7);
	public static final Color BLUE_8 = darken(BLUE, 8);
	public static final Color BLUE_9 = darken(BLUE, 9);

	public static final Color PURPLE_0 = darken(PURPLE, 0);
	public static final Color PURPLE_1 = darken(PURPLE, 1);
	public static final Color PURPLE_2 = darken(PURPLE, 2);
	public static final Color PURPLE_3 = darken(PURPLE, 3);
	public static final Color PURPLE_4 = darken(PURPLE, 4);
	public static final Color PURPLE_5 = darken(PURPLE, 5);
	public static final Color PURPLE_6 = darken(PURPLE, 6);
	public static final Color PURPLE_7 = darken(PURPLE, 7);
	public static final Color PURPLE_8 = darken(PURPLE, 8);
	public static final Color PURPLE_9 = darken(PURPLE, 9);

	public static Color makeGray(int brightness) {
		return new Color(brightness, brightness, brightness);
	}

	/**
	 * @param hue        [0, 360) (constrained using modulus)
	 * @param saturation [0, 255]
	 * @param brightness [0, 255]
	 */
	public static Color makeHSB(int hue, int saturation, int brightness) {
		hue = hue >= 0 ? hue % 360 : hue % 360 + 360;
		return Color.getHSBColor(hue / 360f, saturation / 255f, brightness / 255f);
	}

	public static Color darken(Color primary, int level) {
		return blend(BACKGROUND, primary, levels[level]);
	}

	public static Color blend(Color c1, Color c2, double fac) {
		if (c1.equals(c2))
			return c1;
		double fac1 = 1 - fac;

		int r = (int) Math.round(fac1 * c1.getRed() + fac * c2.getRed());
		int g = (int) Math.round(fac1 * c1.getGreen() + fac * c2.getGreen());
		int b = (int) Math.round(fac1 * c1.getBlue() + fac * c2.getBlue());

		return new Color(r, g, b);
	}

}
