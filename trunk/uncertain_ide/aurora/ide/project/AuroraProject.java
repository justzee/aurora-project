package aurora.ide.project;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import aurora.ide.helpers.StringUtil;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class AuroraProject {
	private IProject project;

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public IFolder getWeb_inf() {
		IContainer web_home = getWeb_home();
		if (StringUtil.isBlank(web_home)) {
			return null;
		}
		return web_home.getFolder(new Path("WEB-INF"));
	}

	public IContainer getWeb_home() {
		String web = "";
		try {
			web = project.getPersistentProperty(ProjectPropertyPage.WebQN);
		} catch (CoreException e) {
		}
		if (StringUtil.isBlank(web)) {
			return null;
		}
		Path path = new Path(web);
		if (path.segmentCount() ==1) {
			return project;
		}
		IFolder webHome = project.getParent().getFolder(path);
		return webHome;
	}

	public IFolder getWeb_classes() {
		String bm = "";
		try {
			bm = project.getPersistentProperty(ProjectPropertyPage.BMQN);
		} catch (CoreException e) {
		}
		if (StringUtil.isBlank(bm)) {
			return null;
		}
		Path path = new Path(bm);
		if (path.segmentCount() ==1) {
			return null;
		}
		IFolder bmHome = project.getParent().getFolder(new Path(bm));
		return bmHome;
	}

	public AuroraProject(IProject project) {
		super();
		this.project = project;
	}

}
