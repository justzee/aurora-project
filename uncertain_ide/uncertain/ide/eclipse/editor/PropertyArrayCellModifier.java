package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.action.IViewerDirty;

public class PropertyArrayCellModifier implements ICellModifier {

	/**
	 * @param viewer
	 */
	protected IViewerDirty viewer;

	public PropertyArrayCellModifier(IViewerDirty viewer) {
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
			viewer.refresh(true);
		}
		if(value == null ||value.equals("")){
			data.remove(property);
		}
	}
}