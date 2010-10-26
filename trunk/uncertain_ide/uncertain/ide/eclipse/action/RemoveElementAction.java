package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.AbstractCMViewer;

public class RemoveElementAction extends Action {
	private AbstractCMViewer viewer;

	public RemoveElementAction(AbstractCMViewer viewer) {
		this.viewer = viewer;
	}

	public RemoveElementAction(AbstractCMViewer viewer,
			ImageDescriptor imageDescriptor, String text) {
		if (imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
		if (text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		viewer.removeElement();

	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage.getString("delete.icon"));
	}

	public static String getDefaultText() {
		return LocaleMessage.getString("delete");
	}
}
