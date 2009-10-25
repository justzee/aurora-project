/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor.sxsd;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.editor.FormPage;

import uncertain.ide.eclipse.editor.CategoryLabel;
import uncertain.ide.eclipse.editor.service.ServicePropertyEditor;
import uncertain.schema.editor.AttributeValue;

public class SxsdPropertyCellModifier implements ICellModifier {

	/**
	 * @param viewer
	 */
	FormPage mformPage;

	// public PropertySheetCellModifier(TableViewer viewer) {
	// super();
	// mViewer = viewer;
	// }
	public SxsdPropertyCellModifier(TableViewer viewer, FormPage formPage) {
		super();
		mViewer = viewer;
		mformPage = formPage;
	}

	public static final String[] PROPERTY_TO_UPDATE = { ServicePropertyEditor.COLUMN_VALUE };

	TableViewer mViewer;

	public boolean canModify(Object element, String property) {
		if (element instanceof CategoryLabel) {
			return false;
		}
		AttributeValue av = (AttributeValue) element;
		if(av.getAttribute().getLocalName().equals("xxx")){
			String[] selectItems = {"h","a"};
			ComboBoxCellEditor cce = new ComboBoxCellEditor(mViewer.getTable(),selectItems);
//			cce.setItems(selectItems);
			mViewer.setCellEditors(new CellEditor[] { null, cce});
		}
		else{
			mViewer.setCellEditors(new CellEditor[] { null, new TextCellEditor(mViewer.getTable()) });
		}
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
			markDirty();
			mViewer.refresh();
		}
		if(value == null ||value.equals("")){
			av.getContainer().remove(attributeName);
		}
	}

	private void markDirty() {
		if (mformPage == null)
			return;
		((SxsdPage) mformPage).makeDirty();
	}
}
