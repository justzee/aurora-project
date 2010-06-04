/**
 * 
 */
package uncertain.ide.eclipse.celleditor;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author linjinxiao
 *
 */
public interface ICellEditor {
	public void init();
	public void createCellEditor(Composite parent);
	public String getSelection();
	public Object valueToShow(String value);
	public void SetSelection(String value);
	public Control getCellControl();
	public TableItem getTableItem();
	public void dispose();
	public boolean validValue(String value);
	public CellEditor getCellEditor();
}
