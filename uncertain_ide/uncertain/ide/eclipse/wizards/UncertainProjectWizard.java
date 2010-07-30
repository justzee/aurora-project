package uncertain.ide.eclipse.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import uncertain.ide.eclipse.editor.bm.UncertainProject;


public class UncertainProjectWizard extends BasicNewProjectResourceWizard {

	public static final String copyright = "(c) Copyright HAND Enterprise Solutions Company Ltd.";

	UncertainProjectWizardPage wizardPage = new UncertainProjectWizardPage();

	public void addPages() {
		super.addPages();
		addPage(wizardPage);
		wizardPage.setPageComplete(false);
	}
	public boolean performFinish() {
		if (!super.performFinish())
			return false;
		initProject();
		return true;
	}

	private void initProject() {
		try {
//			IFile file = getNewProject().getFile(UncertainDataBase.projectFile);
//			String fileFullPath = Common.getIfileLocalPath(file);
//			File root = new File(fileFullPath);
//			if (!root.exists()) {
//				try {
//					if(root.createNewFile()){
//						 Properties props = new Properties();
//						 props.put(UncertainDataBase.uncertain_project_dir, wizardPage.getUncertainProDir());
//						 props.store(new FileOutputStream(root), "uncertina project properties");
//					}
//					
//				} catch (IOException e) {
//					 CustomDialog.showExceptionMessageBox(e);
//				}
//			}
//			getNewProject().refreshLocal(IResource.DEPTH_ONE, null);
			UncertainProject.createProjectFile(getNewProject(), wizardPage.getUncertainProDir());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	public void createPageControls(Composite pageContainer) {
		// super.createPageControls(pageContainer); 
	}
}
