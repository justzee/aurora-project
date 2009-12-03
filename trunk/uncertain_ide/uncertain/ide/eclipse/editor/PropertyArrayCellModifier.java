package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.action.IDirty;

public class PropertyArrayCellModifier implements ICellModifier {

	/**
	 * @param viewer
	 */
	protected IDirty mDirtyObject;
	protected TableViewer mViewer;

	public PropertyArrayCellModifier(TableViewer viewer, IDirty dirtyObject) {
		super();
        mViewer = viewer;
        mDirtyObject = dirtyObject;
	}

	public boolean canModify(Object element, String property) {
	    return true;
	}

	public Object getValue(Object element, String property) {
		if (element == null)
			return "";
		CompositeMap data = (CompositeMap) element;
		Object value = data.get(property);
		if (value == null) {
			value = "";
			data.put(property,value);
		}
		return value;
	}

	public void modify(Object element, String property, Object value) {
	
		TableItem item = (TableItem) element;
		Object o = item.getData();
		CompositeMap data = (CompositeMap) o;
	
		Object oldValue = data.get(property);
		if(oldValue == null)
			oldValue = "";
		if (oldValue == null || !oldValue.equals(value)) {
			data.put(property, value);
			markDirty();
			mViewer.refresh();
		}
		if(value == null ||value.equals("")){
			data.remove(property);
		}
	}

	private void markDirty() {
		mDirtyObject.setDirty(true);
	}

}