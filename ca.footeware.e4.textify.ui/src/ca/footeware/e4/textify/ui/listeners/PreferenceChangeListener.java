package ca.footeware.e4.textify.ui.listeners;

import java.util.Iterator;

import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import ca.footeware.e4.textify.ui.Constants;
import ca.footeware.e4.textify.ui.exceptions.FontException;
import ca.footeware.e4.textify.ui.parts.TextifyView;
import ca.footeware.e4.textify.ui.preferences.ColorUtils;
import ca.footeware.e4.textify.ui.preferences.FontUtils;

/**
 * Listen for preference changes and apply new settings to widgets.
 */
public final class PreferenceChangeListener implements IPropertyChangeListener {

	@Inject
	private Logger logger;
	private TextifyView textifyView;

	/**
	 * Constructor.
	 *
	 * @param textifyView {@link TextifyView}
	 */
	public PreferenceChangeListener(final TextifyView textifyView) {
		this.textifyView = textifyView;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		final String propertyName = event.getProperty();
		Object obj = event.getNewValue();
		if (obj instanceof String newValue) {
			switch (propertyName) {
			case Constants.BACKGROUND_PROPERTY_NAME:
				textifyView.setBackground(Boolean.parseBoolean(newValue));
				break;
			case Constants.CURSOR_LINE_PAINTER_COLOR_PROPERTY_NAME:
				RGB rgb = ColorUtils.convertToRGB(newValue);
				textifyView.setCursorLineBackgroundColor(rgb);
				break;
			case Constants.CURSOR_LINE_PAINTER_PROPERTY_NAME:
				if (textifyView.getViewer() instanceof ITextViewerExtension2 extension) {
					if ((Boolean.parseBoolean(newValue))) {
						extension.addPainter(textifyView.getCursorLinePainter());
					} else {
						extension.removePainter(textifyView.getCursorLinePainter());
					}
				}
				break;
			case Constants.FONT_PROPERTY_NAME:
				try {
					final FontData fontData = FontUtils.getFontData(newValue);
					textifyView.setFont(fontData);
				} catch (FontException e1) {
					logger.error("An error occurred getting font from preferences.", e1);
				}
				break;
			case Constants.LINE_NUMBER_PROPERTY_NAME:
				if (Boolean.parseBoolean(newValue)) {
					final LineNumberRulerColumn numbers = new LineNumberRulerColumn();
					numbers.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION));
					numbers.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
					textifyView.getRuler().addDecorator(0, numbers);
				} else {
					Iterator<IVerticalRulerColumn> iterator = textifyView.getRuler().getDecoratorIterator();
					int index = 0;
					while (iterator.hasNext()) {
						textifyView.getRuler().removeDecorator(index++);
					}
				}
				break;
			case Constants.WRAP_PROPERTY_NAME:
				textifyView.getViewer().getTextWidget().setWordWrap(Boolean.parseBoolean(newValue));
				break;
			default:
				throw new IllegalArgumentException("Unknown property: " + propertyName);
			}
		}
		textifyView.getViewer().getTextWidget().setFocus();
	}
}
