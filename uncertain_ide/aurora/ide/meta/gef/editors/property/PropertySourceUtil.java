package aurora.ide.meta.gef.editors.property;

import java.util.HashMap;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class PropertySourceUtil {
	private static final HashMap<IPropertySource2, IPropertySource> map = new HashMap<IPropertySource2, IPropertySource>(
			128);

	public static IPropertySource translate(final IPropertySource2 ps2) {
		IPropertySource ps = map.get(ps2);
		if (ps == null) {
			ps = new IPropertySource() {

				public void setPropertyValue(Object id, Object value) {
					ps2.setPropertyValue(id, value);
				}

				public void resetPropertyValue(Object id) {
					ps2.resetPropertyValue(id);
				}

				public boolean isPropertySet(Object id) {
					return ps2.isPropertySet(id);
				}

				public Object getPropertyValue(Object id) {
					return ps2.getPropertyValue(id);
				}

				public IPropertyDescriptor[] getPropertyDescriptors() {
					return ps2.getPropertyDescriptors();
				}

				public Object getEditableValue() {
					return ps2.getEditableValue();
				}
			};
			map.put(ps2, ps);
		}
		return ps;
	}
}
