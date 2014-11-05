package aurora.bpmn.designer.rcp.viewer.action;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.progress.UIJob;

import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.BPMService;
import aurora.bpmn.designer.ws.BPMServiceResponse;
import aurora.bpmn.designer.ws.BPMServiceRunner;
import aurora.bpmn.designer.ws.ServiceModel;

public class LoadBPMServiceAction extends Action {
	private ServiceModel model;
	private TreeViewer viewer;

	public LoadBPMServiceAction(ServiceModel model,TreeViewer viewer){
		super("连接服务");
		this.model = model;
		this.viewer = viewer;
	}
	public void run(){
		LoadJob loadJob = new LoadJob("加载BPM Service Define");
		loadJob.schedule();
	}
	
	private class LoadJob extends UIJob{

		public LoadJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			BPMService service = new BPMService(model);
			BPMServiceRunner runner = new BPMServiceRunner(service);
//			model
			BPMServiceResponse list = runner.list();
			int status = list.getStatus();
			if(BPMServiceResponse.sucess == status){
				List<BPMNDefineModel> defines = list.getDefines();
				model.reload();
				for (BPMNDefineModel bpmnDefineModel : defines) {
					model.addDefine(bpmnDefineModel);
				}
				viewer.refresh(model);
				viewer.expandToLevel(model, 1);
			}else{
				//TODO
			}
			return Status.OK_STATUS;
		}
		
	}

}
