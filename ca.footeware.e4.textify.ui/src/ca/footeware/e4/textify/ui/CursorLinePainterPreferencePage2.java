/**
 * 
 */
package ca.footeware.e4.textify.ui;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;

import com.opcoach.e4.preferences.ScopedPreferenceStore;

/**
 * 
 */
public class CursorLinePainterPreferencePage2 extends FieldEditorPreferencePage {

	/**
	 * Constructor.
	 */
	public CursorLinePainterPreferencePage2() {
		super("Current Line",
				ImageDescriptor.createFromFile(CursorLinePainterPreferencePage2.class, "/icons/highlight.png"),
				FieldEditorPreferencePage.GRID);
		super.setDescription("Choose whether or not to highlight the current line and set its color.");
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, "ca.footeware.e4.textify.ui"));
		getPreferenceStore().setDefault(Constants.CURSOR_LINE_PAINTER_PROPERTY_NAME, false);
		getPreferenceStore().setDefault(Constants.CURSOR_LINE_PAINTER_COLOR_PROPERTY_NAME, "80,80,80");
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(Constants.CURSOR_LINE_PAINTER_PROPERTY_NAME, "Highlight current line",
				getFieldEditorParent()));

		addField(new ColorFieldEditor(Constants.CURSOR_LINE_PAINTER_COLOR_PROPERTY_NAME, "Current line color",
				getFieldEditorParent()));
	}
}
