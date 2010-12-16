/**
 * 
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.swt.SWT;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.core.IViewer;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.util.LocaleMessage;
import uncertain.schema.Attribute;

public abstract class PropertyViewer implements IViewer{
	
	public void removePropertyAction() {
		Attribute attribute = getSelection();
		if(attribute == null){
			CustomDialog.showWarningMessageBox(null, "Please select an attribute first!");
			return;
		}
		int buttonID = CustomDialog.showConfirmDialogBox(null, LocaleMessage.getString("delete.attribute.confirm"));
		switch (buttonID) {
		case SWT.OK:
			CompositeMap data = getInput();
			String propertyName = attribute.getLocalName();
			data.remove(propertyName);
			refresh(true);
			
		case SWT.CANCEL:
			break;
		}
	}
	public abstract CompositeMap getInput();
	public abstract Attribute getSelection();
}
