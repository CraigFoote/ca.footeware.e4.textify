
package ca.footeware.e4.textify.ui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class ShowViewHandler {

	private static final String PART_ID = "ca.footeware.e4.textify.ui.part.textifyview";

	@Inject
	private EPartService partService;

	@CanExecute
	public boolean canExecute() {
		return true;
	}

	@Execute
	public void execute() {
		MPart mPart = partService.findPart(PART_ID);
		if (mPart != null) {
			partService.showPart(mPart, PartState.VISIBLE);
		}
	}
}