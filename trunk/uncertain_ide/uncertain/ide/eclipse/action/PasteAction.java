package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.AbstractCMViewer;

public class PasteAction extends Action {
	AbstractCMViewer viewer;

	public PasteAction(AbstractCMViewer viewer, ImageDescriptor imageDescriptor,
			String text) {
		this.setHoverImageDescriptor(getImageDescriptor());
		if (imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
		if (text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		viewer.pasteElement();
	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage.getString("paste.icon"));
	}

	public static String getDefaultText() {
		return LocaleMessage.getString("paste");
	}
}
