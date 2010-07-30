/**
 * 
 */
package uncertain.ide.eclipse.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
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
import uncertain.ide.eclipse.editor.ITableViewer;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.schema.Attribute;
import uncertain.schema.Editor;

/**
 * @author linjinxiao
 * 
 */
public class BoolCellEditor extends CheckboxCellEditor implements ICellEditor {

	Button button;
	private boolean hasSelection = false;
	protected Editor editor;
	protected ITableViewer tableViewer;
	protected CompositeMap record;
	protected Attribute property;
	protected TableItem item;
	private boolean required = false;
	/**
	 * @param tableViewer
	 * @param property
	 * @param record it can be null in grid table
	 * @param item it can be null in grid table
	 */
	public BoolCellEditor(ITableViewer tableViewer, Attribute property,CompositeMap record,TableItem item) {
		this.tableViewer = tableViewer;
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
			CustomDialog.showErrorMessageBox(null, errorMessage);
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
			if(!hasSelection)
				return null;
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
//		if(dataValue == null)
//			return;
		if(dataValue == null)
			dataValue = "false";
		Boolean showValue = (Boolean)valueToShow(dataValue);
		if(button != null){
			hasSelection = true;
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
		Table parent = tableViewer.getViewer().getTable();
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
				if(!hasSelection)
					hasSelection = true;
				String dataValue = getSelection();
				record.put(property.getLocalName(), dataValue);
				tableViewer.refresh(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);

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
}
