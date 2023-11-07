/**
 * 
 */
package ca.footeware.e4.textify.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;

import com.opcoach.e4.preferences.ScopedPreferenceStore;

import ca.footeware.e4.textify.ui.Constants;

/**
 * 
 */
public class BackgroundPreferencePage extends FieldEditorPreferencePage {

	/**
	 * Constructor.
	 */
	public BackgroundPreferencePage() {
		super("Background",
				ImageDescriptor.createFromFile(BackgroundPreferencePage.class, "/icons/background-icon.png"),
				FieldEditorPreferencePage.GRID);
		super.setDescription("Choose whether or not to display the background.");
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, "ca.footeware.e4.textify.ui"));
		getPreferenceStore().setDefault(Constants.BACKGROUND_PROPERTY_NAME, false);
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(Constants.BACKGROUND_PROPERTY_NAME, "Display Background",
				getFieldEditorParent()));
	}
}
