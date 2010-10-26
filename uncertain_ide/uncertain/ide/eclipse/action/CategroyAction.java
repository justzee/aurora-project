package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.ICategoryViewer;

public class CategroyAction extends Action {
	
	private ICategoryViewer viewer;
	public CategroyAction(ICategoryViewer viewer) {
		this.viewer = viewer;
	}
	public CategroyAction(ICategoryViewer viewer,ImageDescriptor imageDescriptor,String text) {
		if(imageDescriptor != null)
			setHoverImageDescriptor(imageDescriptor);
		if(text != null)
			setText(text);
		this.viewer = viewer;
	}


	public void run() {
		viewer.setCategory(true);
	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return Activator.getImageDescriptor(LocaleMessage.getString("category.icon"));
	}
}
