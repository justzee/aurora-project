/*
 * Created on 2009-8-13
 */
package uncertain.ide.eclipse.editor.sxsd;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.editor.FormPage;

import uncertain.composite.CompositeMap;
import uncertain.schema.editor.AttributeValue;

public class SxsdPropertyArrayCellModifier implements ICellModifier {

	/**
	 * @param viewer
	 */
	FormPage mformPage;

	public SxsdPropertyArrayCellModifier(TableViewer viewer, FormPage formPage) {
		super();
		mViewer = viewer;
		mformPage = formPage;
	}

	TableViewer mViewer;

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
		if (mformPage == null)
			return;
		((SxsdPage) mformPage).makeDirty();
	}

}
