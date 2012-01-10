package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class DialogPropertyDescriptor extends PropertyDescriptor {
	private Class<EditWizard> clazz;

	public DialogPropertyDescriptor(Object id, String displayName, Class clazz) {
		super(id, displayName);
		this.clazz = clazz;
		while (true) {
			if (clazz.equals(EditWizard.class))
				break;
			clazz = clazz.getSuperclass();
			if (clazz.equals(Object.class))
				throw new RuntimeException("Illegal Dialog class Type.");
		}
	}

	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new DialogCellEditor(parent, clazz);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

}
