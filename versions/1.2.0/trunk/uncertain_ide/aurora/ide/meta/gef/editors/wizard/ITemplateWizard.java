package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.jface.wizard.IWizard;

import aurora.ide.meta.gef.editors.models.ViewDiagram;

public interface ITemplateWizard extends IWizard {
	public void setViewDiagram(ViewDiagram viewDiagram);

	public ViewDiagram getViewDiagram();

}
