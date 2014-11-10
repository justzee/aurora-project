package aurora.bpmn.designer.rcp.viewer.action;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;

import aurora.bpmn.designer.rcp.viewer.BPMServiceViewer;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.BPMService;
import aurora.bpmn.designer.ws.BPMServiceResponse;
import aurora.bpmn.designer.ws.BPMServiceRunner;
import aurora.ide.designer.editor.AuroraBpmnEditor;
import aurora.ide.designer.editor.BPMServiceInputStreamEditorInput;

public class EditBPMDefineAction extends Action {
	private BPMNDefineModel model;
	private BPMServiceViewer viewer;

	public EditBPMDefineAction(String text, BPMNDefineModel model,
			BPMServiceViewer viewer) {
		super(text);
		this.model = model;
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
			service.setBPMNDefineModel(model);
			BPMServiceRunner runner = new BPMServiceRunner(service);
			BPMServiceResponse list = runner.fetchBPM();
			int status = list.getStatus();
			if (BPMServiceResponse.sucess == status) {
				List<BPMNDefineModel> defines = list.getDefines();
				BPMNDefineModel define = defines.get(0);
				if (define != null) {
					model.copy(define);
				}
			} else {
				// TODO
			}

			try {
				ByteArrayInputStream is = new ByteArrayInputStream(model
						.getDefines().getBytes("UTF-8"));
				IEditorPart openEditor = viewer
						.getSite()
						.getPage()
						.openEditor(new BPMServiceInputStreamEditorInput(is),
								AuroraBpmnEditor.ID, true);
				if (openEditor instanceof AuroraBpmnEditor) {
					((AuroraBpmnEditor) openEditor).setDefine(model);
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return Status.OK_STATUS;
		}

	}

}
