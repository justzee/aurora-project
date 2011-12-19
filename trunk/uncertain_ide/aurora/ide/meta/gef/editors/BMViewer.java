package aurora.ide.meta.gef.editors;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import aurora.ide.AuroraPlugin;

public class BMViewer {

	private VScreenEditor vse;

	public BMViewer(Composite c, VScreenEditor vScreenEditor) {
		configrueTreeViewer(c);
		vse = vScreenEditor;
	}

	private void configrueTreeViewer(Composite c) {
		TreeViewer tv = new TreeViewer(c,SWT.NONE);
		tv.setContentProvider(new WorkbenchContentProvider());
		tv.setLabelProvider(new WorkbenchLabelProvider());
		tv.setInput(AuroraPlugin.getWorkspace().getRoot());
		tv.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

}
