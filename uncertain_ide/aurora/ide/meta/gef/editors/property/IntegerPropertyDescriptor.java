package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class IntegerPropertyDescriptor extends PropertyDescriptor {
	private int min = 0, max = Integer.MAX_VALUE, step1 = 5, step2 = 30;

	public IntegerPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public IntegerPropertyDescriptor(Object id, String displayName, int min,
			int max, int step1, int step2) {
		super(id, displayName);
		this.min = min;
		this.max = max;
		this.step1 = step1;
		this.step2 = step2;
	}

	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new IntegerCellEditor(parent, min, max, step1,
				step2);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

}
