package aurora.bpmn.designer.rcp.viewer.action.wizard;

import org.eclipse.swt.widgets.Shell;

import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.ide.swt.util.PageModel;
import aurora.ide.swt.util.UWizard;

public class CreateBPMDefineWizard extends UWizard {

	private BPMNDefineModel model;
	private CreateBPMDefinePage page;

	public CreateBPMDefineWizard(Shell shell) {
		super(shell);
	}

	@Override
	public void addPages() {
		page = new CreateBPMDefinePage("CreateBPMDefinePage");
		this.addPage(page);
	}

	@Override
	public boolean performFinish() {
		model = new BPMNDefineModel();
		PageModel pm = page.getModel();
		model.setDescription(pm
				.getStringPropertyValue(CreateBPMDefinePage.DESCRIPTION));
		model.setName(pm.getStringPropertyValue(CreateBPMDefinePage.NAME));
		model.setProcess_code(pm
				.getStringPropertyValue(CreateBPMDefinePage.PROCESS_CODE));
		model.setProcess_version(pm
				.getStringPropertyValue(CreateBPMDefinePage.PROCESS_VERSION));
		return true;
	}

	public BPMNDefineModel getModel() {
		return model;
	}

	public void setModel(BPMNDefineModel model) {
		this.model = model;
	}
}
