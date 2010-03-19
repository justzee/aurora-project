package uncertain.ide.eclipse.action;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Common;

public class AddElementListener implements Listener {
	private IViewerDirty viewer;

	CompositeMap parentCM;
	String prefix;
	String uri;
	String cmName;

	public AddElementListener(IViewerDirty viewer, CompositeMap parentCM, QualifiedName qName) {
		this.viewer = viewer;
		this.parentCM = parentCM;
		this.prefix = Common.getPrefix(parentCM,qName);
		this.uri = qName.getNameSpace();
		this.cmName = qName.getLocalName();

	}
	public void handleEvent(Event event) {

		CompositeMapAction.addElement(parentCM, prefix, uri, cmName);
		if (viewer != null) {
			viewer.refresh(true);
		}
	}
}
