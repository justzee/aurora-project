/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import uncertain.ide.eclipse.editor.CategoryLabel;
import uncertain.ide.eclipse.editor.ICategoryContainer;
import uncertain.schema.editor.AttributeValue;

public class PropertyHashCellModifier implements ICellModifier {

	public static final String[] PROPERTY_TO_UPDATE = { PropertyHashViewer.COLUMN_VALUE };
	
	ICategoryContainer viewer;
	// public PropertySheetCellModifier(TableViewer viewer) {
	// super();
	// mViewer = viewer;
	// }
	CellEditor cellEditor;
	public PropertyHashCellModifier(ICategoryContainer viewer) {
		super();
		this.viewer = viewer;
	}



	public boolean canModify(Object element, String property) {
		//如果是标签组标签，就不可修改
		if (element instanceof CategoryLabel) {
			return false;
		}
		return PropertyHashViewer.COLUMN_VALUE.equals(property);
	}

	public Object getValue(Object element, String property) {
//		System.out.println("getValue...");
		AttributeValue av = (AttributeValue) element;
//		if(av.getAttribute().getLocalName().equals("name")){
//			return new Integer(0);
//		}

		if (PropertyHashViewer.COLUMN_VALUE.equals(property))
			return av.getValueString();
		else {
			return av.getAttribute().getLocalName();
		}
	}

	public void modify(Object element, String property, Object value) {
//		System.out.println("modify....");
		TableItem item = (TableItem) element;
		AttributeValue av = (AttributeValue) item.getData();
		//如果是标签组标签，就不可修改
		if (av instanceof CategoryLabel) {
			return ;
		}
		String attributeName = av.getAttribute().getLocalName();
//		System.out.println("modify:"+attributeName);
		Object oldValue = av.getContainer().get(av.getAttribute().getLocalName());
		
		if((oldValue==null ||oldValue.equals(""))&&(value==null ||value.equals(""))){
			return;
		}
		
		if (oldValue == null ||(oldValue != null && !oldValue.equals(value))) {
			av.getContainer().put(attributeName, value);
			viewer.refresh(true);
		}
		if(value == null ||value.equals("")){
			av.getContainer().remove(attributeName);
		}
	}
}
