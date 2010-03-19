package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.Common;

public class CategroyAction extends Action {
	
	private IPropertyCategory viewer;
	public CategroyAction(IPropertyCategory viewer) {
		this.viewer = viewer;
	}
	public CategroyAction(IPropertyCategory viewer,ImageDescriptor imageDescriptor,String text) {
		if(imageDescriptor != null)
			setHoverImageDescriptor(imageDescriptor);
		if(text != null)
			setText(text);
		this.viewer = viewer;
	}


	public void run() {
		viewer.setIsCategory(true);
		viewer.refresh(false);
	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return Activator.getImageDescriptor(Common.getString("category.icon"));
	}
}
