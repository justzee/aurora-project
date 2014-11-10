package aurora.bpmn.designer.rcp.viewer.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.progress.UIJob;

import aurora.bpmn.designer.ws.BPMNDefineCategory;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.BPMService;
import aurora.bpmn.designer.ws.BPMServiceResponse;
import aurora.bpmn.designer.ws.BPMServiceRunner;
import aurora.bpmn.designer.ws.ServiceModel;

public class LoadBPMServiceAction extends Action {
	private ServiceModel model;
	private TreeViewer viewer;

	public LoadBPMServiceAction(String text, ServiceModel model,
			TreeViewer viewer) {
		super(text);
		this.model = model;
		this.viewer = viewer;
	}

	public void run() {
		LoadJob loadJob = new LoadJob("加载BPM Service Define");
		loadJob.schedule();
	}

	private class LoadJob extends UIJob {

		public LoadJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			BPMService service = new BPMService(model);
			BPMServiceRunner runner = new BPMServiceRunner(service);
			// model
			BPMServiceResponse listBPMCategory = runner.listBPMCategory();
			BPMServiceResponse list = runner.listBPM();
			int status = list.getStatus();
			if (BPMServiceResponse.sucess == status) {

				merge(listBPMCategory, list);

				List<BPMNDefineModel> defines = list.getDefines();
				model.reload();
				for (BPMNDefineModel bpmnDefineModel : defines) {
					model.addDefine(bpmnDefineModel);
				}
				viewer.refresh(model);
				viewer.expandToLevel(model, 1);
			} else {
				// TODO
			}
			return Status.OK_STATUS;
		}

		private void merge(BPMServiceResponse listBPMCategory,
				BPMServiceResponse listBPM) {
			List<BPMNDefineCategory> categorys = listBPMCategory.getCategorys();
			Map<String, BPMNDefineCategory> mcs = new HashMap<String, BPMNDefineCategory>();
			for (BPMNDefineCategory category : categorys) {
				String id = category.getId();
				mcs.put(id, category);
			}
			for (BPMNDefineCategory category : categorys) {
				String parent_id = category.getParent_id();
				BPMNDefineCategory pc = mcs.get(parent_id);
				if (pc != null) {
					pc.addCategory(category);
				} else {
					model.addCategory(category);
				}
			}

			List<BPMNDefineModel> defines = listBPM.getDefines();
			for (BPMNDefineModel dm : defines) {
				String category_id = dm.getCategory_id();
				BPMNDefineCategory c = mcs.get(category_id);
				if (c != null) {
					c.addDefine(dm);
				} else {
					model.addDefine(dm);
				}
			}

		}

	}

}
