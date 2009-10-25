/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor.service;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import uncertain.ide.eclipse.action.IViewerDirty;
import uncertain.schema.editor.AttributeValue;

public class ServicePropertyCellModifier implements ICellModifier {

	public static final String[] PROPERTY_TO_UPDATE = { ServicePropertyEditor.COLUMN_VALUE };
	
	IViewerDirty mColumnViewerDirtyAction;
	// public PropertySheetCellModifier(TableViewer viewer) {
	// super();
	// mViewer = viewer;
	// }
	public ServicePropertyCellModifier(IViewerDirty columnViewerDirtyAction) {
		super();
		mColumnViewerDirtyAction = columnViewerDirtyAction;
	}



	public boolean canModify(Object element, String property) {
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
		return ServicePropertyEditor.COLUMN_VALUE.equals(property);
	}

	public Object getValue(Object element, String property) {
		AttributeValue av = (AttributeValue) element;
//		if(av.getAttribute().getLocalName().equals("name")){
//			return new Integer(0);
//		}

		if (ServicePropertyEditor.COLUMN_VALUE.equals(property))
			return av.getValueString();
		else {
			return av.getAttribute().getLocalName();
		}
	}

	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem) element;
		AttributeValue av = (AttributeValue) item.getData();
		String attributeName = av.getAttribute().getLocalName();
//		System.out.println("modify:"+attributeName);
		Object oldValue = av.getContainer().get(av.getAttribute().getLocalName());
		if (oldValue == null ||!oldValue.equals(value)) {
			av.getContainer().put(attributeName, value);
			// mViewer.update( item.getData(), null);
			markDirty();
			mColumnViewerDirtyAction.refresh();
		}

	}

	private void markDirty() {
		mColumnViewerDirtyAction.setDirty(true);
	}
}
