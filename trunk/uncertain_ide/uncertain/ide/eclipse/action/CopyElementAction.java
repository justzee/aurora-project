package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.AbstractCMViewer;
import uncertain.ide.util.LocaleMessage;

public 	class CopyElementAction extends Action {
	AbstractCMViewer viewer;
	public CopyElementAction(AbstractCMViewer viewer) {
		this.viewer = viewer;
	}
	public CopyElementAction(AbstractCMViewer viewer,ImageDescriptor imageDescriptor,String text) {
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
