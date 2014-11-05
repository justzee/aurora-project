package aurora.bpmn.designer.rcp.viewer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;

import aurora.bpmn.designer.rcp.viewer.action.CreateBPMDefineAction;
import aurora.bpmn.designer.rcp.viewer.action.DeleteBPMDefineAction;
import aurora.bpmn.designer.rcp.viewer.action.EditBPMDefineAction;
import aurora.bpmn.designer.rcp.viewer.action.LoadBPMServiceAction;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.ServiceModel;

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

		Object data = viewer.getTree().getSelection()[0].getData();
		if (data instanceof ServiceModel) {
			LoadBPMServiceAction lsa = new LoadBPMServiceAction("连接服务",
					(ServiceModel) data, viewer);

			menu.add(lsa);
			CreateBPMDefineAction ca = new CreateBPMDefineAction("新建工作流",
					(ServiceModel) data, bpmServiceViewer);
			menu.add(ca);
			LoadBPMServiceAction lsar = new LoadBPMServiceAction("刷新",
					(ServiceModel) data, viewer);

			menu.add(lsar);
			menu.add(new Action("删除服务") {

			});
			menu.add(new Action("属性") {

			});

		}

		if (data instanceof BPMNDefineModel) {
			EditBPMDefineAction ea = new EditBPMDefineAction("编辑工作流",
					(BPMNDefineModel) data, bpmServiceViewer);
			menu.add(ea);
			DeleteBPMDefineAction del = new DeleteBPMDefineAction("删除工作流",
					(BPMNDefineModel) data, bpmServiceViewer);
			menu.add(del);
			menu.add(new Action("属性") {

			});
		}

	}
}
