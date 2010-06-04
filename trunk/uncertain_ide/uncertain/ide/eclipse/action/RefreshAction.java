package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.IViewer;

/**
 * Ë¢ÐÂµÄActionÀà
 */
public class RefreshAction extends Action {
	IViewer viewer;

	public RefreshAction(IViewer viewer) {
		this.viewer = viewer;
	}

	public RefreshAction(IViewer viewer, ImageDescriptor imageDescriptor,
			String text) {
		if (imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
		if (text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		viewer.refresh(false);
	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(Common.getString("refresh.icon"));
	}
}
