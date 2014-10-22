package aurora.bpmn.designer.rcp.viewer;

import org.eclipse.bpmn2.modeler.ui.Bpmn2DiagramEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PartInitException;

import aurora.bpmn.designer.rcp.action.TestBPMN;
import aurora.ide.designer.editor.AuroraBpmnEditor;
import aurora.ide.designer.editor.BPMServiceInputStreamEditorInput;

public class BPMServiceViewMenu {

	private TreeViewer viewer;
	private BPMServiceViewer bpmServiceViewer;

	public BPMServiceViewMenu(TreeViewer viewer,
			BPMServiceViewer bpmServiceViewer) {
		super();
		this.viewer = viewer;
		this.bpmServiceViewer = bpmServiceViewer;
	}

	public void initContextMenu() {
		MenuManager menuMgr = new MenuManager("NavigationViewMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}

		});
		Menu menu = menuMgr.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);
	}

	private void fillContextMenu(IMenuManager menu) {
		menu.add(new Action("新建服务") {

		});
		menu.add(new Action("链接服务") {

		});
		menu.add(new Action("新建工作流") {

		});
		menu.add(new Action("编辑工作流") {
			public void run() {

				try {
					bpmServiceViewer
							.getSite()
							.getPage()
							.openEditor(
									new BPMServiceInputStreamEditorInput(
											TestBPMN.getStream()),
									AuroraBpmnEditor.ID, true);
				} catch (PartInitException e) {
					MessageDialog.openError(bpmServiceViewer.getSite()
							.getShell(), "Error",
							"Error opening view:" + e.getMessage());
				}

			}
		});
		menu.add(new Action("断开链接") {

		});
	}
}
