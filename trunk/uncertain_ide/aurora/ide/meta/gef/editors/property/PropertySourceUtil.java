package aurora.ide.meta.gef.editors.property;

import java.util.HashMap;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.views.properties.IPropertySource;

import aurora.ide.meta.gef.editors.models.commands.ChangePropertyCommand;

public class PropertySourceUtil {
	private static final HashMap<IPropertySource2, IPropertySource> map = new HashMap<IPropertySource2, IPropertySource>(
			128);

	public static IPropertySource translate(final IPropertySource2 ps2,
			CommandStack cmdStack) {
		IPropertySource ps = map.get(ps2);
		if (ps == null) {
			ps = new ChangePropertyCommand(ps2, cmdStack);
			map.put(ps2, ps);
		}
		return ps;
	}

}
