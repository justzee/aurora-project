package uncertain.ide.eclipse.wizards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import uncertain.ide.Common;


public class ProjectWizard extends BasicNewProjectResourceWizard {

	public static final String copyright = "(c) Copyright HAND Enterprise Solutions Company Ltd.";

	ProjectWizardPage fMainPage = new ProjectWizardPage();

	public void addPages() {
		super.addPages();
		fMainPage.setPageComplete(false);
		addPage(fMainPage);
	}

	public boolean performFinish() {
		if (!super.performFinish())
			return false;
		initProject();
		return true;
	}

	private void initProject() {
		try {
			IFile file = getNewProject().getFile(Common.projectFile);
			String fileFullPath = Common.getIfileLocalPath(file);
			File root = new File(fileFullPath);
			if (!root.exists()) {
				try {
					if(root.createNewFile()){
						 Properties props = new Properties();
						 props.put(Common.uncertain_project_dir, fMainPage.getUncertainProDir());
						 props.store(new FileOutputStream(root), "uncertina project properties");
					}
					
				} catch (IOException e) {
					 Common.showExceptionMessageBox(null, e);
				}
			}
			// file.create(null, true, null);
//			Activator.openFileInEditor(file, Common.ServiceEditor);
			getNewProject().refreshLocal(IResource.DEPTH_ONE, null);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
