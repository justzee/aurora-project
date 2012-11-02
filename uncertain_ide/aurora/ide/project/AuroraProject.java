package aurora.ide.project;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import aurora.ide.project.propertypage.ProjectPropertyPage;

public class AuroraProject  {
	private IProject project;
//	private IFolder web_inf;
//	private IFolder web_home;
//	private IFolder web_classes;

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public IFolder getWeb_inf() throws CoreException {
		return getWeb_home().getFolder("WEB-INF");
	}


	public IFolder getWeb_home() throws CoreException {
		String web = project.getPersistentProperty(ProjectPropertyPage.WebQN);
		IFolder webHome = project.getParent().getFolder(new Path(web));
		return webHome;
	}


	public IFolder getWeb_classes() throws CoreException {
		String bm = project.getPersistentProperty(ProjectPropertyPage.BMQN);
		IFolder bmHome = project.getParent().getFolder(new Path(bm));
		return bmHome;
	}



	public AuroraProject(IProject project) {
		super();
		this.project = project;
	}
}
