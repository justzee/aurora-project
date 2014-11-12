package aurora.bpmn.designer.rcp.viewer.action;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.progress.UIJob;

import aurora.bpmn.designer.rcp.viewer.BPMServiceViewer;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.BPMService;
import aurora.bpmn.designer.ws.BPMServiceResponse;
import aurora.bpmn.designer.ws.BPMServiceRunner;
import aurora.bpmn.designer.ws.ServiceModel;

public class BPMDefinePropertyAction extends ViewAction {
	private BPMNDefineModel model;
	private BPMServiceViewer viewer;

	public BPMDefinePropertyAction(String text, BPMServiceViewer viewer) {
		this.setText(text);
		this.viewer = viewer;
	}

	public void run() {

		boolean openConfirm = MessageDialog.openConfirm(viewer.getSite()
				.getShell(), "Confirm", "是否确定");
		if (openConfirm) {
			LoadJob loadJob = new LoadJob("删除BPM Define");
			loadJob.schedule();
		}
	}

	private class LoadJob extends UIJob {

		public LoadJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {

			ServiceModel serviceModel = model.getServiceModel();
			BPMService service = new BPMService(serviceModel);
			service.setBPMNDefineModel(model);
			BPMServiceRunner runner = new BPMServiceRunner(service);
			BPMServiceResponse list = runner.deleteBPM();
			int status = list.getStatus();
			if (BPMServiceResponse.sucess == status) {
				List<BPMNDefineModel> defines = list.getDefines();
				BPMNDefineModel repDefine = defines.get(0);
				if (repDefine != null) {
					serviceModel.removeDefine(model);
					viewer.getTreeViewer().refresh(serviceModel);
					viewer.getTreeViewer().expandToLevel(serviceModel, 1);
				}

			} else {
//				String serviceL = model.getListServiceUrl();
//				MessageDialog.openError(this.getDisplay().getActiveShell(),
//						"Error", "服务" + serviceL + "未响应");
//				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;

		}
	}

	@Override
	public void init() {
		TreeItem[] selection = viewer.getTreeViewer().getTree().getSelection();
		if (selection.length > 0) {
			Object data = selection[0].getData();
			if (data instanceof BPMNDefineModel) {
				this.model = (BPMNDefineModel) data;
			}
		}
		this.setVisible(model instanceof BPMNDefineModel);
	}

}
