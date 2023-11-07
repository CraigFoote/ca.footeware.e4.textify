package ca.footeware.e4.textify.ui.parts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.FillLayoutFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.CursorLinePainter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;

import com.opcoach.e4.preferences.ScopedPreferenceStore;

import ca.footeware.e4.textify.ui.Constants;
import ca.footeware.e4.textify.ui.exceptions.FontException;
import ca.footeware.e4.textify.ui.listeners.KeyListener;
import ca.footeware.e4.textify.ui.listeners.PreferenceChangeListener;
import ca.footeware.e4.textify.ui.preferences.ColorUtils;
import ca.footeware.e4.textify.ui.preferences.FontUtils;
import ca.footeware.e4.textify.ui.providers.ImageProvider;
import ca.footeware.e4.textify.ui.search.SearchBar;

public class TextifyView {

	private static final String SAVE_PROMPT = "The text has been modified. Would you like to save it?";
	private File currentFile;
	private CursorLinePainter cursorLinePainter;
	private Color cursorLinePainterColor;
	private Font font;
	private ImageProvider imageProvider;
	@Inject
	private Logger logger;
	private ScopedPreferenceStore preferenceStore;
	private TextPresentation presentation;
	private IPropertyChangeListener propertyChangeListener;
	private CompositeRuler ruler;
	private SearchBar search;
	private boolean textChanged = false;
	private ISourceViewer viewer;

	/**
	 * Check if file can be opened.
	 *
	 * @param file {@link File}
	 * @throws IOException              if mimetype cannot be determined
	 * @throws IllegalArgumentException if file cannot be opened as text
	 */
	private void checkFile(File file) throws IOException, IllegalArgumentException {
		// check file exists
		if (!file.exists()) {
			throw new IllegalArgumentException("File does not exist:\n" + file.toPath());
		}
		// check mimetype
		String mimeType = Files.probeContentType(file.toPath());
		if (!isText(mimeType)) {
			throw new IllegalArgumentException("File is not text: " + mimeType);
		}
		// check charset
		Charset charset = Charset.defaultCharset();
		if (!charset.equals(StandardCharsets.UTF_8)) {
			throw new IllegalArgumentException("File is not UTF-8: " + charset);
		}
	}

	/**
	 * Clears text widget and sets appropriate labels.
	 */
	private void clear() {
		viewer.getDocument().set("");
		currentFile = null;
		textChanged = false;
//		getStatusLineManager().setMessage("");
//		getShell().setText(Constants.APP_NAME);
	}

	public boolean close() {
		if (textChanged) {
			final MessageBox box = new MessageBox(Display.getDefault().getActiveShell(),
					SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
			box.setText("Save");
			box.setMessage("Text has been modified. Would you like to save it before closing?");
			int response = box.open();
			if (response == SWT.CANCEL) {
				return false;
			} else if (response == SWT.NO) {
				return true;
			} else {
				if (!save()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Configure preferences using the {@link PreferenceProvider}.
	 */
	private void configurePreferences() {
		preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "ca.footeware.e4.textify.ui");
		preferenceStore.addPropertyChangeListener(propertyChangeListener);
//		PreferenceProvider preferenceProvider = new PreferenceProvider();
//		preferenceManager = preferenceProvider.getPreferenceManager();
//		preferenceStore = preferenceProvider.getPreferenceStore();
//		preferenceStore.addPropertyChangeListener(propertyChangeListener);
	}

	/**
	 * Creates the context menu
	 */
	protected void createContextMenu() {
		MenuManager contextMenu = new MenuManager("#ViewerMenu");
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(this::fillContextMenu);
		Menu menu = contextMenu.createContextMenu(viewer.getTextWidget());
		viewer.getTextWidget().setMenu(menu);
	}

	@PostConstruct
	public void createPartControl(Composite parent, IApplicationContext iac) {
		final Composite container = new Composite(parent, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(container);
		GridLayoutFactory.swtDefaults().applyTo(container);
		imageProvider = new ImageProvider();
		createViewer(container);

		final TextifyView finalTextify = this;
		Display.getDefault().asyncExec(() -> {
			createContextMenu();
			configurePreferences();
			initWidgets();
			search = new SearchBar(container, imageProvider, finalTextify);
			search.setVisible(false);
			GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).exclude(true)
					.applyTo(search.getControl());
			Map<String, Object> arguments = iac.getArguments();
			handleCliArgs(arguments);
			viewer.getTextWidget().setFocus();
		});
	}

	/**
	 * Create the {@link TextViewer}.
	 *
	 * @param parent {@link Composite}
	 */
	private void createViewer(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).hint(640, 480).applyTo(container);
		FillLayoutFactory.fillDefaults().applyTo(container);

		// viewer with ruler based on prefs
		ruler = new CompositeRuler();
		viewer = new SourceViewer(container, ruler, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setDocument(new Document());
		presentation = new TextPresentation();

		// property change listener
		propertyChangeListener = new PreferenceChangeListener(this);

		// key listener
		viewer.getTextWidget().addKeyListener(new KeyListener(this));

		// text listener
		viewer.addTextListener(event -> {
			textChanged = true;
			// set shell title
//			getShell().setText("* " + getShell().getText().replaceFirst("\\* ", ""));
		});

		viewer.getTextWidget().setBackgroundImage(imageProvider.getBackgroundImage());
	}

	/**
	 * Disposes the images and font and removes preferences listener.
	 */
	@PreDestroy
	private void dispose() {
		preferenceStore.removePropertyChangeListener(propertyChangeListener);
		if (cursorLinePainterColor != null && !cursorLinePainterColor.isDisposed()) {
			cursorLinePainterColor.dispose();
		}
		if (cursorLinePainter != null) {
			cursorLinePainter.dispose();
		}
		if (font != null && !font.isDisposed()) {
			font.dispose();
		}
		if (imageProvider != null) {
			imageProvider.dispose();
		}
	}

	/**
	 * Fill dynamic context menu.
	 *
	 * @param contextMenu {@link IMenuManager}
	 */
	protected void fillContextMenu(IMenuManager contextMenu) {
		// Cut
		ImageDescriptor descriptor = ImageDescriptor.createFromFile(getClass(), "/icons/cut-16.png");
		contextMenu.add(new Action("Cut", descriptor) {
			@Override
			public void run() {
				((ITextOperationTarget) viewer).doOperation(ITextOperationTarget.CUT);
			}
		});
		// Copy
		descriptor = ImageDescriptor.createFromFile(getClass(), "/icons/copy-16.png");
		contextMenu.add(new Action("Copy", descriptor) {
			@Override
			public void run() {
				((ITextOperationTarget) viewer).doOperation(ITextOperationTarget.COPY);
			}
		});
		// Paste
		descriptor = ImageDescriptor.createFromFile(getClass(), "/icons/paste-16.png");
		contextMenu.add(new Action("Paste", descriptor) {
			@Override
			public void run() {
				((ITextOperationTarget) viewer).doOperation(ITextOperationTarget.PASTE);
			}
		});
	}

	/**
	 * Return the line painter that highlights the current line.
	 *
	 * @return {@link IPainter}
	 */
	public IPainter getCursorLinePainter() {
		return cursorLinePainter;
	}

	/**
	 * @return the preferenceStore
	 */
	public ScopedPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	public TextPresentation getPresentation() {
		return presentation;
	}

	/**
	 * @return the ruler
	 */
	public CompositeRuler getRuler() {
		return ruler;
	}

	/**
	 * @return the search
	 */
	public SearchBar getSearch() {
		return search;
	}

	/**
	 * @return the viewer
	 */
	public ISourceViewer getViewer() {
		return viewer;
	}

	/**
	 * Handle command line arguments.
	 * 
	 * @param argMap
	 */
	private void handleCliArgs(Map<String, Object> argMap) {
		// handle cli arg
		if (argMap == null) {
			return;
		}
		Object obj = argMap.get(IApplicationContext.APPLICATION_ARGS);
		if (obj == null) {
			return;
		}
		if (obj instanceof String[] args) {
			if (args.length > 1) {
				showError("Expected at most one argument, a file, but received: " + args.length, null);
			}
			if (args.length == 1) {
				// file received
				String message = "File received: " + args[0];
				logger.info(message);
				final File file = new File(args[0]);
				if (!file.exists()) {
					try {
						logger.info("File does not exist - creating it.");
						final boolean created = file.createNewFile();
						if (!created) {
							showError("Unknown error creating file.", null);
						} else {
							loadFile(file);
						}
						logger.info("File created - loading.");
					} catch (IOException e1) {
						showError("An error occurred loading the file", e1);
					}
				} else {
					logger.info("Loading file.");
					loadFile(file);
				}
			}
		}
	}

	/**
	 * Hides the cursor line.
	 */
	private void hideCursorLine() {
		if (cursorLinePainter != null && viewer instanceof ITextViewerExtension2 extension) {
			extension.removePainter(cursorLinePainter);
			cursorLinePainter.deactivate(true);
			cursorLinePainter.dispose();
			cursorLinePainter = null;
		}
	}

	/**
	 * Initialize appropriate widgets to their value in preferences.
	 */
	private void initWidgets() {
		// background image
		final boolean backgroundProperty = preferenceStore.getBoolean(Constants.BACKGROUND_PROPERTY_NAME);
		setBackground(backgroundProperty);

		// highlight current (caret) line
		final boolean cursorLineBackgroundProperty = preferenceStore
				.getBoolean(Constants.CURSOR_LINE_PAINTER_PROPERTY_NAME);
		cursorLinePainter = new CursorLinePainter(viewer);
		String colorCodes = preferenceStore.getString(Constants.CURSOR_LINE_PAINTER_COLOR_PROPERTY_NAME);
		if (colorCodes == null || colorCodes.isEmpty()) {
			colorCodes = "80,80,80";
		}
		RGB rgb = ColorUtils.convertToRGB(colorCodes);
		if (cursorLinePainterColor != null && !cursorLinePainterColor.isDisposed()) {
			cursorLinePainterColor.dispose();
		}
		cursorLinePainterColor = new Color(rgb);
		cursorLinePainter.deactivate(true);
		cursorLinePainter.setHighlightColor(cursorLinePainterColor);
		if (cursorLineBackgroundProperty) {
			ITextViewerExtension2 extension = (ITextViewerExtension2) viewer;
			extension.addPainter(cursorLinePainter);
			cursorLinePainter.paint(IPainter.CONFIGURATION);
		}

		// set text wrap
		final boolean wrapProperty = preferenceStore.getBoolean(Constants.WRAP_PROPERTY_NAME);
		viewer.getTextWidget().setWordWrap(wrapProperty);

		// set text font
		final String fontProperty = preferenceStore.getString(Constants.FONT_PROPERTY_NAME);
		if (fontProperty != null && !fontProperty.isEmpty()) {
			try {
				FontData fontData = FontUtils.getFontData(fontProperty);
				font = new Font(Display.getDefault(), fontData);
				viewer.getTextWidget().setFont(font);
				ruler.setFont(font);
				ruler.relayout();
			} catch (FontException e) {
				showError("An error occurred getting font from preferences.", e);
			}
		}

		// set line numbers
		if (preferenceStore.getBoolean(Constants.LINE_NUMBER_PROPERTY_NAME)) {
			final LineNumberRulerColumn numbers = new LineNumberRulerColumn();
			numbers.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION));
			numbers.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
			ruler.addDecorator(0, numbers);
		} else {
			// remove all decorators including highlighter
			final Iterator<IVerticalRulerColumn> iterator = ruler.getDecoratorIterator();
			int index = 0;
			while (iterator.hasNext()) {
				ruler.removeDecorator(index++);
			}
		}
	}

	/**
	 * Determines if provided mimeType is text-based.
	 *
	 * @param mimeType {@link String}
	 * @return boolean true if mimeType indicates text-based
	 */
	private boolean isText(String mimeType) {
		return mimeType == null || mimeType.startsWith("text") || mimeType.contains("xml") || mimeType.contains("json")
				|| mimeType.equals("audio/mpegurl") || mimeType.contains("x-sh");
	}

	/**
	 * Load the contents of the provided file into the text widget.
	 *
	 * @param file {@link File}
	 */
	private void loadFile(File file) {
		try {
			checkFile(file);
			// load contents of file
			final List<String> allLines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
			final StringBuilder builder = new StringBuilder();
			for (String line : allLines) {
				builder.append(line);
				builder.append(System.lineSeparator());
			}
			// set in text widget
			viewer.getDocument().set(builder.toString());
			currentFile = file;
			textChanged = false;
//			getStatusLineManager().setMessage(file.getAbsolutePath());
//			getShell().setText(file.getName());
			viewer.getTextWidget().setFocus();
		} catch (IOException | IllegalArgumentException e) {
			showError("An error occurred loading the file.", e);
		}
	}

	/**
	 * Respond to the user pressing the New button.
	 */
	public void newFile() {
		if (textChanged) {
			final MessageBox box = new MessageBox(Display.getDefault().getActiveShell(),
					SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
			box.setText("Save");
			box.setMessage(SAVE_PROMPT);
			int result = box.open();
			if (result == SWT.NO || (result == SWT.YES && save())) {
				clear();
			}
		} else {
			clear();
		}
	}

	/**
	 * Open a file.
	 */
	public void openFile() {
		if (textChanged) {
			// prompt user to save
			final MessageBox box = new MessageBox(Display.getDefault().getActiveShell(),
					SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
			box.setText("Save");
			box.setMessage(SAVE_PROMPT);
			int result = box.open();
			if (result == SWT.CANCEL) {
				return;
			} else if (result == SWT.YES) {
				save();
			}
		}
		// proceed with opening a file
		final FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
		final String path = dialog.open();
		if (path != null && !path.isEmpty()) {
			File file = new File(path);
			loadFile(file);
		}
	}

	/**
	 * Prompt user to overwrite file
	 *
	 * @return int one of SWT.YES, SWT.NO or SWT.CANCEL
	 */
	private int promptForOverwrite() {
		final MessageBox box = new MessageBox(Display.getDefault().getActiveShell(),
				SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
		box.setText("Overwrite");
		box.setMessage("File exists. Would you like to overwrite it?");
		return box.open();
	}

	/**
	 * Save text changes to file.
	 *
	 * @return boolean true if text was saved to file
	 */
	public boolean save() {
		// save text to file
		File file = null;
		// pessimistic view on writing
		boolean write = false;
		if (currentFile != null) {
			file = currentFile;
			write = true;
			// good to go
		} else {
			// prompt for filename and location
			final FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
			final String chosenPath = fileDialog.open();
			// filePath will be null if a file was not chosen or name entered
			if (chosenPath != null) {
				file = new File(chosenPath);
				if (file.exists()) {
					// prompt for overwrite
					final int overwrite = promptForOverwrite();
					if (overwrite == SWT.CANCEL) {
						return false;
					} else if (overwrite == SWT.YES) {
						write = true;
					}
				} else {
					write = true;
				}
			}
		}
		// do we have everything we need to write file?
		if (write) {
			return write(file);
		}
		return false;
	}

	/**
	 * Prompt for filename and location and save text to file.
	 */
	private void saveAs() {
		// save text to file
		File file = null;
		// pessimistic view on writing
		boolean write = false;
		// prompt for filename and location
		final FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
		final String chosenPath = fileDialog.open();
		// filePath will be null if a file was not chosen or name entered
		if (chosenPath != null) {
			file = new File(chosenPath);
			if (file.exists()) {
				// prompt for overwrite
				final int overwrite = promptForOverwrite();
				if (overwrite == SWT.CANCEL) {
					return;
				} else if (overwrite == SWT.YES) {
					write = true;
				}
			} else {
				write = true;
			}
		}
		// do we have everything we need to write file?
		if (write) {
			write(file);
		}
	}

	/**
	 * Set a background image in the viewer.
	 *
	 * @param show
	 */
	public void setBackground(boolean show) {
		if (show) {
			viewer.getTextWidget().setBackgroundImage(imageProvider.getBackgroundImage());
		} else {
			viewer.getTextWidget().setBackgroundImage(null);
		}
	}

	/**
	 * Set the background color of the line with the cursor.
	 *
	 * @param rgb {@link RGB}
	 */
	public void setCursorLineBackgroundColor(RGB rgb) {
		if (cursorLinePainterColor != null && !cursorLinePainterColor.isDisposed()) {
			hideCursorLine();
		}
		cursorLinePainterColor = new Color(rgb);
		showCursorLine();
	}

	@Focus
	public void setFocus() {
		viewer.getTextWidget().setFocus();
	}

	/**
	 * Set the viewer and its ruler to the provided font.
	 *
	 * @param fontData {@link FontData}
	 */
	public void setFont(FontData fontData) {
		final Font newFont = new Font(Display.getDefault(), fontData);
		viewer.getTextWidget().setFont(newFont);
		if (this.font != null && !this.font.isDisposed()) {
			this.font.dispose();
		}
		this.font = newFont;
		ruler.setFont(newFont);
		ruler.relayout();
	}

	/**
	 * Shows the cursor line.
	 */
	private void showCursorLine() {
		if (cursorLinePainter == null && viewer instanceof ITextViewerExtension2 extension) {
			cursorLinePainter = new CursorLinePainter(viewer);
			cursorLinePainter.setHighlightColor(cursorLinePainterColor);
			extension.addPainter(cursorLinePainter);
		}
	}

	/**
	 * Display an error message to the user.
	 *
	 * @param string {@link String}
	 */
	private void showError(String string, Exception e) {
		logger.error(string, e);
		MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", string);
	}

	/**
	 * Write file to disk and update UI.
	 *
	 * @param file {@link File}
	 * @return boolean true if file was written
	 */
	private boolean write(File file) {
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(viewer.getDocument().get());
			currentFile = file;
			textChanged = false;
//			getStatusLineManager().setMessage(file.getAbsolutePath());
//			getShell().setText(file.getName());
			return true;
		} catch (IOException e) {
			showError("An error occurred writing the file out to disk.", e);
		}
		return false;
	}
}
