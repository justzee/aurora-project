package aurora.ide.meta.gef.editors.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

public interface IPropertySource2 {

	public Object getEditableValue();

	public IPropertyDescriptor[] getPropertyDescriptors();

	public Object getPropertyValue(Object id);

	public boolean isPropertySet(Object id);

	public void resetPropertyValue(Object id);

	public void setPropertyValue(Object id, Object value);
}
