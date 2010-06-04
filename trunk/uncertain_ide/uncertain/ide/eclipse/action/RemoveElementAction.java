package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.IContainer;

public class RemoveElementAction extends Action {
	IContainer viewer;

	public RemoveElementAction(IContainer viewer) {
		this.viewer = viewer;
	}

	public RemoveElementAction(IContainer viewer,
			ImageDescriptor imageDescriptor, String text) {
		if (imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
		if (text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		CompositeMapAction.removeElement(viewer);

	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(Common.getString("delete.icon"));
	}

	public static String getDefaultText() {
		return Common.getString("delete");
	}
}
