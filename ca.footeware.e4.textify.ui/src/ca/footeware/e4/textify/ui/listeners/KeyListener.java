package ca.footeware.e4.textify.ui.listeners;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import ca.footeware.e4.textify.ui.parts.TextifyView;

/**
 * Listens for and handles user key strokes.
 */
public class KeyListener extends KeyAdapter {

	private TextifyView textifyView;

	/**
	 * Constructor.
	 *
	 * @param textifyView {@link TextifyView}
	 */
	public KeyListener(TextifyView textifyView) {
		this.textifyView = textifyView;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if ((e.stateMask & SWT.CTRL) != 0) {
			final String pressed = Action.findKeyString(e.keyCode);
			if (pressed != null) {
				switch (pressed) {
				case "a":
					((ITextOperationTarget) textifyView.getViewer()).doOperation(ITextOperationTarget.SELECT_ALL);
					break;
				case "f":
					textifyView.getSearch().setVisible(true);
					break;
				case "n":
					textifyView.newFile();
					break;
				case "o":
					textifyView.openFile();
					break;
				case "p":
					((ITextOperationTarget) textifyView.getViewer()).doOperation(ITextOperationTarget.PRINT);
					break;
				case "s":
					textifyView.save();
					break;
				case "w":
					textifyView.getViewer().getTextWidget().getShell().close();
					break;
				default:
					// do nothing
				}
			}
		}
		super.keyReleased(e);
	}
}