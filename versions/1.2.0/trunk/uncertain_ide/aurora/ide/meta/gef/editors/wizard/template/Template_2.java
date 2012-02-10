package aurora.ide.meta.gef.editors.wizard.template;

import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.wizard.ITemplateWizard;
import aurora.ide.meta.gef.editors.wizard.TemplateWizard;

public class Template_2 extends TemplateWizard implements ITemplateWizard {

	private ViewDiagram viewDiagram;

	public ViewDiagram getViewDiagram() {
		return viewDiagram;
	}

	public void setViewDiagram(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
	}

}
