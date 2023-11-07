
package ca.footeware.e4.textify.ui.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

public class SaveHandler {

	@CanExecute
	public boolean canExecute() {
		return true;
	}

	@Execute
	public void execute() {
		System.out.println("save");
	}
}