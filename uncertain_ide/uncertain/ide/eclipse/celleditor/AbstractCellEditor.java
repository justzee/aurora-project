/**
 * 
 */
package uncertain.ide.eclipse.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import uncertain.ide.eclipse.editor.ICategoryContainer;
import uncertain.schema.Attribute;
import uncertain.schema.Editor;
import uncertain.schema.editor.AttributeValue;


/**
 * @author linjinxiao
 *
 */
public abstract class AbstractCellEditor implements ICellEditor{

	
	public CellEditor cellEditor;
	Editor editor;
	AbstractCellEditor(Editor editor,final ICategoryContainer viewer,final AttributeValue av){
		this.editor = editor;
		Table  parent = ((TableViewer)viewer.getViewer()).getTable();
//		cellEditor = createCellEditor(parent);
		createCellEditor(parent);
		Attribute attr = av.getAttribute();
		Color bg = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
		if (attr.getUse() != null && attr.getUse().equals("required")){
			getCellControl().setBackground(bg);
		}
//		System.out.println("av.getValueString()...");
		SetSelection(av.getValueString());
		cellEditor.addListener(new ICellEditorListener() {
			
			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
				System.out.println("editorValueChanged");
			
			}
			public void cancelEditor() {
				System.out.println("cancelEditor");
			}
			public void applyEditorValue() {
				System.out.println("applyEditorValue");
				String dataValue = getSelection();
				validValue(dataValue);
				av.setValue(dataValue);
//				System.out.println("dataValue:"+dataValue);
				String attributeName = av.getAttribute().getLocalName();
				av.getContainer().put(attributeName, dataValue);
				viewer.refresh(true);
			}
		});
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public void dispose() {
		getCellControl().dispose();
	}

}
