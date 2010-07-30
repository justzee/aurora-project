package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.CompositeMapViewer;

public 	class CopyElementAction extends Action {
	CompositeMapViewer viewer;
	public CopyElementAction(CompositeMapViewer viewer) {
		this.viewer = viewer;
	}
	public CopyElementAction(CompositeMapViewer viewer,ImageDescriptor imageDescriptor,String text) {
		if(imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
		if(text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		viewer.copyElement();
	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return Activator.getImageDescriptor(LocaleMessage.getString("copy.icon"));
	}
	public static String getDefaultText(){
		return LocaleMessage.getString("copy");
	}
	
}
