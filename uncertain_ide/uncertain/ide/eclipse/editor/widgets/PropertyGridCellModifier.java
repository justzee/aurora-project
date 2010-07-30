package uncertain.ide.eclipse.editor.widgets;

import java.util.HashMap;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.celleditor.ICellEditor;
import uncertain.ide.eclipse.editor.CompositeMapViewer;

public class PropertyGridCellModifier implements ICellModifier {

	/**
	 * @param viewer
	 */
	protected CompositeMapViewer viewer;
	private HashMap property_editors = new HashMap();

	public PropertyGridCellModifier(CompositeMapViewer viewer) {
		super();
		this.viewer = viewer;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		if (element == null)
			return "";
		CompositeMap data = (CompositeMap) element;
		String value = data.getString(property);
		if (value == null) {
			value = "";
			data.put(property, value);
		}
		Object cellEditor_object = property_editors.get(property);
		if (cellEditor_object != null) {
			ICellEditor cellEditor = (ICellEditor) cellEditor_object;
			Object returnValue = cellEditor.valueToShow(value);
			return returnValue;
		}
		return value;
	}

	public void modify(Object element, String property, Object value) {
		Object cellEditor_object = property_editors.get(property);
		if (cellEditor_object != null) {
			// System.out.println("cellEditor_object:"+cellEditor_object);
			value = ((ICellEditor) cellEditor_object).getSelection();
		}

		TableItem item = (TableItem) element;
		Object o = item.getData();
		CompositeMap data = (CompositeMap) o;

		Object oldValue = data.get(property);
		if (oldValue == null)
			oldValue = "";
		if (oldValue == null || !oldValue.equals(value)) {
			data.put(property, value);
			viewer.refresh(true);
		}
		if (value == null || value.equals("")) {
			data.remove(property);
		}
	}

	public void addEditor(String property, ICellEditor cellEditor) {
		property_editors.put(property, cellEditor);
	}

	public void clear() throws Exception {
		Object[] editors = property_editors.values().toArray();
		// Don't valid the null grid properties.
		if (viewer.getInput() != null
				&& viewer.getInput().getChildsNotNull().size() > 0) {
			for (int i = 0; i < editors.length; i++) {
				ICellEditor ed = (ICellEditor) editors[i];
				ed.validValue(ed.getSelection());
			}
		}
		for (int i = 0; i < editors.length; i++) {
			ICellEditor ed = (ICellEditor) editors[i];
			ed.dispose();
		}
		property_editors.clear();

	}

	public ICellEditor getCellEditor(String property) {
		Object editor = property_editors.get(property);
		if (editor != null)
			return (ICellEditor) editor;
		return null;
	}

}