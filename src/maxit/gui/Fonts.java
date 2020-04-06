package maxit.gui;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import maxit.util.tuple.Pair;

public final class Fonts {

	private static final Map<String, Map<Integer, Map<Integer, Pair<Font, Map<Float, Font>>>>> CACHE = new HashMap<>();

	private Fonts() {}

	static Font get(String name, int style, float size) {
		int roundedSize = (int) Math.ceil(size);

		boolean adding = false;

		// @formatter:off
		Map<Integer, Map<Integer, Pair<Font, Map<Float, Font>>>> namedFont      = null;
		    Integer                                              roundedSizeObj = roundedSize;
		             Map<Integer, Pair<Font, Map<Float, Font>>>  sizedFont      = null;
		                 Integer                                 styleObj       = style;
		                          Pair<Font, Map<Float, Font>>   styledFont     = null;
		                               Font                      base           = null;
		                                     Map<Float, Font>    basedFont      = null;
		                                         Float           sizeObj        = size;
		                                                Font     font           = null;
		// @formatter:on

		// name
		namedFont = CACHE.get(name);
		adding = namedFont == null;
		if (adding) {
			namedFont = new HashMap<>();
			CACHE.put(name, namedFont);
		}

		// rounded size
		if (!adding) {
			sizedFont = namedFont.get(roundedSizeObj);
			adding = sizedFont == null;
		}
		if (adding) {
			sizedFont = new HashMap<>();
			namedFont.put(roundedSizeObj, sizedFont);
		}

		// style
		if (!adding) {
			styledFont = sizedFont.get(styleObj);
			adding = styledFont == null;
		}
		if (adding) {
			styledFont = new Pair<Font, Map<Float, Font>>(new Font(name, style, roundedSizeObj), new HashMap<>());
			sizedFont.put(styleObj, styledFont);
		}
		base = styledFont.first;
		basedFont = styledFont.second;

		// exact size
		if (!adding) {
			font = basedFont.get(sizeObj);
			adding = font == null;
		}
		if (adding) {
			font = base.deriveFont(size);
			basedFont.put(sizeObj, font);
		}

		return font;
	}

	static Font resizeFont(Font font, float size) {
		return getFont(font, font.getStyle(), size);
	}

	static Font restyleFont(Font font, int style) {
		return getFont(font, style, font.getSize2D());
	}

	static Font getFont(Font font, int style, float size) {
		return get(font.getName(), style, size);
	}

}
