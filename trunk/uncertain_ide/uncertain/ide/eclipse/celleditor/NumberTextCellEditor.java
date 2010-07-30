/**
 * 
 */
package uncertain.ide.eclipse.celleditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.datatype.ConvertionException;
import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import uncertain.ide.eclipse.editor.ITableViewer;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.schema.Attribute;

/**
 * @author linjinxiao
 * 
 */
public class NumberTextCellEditor extends AbstractTextCellEditor {

	/**
	 * @param tableViewer
	 * @param property
	 * @param record it can be null in grid table
	 * @param item it can be null in grid table
	 */
	public NumberTextCellEditor(ITableViewer tableViewer, Attribute property,
			CompositeMap record, TableItem item) {
		super(tableViewer, property, record, item);
	}
	/**
	 * 
	 */
	public boolean validValue(String value) {
		String errorMessage = "";
		QualifiedName typeQname = property.getTypeQName();
		String typeLocalName = typeQname.getLocalName();
		DataType dt = DataTypeRegistry.getInstance().getDataType(typeLocalName);
		try {
			if (dt != null && value != null)
				dt.convert(value);
		} catch (ConvertionException e) {
			errorMessage = "the value '" + value + "' can not for this field <"
					+ property.getLocalName() + "> !  "
					+ e.getLocalizedMessage();
			CustomDialog.showErrorMessageBox(null, errorMessage);
			getCellControl().setFocus();
			throw new IllegalArgumentException(errorMessage);
		}
		return super.validValue(value);
	}

	public void createCellEditor(Composite parent) {
		this.setStyle(SWT.RIGHT_TO_LEFT);
		super.create(parent);

	}
}
