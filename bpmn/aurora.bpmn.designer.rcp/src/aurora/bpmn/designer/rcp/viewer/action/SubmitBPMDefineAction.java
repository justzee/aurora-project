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

public class SubmitBPMDefineAction extends ViewAction {
	private BPMNDefineModel model;
	private BPMServiceViewer viewer;

	public SubmitBPMDefineAction(String text, BPMServiceViewer viewer) {
		this.setText(text);
		this.viewer = viewer;
	}

	public void run() {
		LoadJob loadJob = new LoadJob("Load BPM Define");
		loadJob.schedule();
	}

	private class LoadJob extends UIJob {

		public LoadJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {

			BPMService service = new BPMService(model.getServiceModel());
			String oaf = model.getApprove_flag();
			model.setApprove_flag("1");
			service.setBPMNDefineModel(model);
			BPMServiceRunner runner = new BPMServiceRunner(service);
			BPMServiceResponse list = runner.saveBPM();
			int status = list.getStatus();
			if (BPMServiceResponse.sucess == status) {
				List<BPMNDefineModel> defines = list.getDefines();
				BPMNDefineModel define = defines.get(0);
				if (define != null) {
					model.copy(define);
				}
			} else {
				model.setApprove_flag(oaf);
				String serviceL = model.getServiceModel().getSaveServiceUrl();
				MessageDialog.openError(this.getDisplay().getActiveShell(),
						"Error", "服务" + serviceL + "未响应");
				return Status.CANCEL_STATUS;
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
		this.setVisible(model instanceof BPMNDefineModel
				&& "0".equals(model.getApprove_flag())
				&& "n".equalsIgnoreCase(model.getCurrent_version_flag())
				&& "n".equalsIgnoreCase(model.getEnable()));
	}

}
