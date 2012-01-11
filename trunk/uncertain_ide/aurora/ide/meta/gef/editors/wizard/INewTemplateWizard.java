package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.ui.INewWizard;

import aurora.ide.meta.gef.editors.models.ViewDiagram;

public interface INewTemplateWizard extends INewWizard {
	public ViewDiagram getViewDiagram();
}
