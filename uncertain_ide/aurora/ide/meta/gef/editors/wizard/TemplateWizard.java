package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.jface.wizard.Wizard;

import aurora.ide.meta.gef.editors.models.ViewDiagram;

public abstract class TemplateWizard extends Wizard implements ITemplateWizard {

	public abstract void setViewDiagram(ViewDiagram viewDiagram);

	public abstract ViewDiagram getViewDiagram();

	/**
	 * @deprecated 在自定义模板向导中，此方法已失效
	 * */
	@Override
	public boolean performFinish() {
		return true;
	}

	/**
	 * @deprecated 在自定义模板向导中，此方法已失效
	 * */
	@Override
	public boolean canFinish() {
		return true;
	}

}
