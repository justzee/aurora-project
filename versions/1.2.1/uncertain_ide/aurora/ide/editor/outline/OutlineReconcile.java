package aurora.ide.editor.outline;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import aurora.ide.editor.textpage.IReconcileListener;

public class OutlineReconcile implements IReconcileListener {
	/**
	 * @param sourceViewer
	 */
	public OutlineReconcile(ISourceViewer mSourceViewer) {
		super();
	}

	public void reconcile() {
		if(true)
			return;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (null == workbench) {
					return;
				}

				IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
				if (null == workbenchWindow && workbench.getWorkbenchWindowCount() > 0) {
					workbenchWindow = workbench.getWorkbenchWindows()[0];
				} else if (null == workbenchWindow) {
					return;
				}

				IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
				if (null == workbenchPage) {
					return;
				}

				IViewPart view = workbenchPage.findView("org.eclipse.ui.views.ContentOutline");
				if (null == view) {
					return;
				}

				ContentOutline outline = (ContentOutline) view;

				if (outline.getCurrentPage() instanceof BaseOutlinePage) {
					BaseOutlinePage outlineView = (BaseOutlinePage) outline.getCurrentPage();
					outlineView.refresh();
				}
			}
		});
	}
}
