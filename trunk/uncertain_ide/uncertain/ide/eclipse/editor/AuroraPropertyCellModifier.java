/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import uncertain.ide.eclipse.action.IPropertyCategory;
import uncertain.schema.editor.AttributeValue;

public class AuroraPropertyCellModifier implements ICellModifier {

	public static final String[] PROPERTY_TO_UPDATE = { AuroraPropertyEditor.COLUMN_VALUE };
	
	IPropertyCategory viewer;
	// public PropertySheetCellModifier(TableViewer viewer) {
	// super();
	// mViewer = viewer;
	// }
	public AuroraPropertyCellModifier(IPropertyCategory viewer) {
		super();
		this.viewer = viewer;
	}



	public boolean canModify(Object element, String property) {
		//如果是标签组标签，就不可修改
		if (element instanceof CategoryLabel) {
			return false;
		}
		AttributeValue av = (AttributeValue) element;
//		if(av.getAttribute().getLocalName().equals("xxx")){
//			String[] selectItems = {"h","a"};
//			ComboBoxCellEditor cce = new ComboBoxCellEditor(mColumnViewerDirtyAction.getObject().getTable(),selectItems);
////			cce.setItems(selectItems);
//			mViewer.setCellEditors(new CellEditor[] { null, cce});
//		}
//		else{
//			mViewer.setCellEditors(new CellEditor[] { null, new TextCellEditor(mViewer.getTable()) });
//		}
		return AuroraPropertyEditor.COLUMN_VALUE.equals(property);
	}

	public Object getValue(Object element, String property) {
		AttributeValue av = (AttributeValue) element;
//		if(av.getAttribute().getLocalName().equals("name")){
//			return new Integer(0);
//		}

		if (AuroraPropertyEditor.COLUMN_VALUE.equals(property))
			return av.getValueString();
		else {
			return av.getAttribute().getLocalName();
		}
	}

	public void modify(Object element, String property, Object value) {
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
