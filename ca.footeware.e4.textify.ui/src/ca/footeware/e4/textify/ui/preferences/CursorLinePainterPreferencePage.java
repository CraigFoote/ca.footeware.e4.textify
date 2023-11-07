/**
 *
 */
package ca.footeware.e4.textify.ui.preferences;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import ca.footeware.e4.textify.ui.Constants;

/**
 * Allows a user to specify whether or not to highlight the current line.
 */
public class CursorLinePainterPreferencePage extends PreferencePage {

	private Button checkbox;
	private ColorSelector colorSelector;

	/**
	 * Constructor.
	 */
	public CursorLinePainterPreferencePage() {
		super(Constants.CURSOR_LINE_PAINTER_PROPERTY_NAME);
		setDescription("Choose whether or not to highlight the current line.");
		ImageDescriptor descriptor = ImageDescriptor.createFromFile(getClass(), "/images/highlight.png");
		setImageDescriptor(descriptor);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).extendedMargins(25, 25, 25, 25).equalWidth(false)
				.applyTo(composite);

		IPreferenceStore preferenceStore = getPreferenceStore();

		checkbox = new Button(composite, SWT.CHECK);
		checkbox.setText("Highlight current line");

		boolean cursorLineProperty = preferenceStore.getBoolean(Constants.CURSOR_LINE_PAINTER_PROPERTY_NAME);
		checkbox.setSelection(cursorLineProperty);

		colorSelector = new ColorSelector(composite);
		String colorProperty = preferenceStore.getString(Constants.CURSOR_LINE_PAINTER_COLOR_PROPERTY_NAME);
		colorSelector.setColorValue(ColorUtils.convertToRGB(colorProperty));

		return composite;
	}

	@Override
	protected void performDefaults() {
		IPreferenceStore preferenceStore = getPreferenceStore();

		boolean defaultCursorLinePainterProperty = preferenceStore
				.getDefaultBoolean(Constants.CURSOR_LINE_PAINTER_PROPERTY_NAME);
		checkbox.setSelection(defaultCursorLinePainterProperty);

		String defaultColorProperty = preferenceStore
				.getDefaultString(Constants.CURSOR_LINE_PAINTER_COLOR_PROPERTY_NAME);
		colorSelector.setColorValue(ColorUtils.convertToRGB(defaultColorProperty));
	}

	@Override
	public boolean performOk() {
		IPreferenceStore preferenceStore = getPreferenceStore();
		preferenceStore.setValue(Constants.CURSOR_LINE_PAINTER_COLOR_PROPERTY_NAME,
				ColorUtils.convertToString(colorSelector.getColorValue()));
		preferenceStore.setValue(Constants.CURSOR_LINE_PAINTER_PROPERTY_NAME, checkbox.getSelection());
		return true;
	}
}
