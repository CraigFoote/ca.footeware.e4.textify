/**
 *
 */
package ca.footeware.e4.textify.ui.preferences;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import ca.footeware.e4.textify.ui.Constants;

/**
 *
 */
public class WrapPreferencePage extends PreferencePage {

	private Button button;

	/**
	 * Constructor.
	 */
	public WrapPreferencePage() {
		super(Constants.WRAP_PROPERTY_NAME);
		setDescription("Choose whether or not to wrap text at window width.");
		ImageDescriptor descriptor = ImageDescriptor.createFromFile(getClass(), "/images/wrap.png");
		setImageDescriptor(descriptor);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).extendedMargins(25, 25, 25, 25).equalWidth(false)
				.applyTo(composite);

		IPreferenceStore preferenceStore = getPreferenceStore();

		button = new Button(composite, SWT.CHECK);
		button.setText("Wrap Text");

		boolean wrapProperty = preferenceStore.getBoolean(Constants.WRAP_PROPERTY_NAME);
		button.setSelection(wrapProperty);

		return composite;
	}

	@Override
	protected void performDefaults() {
		IPreferenceStore preferenceStore = getPreferenceStore();
		boolean defaultProperty = preferenceStore.getDefaultBoolean(Constants.WRAP_PROPERTY_NAME);
		button.setSelection(defaultProperty);
	}

	@Override
	public boolean performOk() {
		IPreferenceStore preferenceStore = getPreferenceStore();
		preferenceStore.setValue(Constants.WRAP_PROPERTY_NAME, button.getSelection());
		return true;
	}
}
