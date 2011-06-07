package node.action;

import ide.AuroraPlugin;
import helpers.LocaleMessage;

import org.eclipse.jface.resource.ImageDescriptor;

import editor.AbstractCMViewer;


public class PasteAction extends ActionListener {
	AbstractCMViewer viewer;

	public PasteAction(AbstractCMViewer viewer,int actionStyle) {
		this.viewer = viewer;
		setActionStyle(actionStyle);
	}

	public void run() {
		viewer.pasteElement();
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("paste.icon"));
	}

	public String getDefaultText() {
		return LocaleMessage.getString("paste");
	}
}
