package uncertain.ide.eclipse.action;

import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.core.IViewer;
import uncertain.ide.util.LocaleMessage;

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
		return Activator.getImageDescriptor(LocaleMessage.getString("refresh.icon"));
	}
	public String getDefaultText(){
		return LocaleMessage.getString("refresh");
	}
	 
}
