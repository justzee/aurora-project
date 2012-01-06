package aurora.ide.meta.gef.editors.property;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

public abstract class EditDialog extends Dialog {

	public EditDialog(Shell parent) {
		super(parent);
	}

	public EditDialog(Shell parent, int style) {
		super(parent, style);
	}

	public abstract void setDialogEdiableObject(DialogEdiableObject obj);

	public abstract Object open();

}
