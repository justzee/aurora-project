package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.IViewer;

public class AddElementAction extends Action {
	private IViewer viewer;

	private CompositeMap parentCM;
	private String prefix;
	private String uri;
	private String cmName;

	public AddElementAction(IViewer viewer, CompositeMap parentCM,
			String prefix, String uri, String cmName) {
		this.viewer = viewer;
		this.parentCM = parentCM;
		this.prefix = prefix;
		this.uri = uri;
		this.cmName = cmName;
		setText(cmName);

	}

	public AddElementAction(IViewer viewer, CompositeMap parentCM,
			QualifiedName qName, ImageDescriptor imageDescriptor, String text) {
		this.viewer = viewer;
		this.parentCM = parentCM;
		this.uri = qName.getNameSpace();
		this.prefix = CompositeMapAction.getPrefix(parentCM,qName);
		this.cmName = qName.getLocalName();
		if (imageDescriptor != null)
			setHoverImageDescriptor(imageDescriptor);
		if (text != null)
			setText(text);

	}

	public void run() {
		CompositeMapAction.addElement(parentCM, prefix, uri, cmName);
		if (viewer != null) {
			viewer.refresh(true);
		}
	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage.getString("element.icon"));
	}
}
