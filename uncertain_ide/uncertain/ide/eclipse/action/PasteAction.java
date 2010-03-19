package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.Common;

public class PasteAction extends Action {
	IViewerDirty viewer;

	public PasteAction(IViewerDirty viewer, ImageDescriptor imageDescriptor,
			String text) {
		this.setHoverImageDescriptor(getImageDescriptor());
		if (imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
		if (text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		CompositeMapAction.pasteElement(viewer);
	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(Common.getString("paste.icon"));
	}

	public static String getDefaultText() {
		return Common.getString("paste");
	}
}
