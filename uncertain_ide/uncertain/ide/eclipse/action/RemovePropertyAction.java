package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.PropertyViewer;

public class RemovePropertyAction extends Action {

	PropertyViewer viewer;

	public RemovePropertyAction(PropertyViewer viewer) {
		this.viewer = viewer;
	}

	public RemovePropertyAction(PropertyViewer viewer,
			ImageDescriptor imageDescriptor, String text) {
		if (imageDescriptor != null)
			setHoverImageDescriptor(imageDescriptor);
		if (text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		viewer.removePropertyAction();
	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage.getString("delete.icon"));
	}
}
