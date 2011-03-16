package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.ICategoryViewer;


public class CharSortAction extends Action {
	
	private ICategoryViewer viewer;
	public CharSortAction(ICategoryViewer viewer) {

		this.viewer = viewer;
	}
	public CharSortAction(ICategoryViewer viewer,ImageDescriptor imageDescriptor,String text) {
		if(imageDescriptor != null)
			setHoverImageDescriptor(imageDescriptor);
		if(text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		viewer.setCategory(false);
		viewer.refresh(false);
	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return Activator.getImageDescriptor(LocaleMessage.getString("asc.icon"));
	}
}
