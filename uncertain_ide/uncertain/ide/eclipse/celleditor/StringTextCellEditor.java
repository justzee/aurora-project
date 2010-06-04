package uncertain.ide.eclipse.celleditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.IContainer;
import uncertain.schema.Attribute;

public class StringTextCellEditor extends AbstractTextCellEditor {

	StringTextCellEditor(IContainer container, CompositeMap record,Attribute property,TableItem item) {
		super(container, record,property,item);
	}

	public void createCellEditor(Composite parent) {
		super.create(parent);

	}

}
