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
import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.IContainer;
import uncertain.schema.Attribute;

/**
 * @author linjinxiao
 * 
 */
public class NumberTextCellEditor extends AbstractTextCellEditor {

	public NumberTextCellEditor(IContainer container, CompositeMap record,
			Attribute property, TableItem item) {
		super(container, record, property, item);
	}

	public boolean validValue(String value) {
		String errorMessage = "";
		QualifiedName typeQname = property.getTypeQName();
		String typeLocalName = typeQname.getLocalName();
		DataType dt = DataTypeRegistry.getInstance().getDataType(typeLocalName);
		try {
			if(dt !=null && value != null)
				dt.convert(value);
		} catch (ConvertionException e) {
			errorMessage = "the value '"+value+"' can not for this field <"+property.getLocalName()+"> !  "+e.getLocalizedMessage();
			Common.showErrorMessageBox(null, errorMessage);
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
