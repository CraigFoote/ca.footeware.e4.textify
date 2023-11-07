/**
 * 
 */
package ca.footeware.e4.textify.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;

import com.opcoach.e4.preferences.ScopedPreferenceStore;

import ca.footeware.e4.textify.ui.Constants;

/**
 * 
 */
public class FontPreferencePage extends FieldEditorPreferencePage {

	/**
	 * Constructor.
	 */
	public FontPreferencePage() {
		super("Font", ImageDescriptor.createFromFile(FontPreferencePage.class, "/icons/fonts.png"),
				FieldEditorPreferencePage.GRID);
		super.setDescription("Select a display font.");
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, "ca.footeware.e4.textify.ui"));
		getPreferenceStore().setDefault(Constants.FONT_PROPERTY_NAME,
				FontUtils.getDisplayText(FontUtils.getDefaultFontData()));
	}

	@Override
	protected void createFieldEditors() {
		addField(new FontFieldEditor(Constants.FONT_PROPERTY_NAME, "", getFieldEditorParent()));
	}
}
