package uncertain.ide.eclipse.node.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.core.ICategoryViewer;
import uncertain.ide.help.LocaleMessage;

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
		return Activator.getImageDescriptor(LocaleMessage.getString("category.icon"));
	}
}
