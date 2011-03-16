package uncertain.ide;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class AuroraProjectNature implements IProjectNature {

	private IProject project;
	public static final String ID = "uncertain_ide.auroranature";

	public void configure() throws CoreException {
	}

	public void deconfigure() throws CoreException {
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}
	public static void addAuroraNature(IProject project) throws CoreException {
		if (project.hasNature(ID))
			return;
		IProjectDescription description = project.getDescription();
		String[] ids = description.getNatureIds();
		String[] newIds = new String[ids.length + 1];
		System.arraycopy(ids, 0, newIds, 0, ids.length);
		newIds[ids.length] = ID;
		description.setNatureIds(newIds);
		project.setDescription(description, null);
	}
	public static void removeAuroraNature(IProject project) throws CoreException {
		if (!project.hasNature(ID))
			return;
		IProjectDescription description = project.getDescription();
		String[] ids = description.getNatureIds();
		for (int i = 0; i < ids.length; ++i) {
			if (ids[i].equals(ID)) {
				String[] newIds = new String[ids.length - 1];
				System.arraycopy(ids, 0, newIds, 0, i);
				System.arraycopy(ids, i + 1, newIds, i, ids.length - i - 1);
				description.setNatureIds(newIds);
				project.setDescription(description, null);
				return;
			}
		}
	}
	public static boolean hasAuroraNature(IProject project) throws CoreException{
		return project.hasNature(ID);
	}

}
