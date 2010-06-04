/**
 * 
 */
package uncertain.ide.eclipse.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import uncertain.composite.CompositeMap;
import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.IContainer;
import uncertain.schema.Attribute;
import uncertain.schema.Editor;

/**
 * @author linjinxiao
 * 
 */
public class BoolCellEditor extends CheckboxCellEditor implements ICellEditor {

	Button button;

	protected Editor editor;
	protected IContainer container;
	protected CompositeMap record;
	protected Attribute property;
	protected TableItem item;
	private boolean required = false;
	public BoolCellEditor(IContainer container, CompositeMap record,Attribute property,TableItem item) {
		this.container = container;
		this.record = record;
		this.property = property;
		this.item = item;
	}
	protected Control createControl(Composite parent) {
//		button = new Button(parent, SWT.CHECK);
//		return button;
		if(record !=null){
			button = new Button(parent, SWT.CHECK);
			return button;
		}
		else{
			return super.createControl(parent);
		}
	}
	
	public boolean validValue(String value) {
		boolean validValue = true;
		String errorMessage = "";
		if(required && value == null){
			validValue = false;
			errorMessage = "this field <"+property.getLocalName()+"> is required! can't be not null";
		}
		if(value != null && (!value.equals("true")) &&(!value.equals("false"))){
			validValue = false;
			errorMessage = "this field <"+property.getLocalName()+">  value must be 'true' or 'false' ! Can not be "+value+" !";
		}
		if(!validValue){
			Common.showErrorMessageBox(null, errorMessage);
			if(getCellControl() != null)
				getCellControl().setFocus();
			throw new IllegalArgumentException(errorMessage);
		}
		return true;
	}

	public Control getCellControl() {
		return super.getControl();
	}

	public String getSelection() {
		// System.out.println("button.getSelection():"+button.getSelection());
		if (button != null) {
			if (button.getSelection()) {
				return "true";
			} else
				return "false";
//			return record.getString(property.getLocalName());
		} else {
			if (super.getValue() == null)
				return null;
			return super.getValue().toString();
		}
	}
	public Object valueToShow(String value){
		Boolean showValue = null;
		if ("true".equals(value))
			showValue = new Boolean(true);
//		if ("false".equals(value))
		else
			showValue = new Boolean(false);
		return showValue;
	}

	public void SetSelection(String dataValue) {
		Boolean showValue = (Boolean)valueToShow(dataValue);
		if(button != null){
			button.setSelection(showValue.booleanValue());
		}else{
			super.setValue(showValue);
		}
	}

	public Control createCellControl(Composite parent) {
		return super.getControl();
	}

	public void dispose() {
		super.dispose();

	}
	public void init() {
		Table parent = ((TableViewer) container.getViewer()).getTable();
		createCellEditor(parent);
		Color bg = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
		if (property.getUse() != null && property.getUse().equals("required")) {
			required = true;
			if(getCellControl() != null)
				getCellControl().setBackground(bg);
		}
		if(record != null){
			SetSelection(record.getString(property.getLocalName()));
			addCellListener();
		}
	}

	private void addCellListener() {
		button.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
//				System.out.println("applyEditorValue");
				String dataValue = getSelection();
//				validValue(dataValue);
//				data.setValue(dataValue);
				record.put(property.getLocalName(), dataValue);
				container.refresh(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void createCellEditor(Composite parent) {
		super.create(parent);
	}

	public TableItem getTableItem() {
		return item;
	}

	public CellEditor getCellEditor() {
		return this;
	}
//	public LayoutData getLayoutData() {
//		LayoutData layoutData = super.getLayoutData();
//		if ((button == null) || button.isDisposed()) {
//			layoutData.minimumWidth = 60;
//		} else {
//			// make the comboBox 10 characters wide
//			GC gc = new GC(button);
//			layoutData.minimumWidth = (gc.getFontMetrics()
//					.getAverageCharWidth() * 10) + 10;
//			gc.dispose();
//		}
//		return layoutData;
//	}
}
