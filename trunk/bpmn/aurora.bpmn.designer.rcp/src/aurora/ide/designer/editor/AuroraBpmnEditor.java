package aurora.ide.designer.editor;

import org.eclipse.bpmn2.modeler.ui.editor.BPMN2MultiPageEditor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.progress.UIJob;

import aurora.bpmn.designer.rcp.util.InputStreamUtil;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.BPMService;
import aurora.bpmn.designer.ws.BPMServiceResponse;
import aurora.bpmn.designer.ws.BPMServiceRunner;

public class AuroraBpmnEditor extends BPMN2MultiPageEditor {

	public static final String ID = "aurora.ide.workflow.bpmn.graphiti.ui.editor.AuroraBpmnEditor";

	private BPMNDefineModel define;

	public AuroraBpmnEditor() {

	}

	private class SaveJob extends UIJob {

		public SaveJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			BPMService service = new BPMService(define.getServiceModel());
			service.setBPMNDefineModel(define);
			BPMServiceRunner runner = new BPMServiceRunner(service);
			BPMServiceResponse list = runner.saveBPM();
			int status = list.getStatus();
			if (BPMServiceResponse.sucess == status) {

			} else {
				// TODO
				String serviceL = define.getServiceModel().getSaveServiceUrl();
				MessageDialog.openError(this.getDisplay().getActiveShell(),
						"Error", "服务" + serviceL + "未响应");
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		Resource resource = this.getDesignEditor().getResource();
//		System.out.println(resource.getURI());
		define.setDefine(InputStreamUtil.resource2String(resource));
		new SaveJob("保存到Service").schedule();
		// update to service
	}

	@Override
	public void doSaveAs() {
		super.doSaveAs();
	}

	public BPMNDefineModel getDefine() {
		return define;
	}

	public void setDefine(BPMNDefineModel define) {
		this.define = define;
	}
}
