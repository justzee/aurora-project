package uncertain.ide.eclipse.action;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.IViewer;

public class RemoveElementListener implements Listener {
//	private ColumnViewer mColumnViewer;
	private IViewer viewer;
	CompositeMap parentCM;
	String prefix;
	String uri;
	String name;

	public RemoveElementListener(ColumnViewer mColumnViewer,
			IViewer viewer, CompositeMap parentCM, String prefix,
			String uri, String name) {
//		this.mColumnViewer = mColumnViewer;
		this.viewer = viewer;
		this.parentCM = parentCM;
		this.prefix = prefix;
		this.uri = uri;
		this.name = name;

	}

	public void handleEvent(Event event) {

		CompositeMapAction.addElement(parentCM, prefix, uri, name);
		if (viewer != null) {
			viewer.refresh(true);
		}
	}
}
