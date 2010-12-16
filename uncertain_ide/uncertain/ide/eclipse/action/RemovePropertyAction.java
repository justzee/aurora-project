package uncertain.ide.eclipse.action;

import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.PropertyViewer;
import uncertain.ide.util.LocaleMessage;

public class RemovePropertyAction extends ActionListener {

	PropertyViewer viewer;

	public RemovePropertyAction(PropertyViewer viewer,int actionStyle) {
		setActionStyle(actionStyle);
		this.viewer = viewer;
	}

	public void run() {
		viewer.removePropertyAction();
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage.getString("delete.icon"));
	}
}
