/**
 * 
 */
package uncertain.ide.eclipse.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.eclipse.editor.ITableViewer;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.schema.Attribute;
import uncertain.schema.Editor;
import uncertain.schema.Enumeration;
import uncertain.schema.IType;
import uncertain.schema.Restriction;
import uncertain.schema.SimpleType;

/**
 * @author linjinxiao
 * 
 */
public class ComboxCellEditor extends ComboBoxCellEditor implements ICellEditor {

	String[] items;

	protected Editor editor;
	protected ITableViewer tableViewer;
	protected CompositeMap record;
	protected Attribute property;
	protected TableItem item;
	private boolean required;
	public ComboxCellEditor(ITableViewer tableViewer, Attribute property,CompositeMap record,TableItem item) {
		this.tableViewer = tableViewer;
		this.record = record;
		this.property = property;
		this.item = item;
	}

	public boolean validValue(String value) {
		boolean validResult = false;
		String selections = "";
		String errorMessage = "";
		for(int i=0;i<items.length;i++){
			String selection = items[i];
			selections += selection+",";
			if(value==null&&selection.equals(""))
				return true;
			if(value != null && value.equals(selection))
				return true;
		}
		if(!validResult){
			errorMessage = "the field <"+property.getLocalName()+"> value must be in '"+selections+"' !";
			CustomDialog.showErrorMessageBox(null, errorMessage);
			getCellControl().setFocus();
			throw new IllegalArgumentException(errorMessage);
		}
		return validResult;
	}

	public Control getCellControl() {
		return super.getControl();
	}

	public String getSelection() {
		Object value = getValue();
		Integer newInt = (Integer) value;
		if(newInt.intValue() == -1)
			return null;
		String dataValue = items[newInt.intValue()];
		return dataValue;
	}
	public Object valueToShow(String value){
		Integer showValue = null;
		for (int i = 0; i < items.length; i++) {
			if(value == null && items[i].equals("")){
				showValue = new Integer(i);
				break;
			}
			if (items[i].equals(value)) {
				showValue = new Integer(i);
				break;
			}
		}
		if(showValue == null)
			showValue = new Integer(-1);
//			showValue = (Integer)super.getValue();
		return showValue;
	}
	public void SetSelection(String value) {
		Integer showValue = (Integer)valueToShow(value);
		super.setValue(showValue);

	}

	public void init() {
		editor = getEditor();
		Color bg = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
		if (property.getUse() != null && property.getUse().equals("required")) {
			required = true;
			if(getCellControl() != null)
				getCellControl().setBackground(bg);
		}
		Table parent = tableViewer.getViewer().getTable();
		createCellEditor(parent);

		if (property.getUse() != null && property.getUse().equals("required")) {
			getCellControl().setBackground(bg);
		}
		if(record != null){
			SetSelection(record.getString(property.getLocalName()));
			addCellListener();
		}
	}

	private void addCellListener() {
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
				fillTableCellEditor(tableViewer.getViewer().getTable(),getTableItem());
				
			}
			
			public void focusGained(FocusEvent e) {
			}
		});
	}
	private String[] getCellItems() {
		String[] itmes = null;
		IType attributeType = null;
		if(editor !=null){
			attributeType = editor.getElementType();
		}else{
			QualifiedName typeQname = property.getTypeQName();
			attributeType = LoadSchemaManager.getSchemaManager().getType(typeQname);
		}
		if (attributeType != null && attributeType instanceof SimpleType) {
			SimpleType simpleType = (SimpleType) attributeType;
			Restriction rest = simpleType.getRestriction();
			if (rest != null) {
				Enumeration[] emus = rest.getEnumerations();
				if (emus != null) {
					if(required){
						itmes = new String[emus.length];
						for (int i = 0; i < emus.length; i++) {
							itmes[i] = emus[i].getValue();
							// System.out.println("items:"+itmes[i]);
						}
					}else{
						itmes = new String[emus.length+1];
						itmes[0] = ""; 
						for (int i = 0; i < emus.length; i++) {
							itmes[i+1] = emus[i].getValue();
							// System.out.println("items:"+itmes[i]);
						}
					}
				}
			}
		}
		return itmes;
	}

	public void createCellEditor(Composite parent) {
		items = getCellItems();
		super.setItems(items);
		super.create(parent);
		super.setValue(new Integer(-1));
	}

	public TableItem getTableItem() {
		return item;
	}
	private void fillTableCellEditor(Table table, TableItem item) {
		TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.setEditor(getCellControl(), item, 1);
	}

	public CellEditor getCellEditor() {
		return this;
	}
	public void dispose(){
		super.dispose();
	}
	private Editor getEditor(){
		QualifiedName editorName = property.getEditorQName();
		if (editorName == null)
			return null;
		Editor ed = LoadSchemaManager.getSchemaManager().getEditor(editorName);
		return ed;

	}
}
