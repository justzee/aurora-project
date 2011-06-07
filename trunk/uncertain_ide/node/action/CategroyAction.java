package node.action;

import ide.AuroraPlugin;
import helpers.LocaleMessage;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import editor.core.ICategoryViewer;


public class CategroyAction extends Action {
	
	private ICategoryViewer viewer;
	public CategroyAction(ICategoryViewer viewer) {
		this.viewer = viewer;
		setHoverImageDescriptor(getDefaultImageDescriptor());
	}
	public void run() {
		viewer.setCategory(true);
	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("category.icon"));
	}
}
