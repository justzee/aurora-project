package uncertain.ide.eclipse.action;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;

public class AddElementListener implements Listener {
	private IViewerDirty viewer;

	CompositeMap parentCM;
	String prefix;
	String uri;
	String cmName;

	private AddElementListener(ColumnViewer mColumnViewer,
			IViewerDirty viewer, CompositeMap parentCM, String prefix,
			String uri, String cmName) {
		this.viewer = viewer;
		this.parentCM = parentCM;
		this.prefix = prefix;
		this.uri = uri;
		this.cmName = cmName;

	}
	public AddElementListener(ColumnViewer mColumnViewer,
			IViewerDirty viewer, CompositeMap parentCM, QualifiedName qName) {
		this.viewer = viewer;
		this.parentCM = parentCM;
		this.prefix = qName.getPrefix();
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
