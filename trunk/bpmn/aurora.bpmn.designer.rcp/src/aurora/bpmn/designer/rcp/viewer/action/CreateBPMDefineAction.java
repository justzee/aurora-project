package aurora.bpmn.designer.rcp.viewer.action;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;

import aurora.bpmn.designer.rcp.action.TestBPMN;
import aurora.bpmn.designer.rcp.util.InputStreamUtil;
import aurora.bpmn.designer.rcp.viewer.BPMServiceViewer;
import aurora.bpmn.designer.rcp.viewer.action.wizard.CreateBPMDefineWizard;
import aurora.bpmn.designer.ws.BPMNDefineCategory;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.BPMService;
import aurora.bpmn.designer.ws.BPMServiceResponse;
import aurora.bpmn.designer.ws.BPMServiceRunner;
import aurora.bpmn.designer.ws.ServiceModel;
import aurora.ide.designer.editor.AuroraBpmnEditor;
import aurora.ide.designer.editor.BPMServiceInputStreamEditorInput;

public class CreateBPMDefineAction extends Action {
	private ServiceModel model;
	private BPMServiceViewer viewer;

	public CreateBPMDefineAction(String text, ServiceModel model,
			BPMServiceViewer viewer) {
		super(text);
		this.model = model;
		this.viewer = viewer;
	}

	public void run() {

		CreateBPMDefineWizard w = new CreateBPMDefineWizard(
				model.getAllBPMNDefineCategory(), viewer.getSite().getShell());
		int open = w.open();

		if (WizardDialog.OK == open) {
			LoadJob loadJob = new LoadJob("新建BPM Define", w.getModel());
			loadJob.schedule();
		}
	}

	// private class CategoryJob extends UIJob{
	//
	// public CategoryJob(String name) {
	// super(name);
	// }
	//
	// @Override
	// public IStatus runInUIThread(IProgressMonitor monitor) {
	// BPMService service = new BPMService(model);
	// BPMServiceRunner runner = new BPMServiceRunner(service);
	// BPMServiceResponse list = runner.listBPMCategory();
	// List<BPMNDefineCategory> categorys = list.getCategorys();
	// return Status.OK_STATUS;
	// }
	//
	// }
	private class LoadJob extends UIJob {

		private BPMNDefineModel define;

		public LoadJob(String name, BPMNDefineModel model) {
			super(name);
			this.define = model;
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			// BPMNDefineModel define = new BPMNDefineModel();
			// define.setCurrent_version_flag("Y");
			// define.setDescription("XX");
			// define.setName("Hello");
			// define.setProcess_code("007");
			// define.setProcess_version("001");
			define.setDefine(InputStreamUtil.stream2String(TestBPMN.getStream()));

			BPMService service = new BPMService(model);
			service.setBPMNDefineModel(define);
			BPMServiceRunner runner = new BPMServiceRunner(service);
			BPMServiceResponse list = runner.saveBPM();
			int status = list.getStatus();
			if (BPMServiceResponse.sucess == status) {
				List<BPMNDefineModel> defines = list.getDefines();
				BPMNDefineModel repDefine = defines.get(0);
				if (repDefine != null) {

					BPMNDefineCategory bpmnDefineCategory = model
							.getBPMNDefineCategory(repDefine.getCategory_id());
					if (bpmnDefineCategory != null) {
						bpmnDefineCategory.addDefine(repDefine);
						viewer.getTreeViewer().refresh(bpmnDefineCategory);
						viewer.getTreeViewer().expandToLevel(
								bpmnDefineCategory, 1);
					} else {
						model.addDefine(repDefine);
						viewer.getTreeViewer().refresh(model);
						viewer.getTreeViewer().expandToLevel(model, 1);
					}
					try {
						ByteArrayInputStream is = new ByteArrayInputStream(
								repDefine.getDefines().getBytes("UTF-8"));
						IEditorPart openEditor = viewer
								.getSite()
								.getPage()
								.openEditor(
										new BPMServiceInputStreamEditorInput(is),
										AuroraBpmnEditor.ID, true);
						if (openEditor instanceof AuroraBpmnEditor) {
							((AuroraBpmnEditor) openEditor)
									.setDefine(repDefine);
						}
					} catch (PartInitException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

			} else {
				// TODO
			}
			return Status.OK_STATUS;

		}
	}

}
