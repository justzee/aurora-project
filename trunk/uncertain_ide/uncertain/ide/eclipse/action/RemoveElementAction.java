package uncertain.ide.eclipse.action;

import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.AbstractCMViewer;
import uncertain.ide.util.LocaleMessage;

public class RemoveElementAction extends ActionListener {
	private AbstractCMViewer viewer;

	public RemoveElementAction(AbstractCMViewer viewer,int actionStyle) {
		setActionStyle(actionStyle);
		this.viewer = viewer;
	}

	public void run() {
		viewer.removeElement();
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage.getString("delete.icon"));
	}

	public String getDefaultText() {
		return LocaleMessage.getString("delete");
	}
}
