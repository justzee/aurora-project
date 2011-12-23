package aurora.ide.meta.gef.editors.models.commands;

import aurora.ide.meta.gef.editors.models.Form;

public class BindFormCommand extends DropBMCommand {
	private Form form;

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public void execute() {
		this.createQueryDataset();
		this.fillForm(form);
	}
}
