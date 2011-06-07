package node.action;

import ide.AuroraPlugin;
import helpers.LocaleMessage;

import org.eclipse.jface.resource.ImageDescriptor;

import editor.core.IViewer;


public class RefreshAction extends ActionListener {
	IViewer viewer;

	public RefreshAction(IViewer viewer,int actionStyle) {
		setActionStyle(actionStyle);
		this.viewer = viewer;
	}

	public void run() {
		viewer.refresh(false);
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("refresh.icon"));
	}
	public String getDefaultText(){
		return LocaleMessage.getString("refresh");
	}
	 
}
