package aurora.ide.meta.gef.editors.property;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.IPropertySheetEntry;

public class PropertyItem {
	private IPropertySheetEntry data;

	public PropertyItem(IPropertySheetEntry pse) {
		data = pse;
	}

	public PropertyItem(IPropertySheetEntry pse, int index) {
		data = pse;
	}

	public void setData(IPropertySheetEntry pse) {
		data = pse;
	}

	public IPropertySheetEntry getData() {
		return data;
	}

	public String getLabel() {
		return data.getDisplayName();
	}

	public Control getControl(Composite par) {
		return data.getEditor(par).getControl();
	}
}
