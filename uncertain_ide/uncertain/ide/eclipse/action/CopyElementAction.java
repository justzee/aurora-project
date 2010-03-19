package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.Common;

public 	class CopyElementAction extends Action {
	IViewerDirty viewer;
	public CopyElementAction(IViewerDirty viewer) {
		this.viewer = viewer;
	}
	public CopyElementAction(IViewerDirty viewer,ImageDescriptor imageDescriptor,String text) {
		if(imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
		if(text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		CompositeMapAction.copyElement(viewer);
	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return Activator.getImageDescriptor(Common.getString("copy.icon"));
	}
	public static String getDefaultText(){
		return Common.getString("copy");
	}
	
}
