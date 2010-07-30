package uncertain.ide.eclipse.celleditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.ITableViewer;
import uncertain.schema.Attribute;

public class StringTextCellEditor extends AbstractTextCellEditor {

	/**
	 * @param tableViewer
	 * @param property
	 * @param record it can be null in grid table
	 * @param item it can be null in grid table
	 */
	StringTextCellEditor(ITableViewer tableViewer, Attribute property,
			CompositeMap record, TableItem item) {
		super(tableViewer, property, record, item);
	}

	public void createCellEditor(Composite parent) {
		super.create(parent);

	}

}
