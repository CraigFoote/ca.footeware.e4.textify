/**
 *
 */
package ca.footeware.e4.textify.ui.preferences;

import org.eclipse.swt.graphics.RGB;

/**
 * Utility class to deal with {@link RGB} and their hexidecimal representation.
 */
public class ColorUtils {

	/**
	 * Convert an <code>RGB</code> to a hexidecimal string.
	 *
	 * @param rgb {@link RGB}
	 * @return {@link String}
	 */
	public static String convertToString(RGB rgb) {
		StringBuilder builder = new StringBuilder(8);
		builder.append(String.valueOf(rgb.red));
		builder.append(",");
		builder.append(String.valueOf(rgb.green));
		builder.append(",");
		builder.append(String.valueOf(rgb.blue));
		return builder.toString();
	}

	/**
	 * Convert a hexidecimal string to a <code>RGB</code>.
	 *
	 * @param hexCode {@link String}
	 * @return {@link RGB}
	 */
	public static RGB convertToRGB(String hexCode) {
		String[] split = hexCode.split(",");
		int red = Integer.parseInt(split[0]);
		int green = Integer.parseInt(split[1]);
		int blue = Integer.parseInt(split[2]);
		return new RGB(red, green, blue);
	}

	/**
	 * Hidden constructor, use static methods.
	 */
	private ColorUtils() {
	}
}
