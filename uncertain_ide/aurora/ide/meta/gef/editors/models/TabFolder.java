package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.IntegerPropertyDescriptor;

public class TabFolder extends Container {

	private static final long serialVersionUID = 628304066767323457L;
	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new IntegerPropertyDescriptor(WIDTH, "Width"),
			new IntegerPropertyDescriptor(HEIGHT, "Height") };

	public TabFolder() {
		setSize(new Dimension(700, 300));
	}

	public void disSelectAll() {
		for (AuroraComponent ac : getChildren()) {
			if (ac instanceof TabItem) {
				((TabItem) ac).setCurrent(false);
			}
		}
	}

	public TabItem getCurrent() {
		for (AuroraComponent ac : getChildren()) {
			if (ac instanceof TabItem) {
				if (((TabItem) ac).isCurrent())
					return (TabItem) ac;
			}
		}
		return null;
	}

	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		Class<? extends AuroraComponent> cls = component.getClass();
		return cls.equals(TabItem.class) || cls.equals(TabBody.class);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

}
