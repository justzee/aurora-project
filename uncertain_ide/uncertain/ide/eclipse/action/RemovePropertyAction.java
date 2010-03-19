package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.Common;

public class RemovePropertyAction extends Action {

	IPropertyCategory viewer;

	public RemovePropertyAction(IPropertyCategory viewer) {
		this.viewer = viewer;
	}

	public RemovePropertyAction(IPropertyCategory viewer,
			ImageDescriptor imageDescriptor, String text) {
		if (imageDescriptor != null)
			setHoverImageDescriptor(imageDescriptor);
		if (text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		CompositeMapAction.removePropertyAction(viewer);
	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(Common.getString("delete.icon"));
	}
}
