package uncertain.ide.eclipse.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.ITableViewer;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.schema.Attribute;
import uncertain.schema.Editor;

public abstract class AbstractTextCellEditor extends TextCellEditor implements
		ICellEditor {

	protected Editor editor;
	protected ITableViewer tableViewer;
	protected Attribute property;
	protected CompositeMap record;
	protected TableItem item;
	private boolean required = false;
	
	/**
	 * @param tableViewer
	 * @param property
	 * @param record it can be null in grid table
	 * @param item it can be null in grid table
	 */
	public AbstractTextCellEditor(ITableViewer tableViewer,Attribute property,CompositeMap record,TableItem item) {
		this.tableViewer = tableViewer;
		this.record = record;
		this.property = property;
		this.item = item;
	}

	public boolean validValue(String value) {
		if(required && (value == null || value.equals(""))){
			String message = "this field <"+property.getLocalName()+"> is required! can't be not null";
			CustomDialog.showErrorMessageBox(null, message);
			getCellControl().setFocus();
			throw new IllegalArgumentException(message);
		}
		return true;
	}

	public Control getCellControl() {
		return super.getControl();
	}

	public String getSelection() {
		Object value = getValue();
		if(value == null)
			return null;
		return value.toString();
	}
	public Object valueToShow(String value){
		return value;
	}
	public void SetSelection(String value) {
		if(value != null)
			super.setValue(value);
	}

	public void dispose() {
		super.dispose();

	}

	public void init() {
		if(getCellControl()!=null){
			return;
		}
		Color bg = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
		Table parent = tableViewer.getViewer().getTable();
		createCellEditor(parent);
		if (property.getUse() != null && property.getUse().equals("required")) {
			required = true;
			getCellControl().setBackground(bg);
		}
		if(item != null){
			SetSelection(record.getString(property.getLocalName()));
			addCellListener();
		}
	}
	public TableItem getTableItem(){
		return item;
	}
	protected void addCellListener() {
		this.addListener(new ICellEditorListener() {

			public void editorValueChanged(boolean oldValidState,
					boolean newValidState) {
			}

			public void cancelEditor() {
			}

			public void applyEditorValue() {
				String dataValue = getSelection();
				record.put(property.getLocalName(), dataValue);
				tableViewer.refresh(true);
			}
		});
		getCellControl().addFocusListener(new FocusListener() {
			
			public void focusLost(FocusEvent e) {
				if(item!=null)
					fillTableCellEditor(getTableItem());
			
			}
			
			public void focusGained(FocusEvent e) {
			}
		});
	}
	protected void fillTableCellEditor(TableItem item) {
		TableEditor editor = new TableEditor(item.getParent());
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.setEditor(getCellControl(), item, 1);
	}
	protected boolean dependsOnExternalFocusListener() {
		return false;
	}
	public CellEditor getCellEditor(){
		return this;
	}
	
}
