package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class IntegerPropertyDescriptor extends PropertyDescriptor implements
		VerifyListener {

	public IntegerPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new StringCellEditor(parent);
		Text ctrl = (Text) editor.getControl();
		ctrl.addVerifyListener(this);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

	public void verifyText(VerifyEvent e) {
		for (char c : e.text.toCharArray()) {
			if (!Character.isDigit(c)) {
				e.doit = false;
				return;
			}
		}
	}
}
