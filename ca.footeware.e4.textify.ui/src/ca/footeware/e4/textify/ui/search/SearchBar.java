/**
 *
 */
package ca.footeware.e4.textify.ui.search;

import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.footeware.e4.textify.ui.listeners.KeyListener;
import ca.footeware.e4.textify.ui.parts.TextifyView;
import ca.footeware.e4.textify.ui.providers.ImageProvider;

/**
 * Searches for bars and the loose women therein.
 */
public class SearchBar {

	private static final Color BG_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW);
	private static final Color FG_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
	private Button closeButton;
	private Composite container;
	@Inject
	private Logger logger;
	private Button matchCaseButton;
	private Text text;
	private TextifyView textifyView;
	private Button wholeWordButton;

	/**
	 * Constructor.
	 *
	 * @param parent        {@link Composite}
	 * @param imageProvider {@link ImageProvider}
	 * @param textifyView       {@link TextifyView}
	 */
	public SearchBar(Composite parent, ImageProvider imageProvider, TextifyView textifyView) {
		this.textifyView = textifyView;

		container = new Composite(parent, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(container);
		GridLayoutFactory.swtDefaults().numColumns(5).equalWidth(false).applyTo(container);

		// icon
		final Label searchLabel = new Label(container, SWT.NONE);
		searchLabel.setImage(imageProvider.getSearchImage());
		GridDataFactory.swtDefaults().applyTo(searchLabel);

		// text
		text = new Text(container, SWT.NONE);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(text);
		text.addKeyListener(new KeyListener(textifyView));
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// escape key only for this listener
				if (e.keyCode == 27) {
					setVisible(false);
				}
			}
		});
		text.addModifyListener(e -> search(text.getText()));

		// case sensitive checkbox
		matchCaseButton = new Button(container, SWT.CHECK);
		matchCaseButton.setText("Match Case");
		matchCaseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search(text.getText());
			}
		});

		// whole word checkbox
		wholeWordButton = new Button(container, SWT.CHECK);
		wholeWordButton.setText("Whole Word");
		wholeWordButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search(text.getText());
			}
		});

		// close button
		closeButton = new Button(container, SWT.PUSH);
		closeButton.setText("x");
		closeButton.setToolTipText("Close");
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setVisible(false);
			}
		});

		// viewer modified listener
		textifyView.getViewer().addTextListener(event -> {
			if (container.isVisible() && text.getCharCount() > 0) {
				search(text.getText());
			}
		});
	}

	/**
	 * Get the search bar widget.
	 *
	 * @return {@link Control}
	 */
	public Control getControl() {
		return container;
	}

	/**
	 * Search the textifyView viewer for the provided string.
	 *
	 * @param query {@link String}
	 */
	protected void search(String query) {
		boolean matchCase = matchCaseButton.getSelection();
		boolean wholeWord = wholeWordButton.getSelection();
		textifyView.getPresentation().clear();
		FindReplaceDocumentAdapter finder = new FindReplaceDocumentAdapter(textifyView.getViewer().getDocument());
		int startIndex = 0;
		final int docLength = textifyView.getViewer().getDocument().getLength();
		IRegion region;
		StyleRange styleRange;
		try {
			do {
				region = finder.find(startIndex, query, true, matchCase, wholeWord, false);
				if (region == null) {
					break;
				}
				startIndex = region.getOffset() + region.getLength();
				// create a new style
				styleRange = new StyleRange(region.getOffset(), region.getLength(), FG_COLOR, BG_COLOR);
				textifyView.getPresentation().addStyleRange(styleRange);
			} while (startIndex < docLength);
		} catch (BadLocationException e) {
			logger.error("An error occurred highlighting text.", e);
		}
		TextPresentation.applyTextPresentation(textifyView.getPresentation(), textifyView.getViewer().getTextWidget());
	}

	/**
	 * Set the visibility of the search bar to the provided boolean.
	 *
	 * @param visible boolean true will show the search bar, false will hide it
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			// init text widget
			final TextSelection selection = (TextSelection) textifyView.getViewer().getSelectionProvider().getSelection();
			if (selection != null && !selection.isEmpty()) {
				text.setText(selection.getText());
			}
		}

		// exclude if not visible
		GridData data = (GridData) container.getLayoutData();
		data.exclude = !visible;

		// show/hide search bar
		container.setVisible(visible);

		// clear all styles in viewer
		textifyView.getPresentation().clear();
		TextPresentation.applyTextPresentation(textifyView.getPresentation(), textifyView.getViewer().getTextWidget());

		// layout to accommodate new font
		container.getParent().pack(true);
		container.getParent().getParent().layout(true);

		// set focus, init text widget selection and do a fresh search
		if (visible) {
			text.setFocus();
			text.setSelection(0, text.getCharCount());
			search(text.getText());
		} else {
			textifyView.getViewer().getTextWidget().setFocus();
		}
	}
}
