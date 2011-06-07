package node.action;

import ide.AuroraPlugin;
import helpers.LocaleMessage;

import org.eclipse.jface.resource.ImageDescriptor;

import editor.AbstractCMViewer;


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
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("delete.icon"));
	}

	public String getDefaultText() {
		return LocaleMessage.getString("delete");
	}
}
