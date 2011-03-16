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

import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.LocaleMessage;

public abstract class AbstractTextCellEditor extends TextCellEditor implements
		ICellEditor {

	protected CellInfo cellProperties;
	protected String oldInput;
	public AbstractTextCellEditor(CellInfo cellProperties) {
		this.cellProperties = cellProperties;
	}

	public boolean validValue(String value) {
		if(cellProperties.isRequired() && (value == null || value.equals(""))){
			String message = "<"+cellProperties.getColumnName()+"> "+LocaleMessage.getString("field")+LocaleMessage.getString("is.required");
			setErrorMessage(message);
			getCellControl().setFocus();
			return false;
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
		Table parent = cellProperties.getTable();
		createCellEditor(parent);
		if (cellProperties.isRequired()) {
			getCellControl().setBackground(bg);
		}
		if(isTableItemEditor()){
			SetSelection(cellProperties.getRecord().getString(cellProperties.getColumnName()));
		}
		addCellListener();
		oldInput = getSelection();
	}
	
	protected void addCellListener() {
		if(!isTableItemEditor())
			return ;
		this.addListener(new ICellEditorListener() {

			public void editorValueChanged(boolean oldValidState,
					boolean newValidState) {
				cellProperties.getTableViewer().refresh(true);
				String input = getSelection();
				 if(validValue(input)){
					 oldInput = input;
				 }else{
					 CustomDialog.showErrorMessageBox(getErrorMessage());
					 SetSelection(oldInput);
				 }
			}

			public void cancelEditor() {
			}

			public void applyEditorValue() {
				String dataValue = getSelection();
				cellProperties.getRecord().put(cellProperties.getColumnName(), dataValue);
			}
		});
		getCellControl().addFocusListener(new FocusListener() {
			
			public void focusLost(FocusEvent e) {
				if(isTableItemEditor()){
					fillTableCellEditor(cellProperties.getTableItem());
				}
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
	private boolean isTableItemEditor(){
		return cellProperties.getTableItem() != null;
	}
	
}
