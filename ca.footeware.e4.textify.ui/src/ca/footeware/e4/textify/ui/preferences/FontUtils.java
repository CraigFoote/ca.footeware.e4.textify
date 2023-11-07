/**
 *
 */
package ca.footeware.e4.textify.ui.preferences;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import ca.footeware.e4.textify.ui.exceptions.FontException;

/**
 *
 */
public class FontUtils {

	public static FontData getDefaultFontData() {
		return new FontData("sans", 14, SWT.NORMAL);
	}

	public static String getDisplayText(FontData fontData) {
		if (fontData != null) {
			return fontData.getName() + " " + fontData.getHeight() + " " + getStyleName(fontData.getStyle());
		}
		return "Unknown";
	}

	public static FontData getFontData(String fontProperty) throws FontException {
		String[] split = fontProperty.split("\\|");
		if (split.length < 4) {
			throw new FontException("Expected 4+ fontdata properties, received: " + fontProperty);
		}
		String name = split[1];
		int height = (int) Double.parseDouble(split[2]);
		int style = Integer.parseInt(split[3]);
		return new FontData(name, height, style);
	}

	public static String getStyleName(int style) {
		switch (style) {
		case 0:
			return "Normal";
		case 1:
			return "Bold";
		case 2:
			return "Italic";
		case 3:
			return "Bold Italic";
		case 32:
			return "Oblique";
		case 33:
			return "Bold Oblique";
		default:
			return "Unknown";
		}
	}

	private FontUtils() {
	}
}
