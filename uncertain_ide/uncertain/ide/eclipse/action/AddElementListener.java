package uncertain.ide.eclipse.action;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import uncertain.composite.CompositeMap;

public class AddElementListener implements Listener {
	private IViewerDirty mDirtyObject;

	CompositeMap parentCM;
	String _prefix;
	String _uri;
	String _name;

	public AddElementListener(ColumnViewer mColumnViewer,
			IViewerDirty mDirtyObject, CompositeMap parentCM, String _prefix,
			String _uri, String _name) {
		this.mDirtyObject = mDirtyObject;
		this.parentCM = parentCM;
		this._prefix = _prefix;
		this._uri = _uri;
		this._name = _name;

	}

	public void handleEvent(Event event) {

		CompositeMapAction.addElement(parentCM, _prefix, _uri, _name);
		if (mDirtyObject != null) {
			mDirtyObject.setDirty(true);
			mDirtyObject.refresh();
		}
	}
}
