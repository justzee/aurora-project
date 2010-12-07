/**
 * 
 */
package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author linjinxiao
 *
 */
public abstract class ActionListener extends Action implements Listener{

	public static final int NONE = 0;
	public static final int defaultIMG = 1<<1;
	public static final int defaultTitle = 1<<2;
	
	public void setActionStyle(int actionStyle){
		if((actionStyle&defaultIMG) != 0){
			setHoverImageDescriptor(getDefaultImageDescriptor());
		}
		if((actionStyle&defaultTitle) != 0){
			setText(getDefaultText());
		}
	}
	public abstract ImageDescriptor getDefaultImageDescriptor();

	public String getDefaultText() {
		return "";
	}
	public void handleEvent(Event event) {
		run();		
	}
}
