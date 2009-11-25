/**
 * 
 */
package uncertain.ide.eclipse.action;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Control;

import uncertain.composite.CompositeMap;

public interface IViewerDirty extends IViewer{
	public ColumnViewer getObject();
	public CompositeMap getSelectedData();
	public void setSelectedData(CompositeMap data);
	public void setFocusData(CompositeMap data);
	public CompositeMap getFocusData();
	public Control getControl();
}
