package aurora.ide.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public final class ResourceUtil {
	/**
	 * find the bm file via its class path
	 * 
	 * @param proj
	 *            the project
	 * @param clsPath
	 *            model path of bm
	 * @return if any exception occurred , returns null
	 */
	public static final IFile getBMFile(IProject proj, String clsPath) {
		if (clsPath == null || proj == null)
			return null;
		String path = clsPath.replace('.', '/') + '.'
				+ AuroraConstant.BMFileExtension;
		String bmhome = getBMHome(proj);
		if (bmhome.length() == 0)
			return null;
		String fullPath = bmhome + '/' + path;
		IResource res = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(fullPath);
		if (res instanceof IFile)
			return (IFile) res;
		return null;
	}

	/**
	 * get a file under web home<br/>
	 * use {@link #getWebHome(IProject)} to get the web home , if success then
	 * append path...
	 * 
	 * @param path
	 *            if webhome is <u>/AA/bb/web</u><br/>
	 *            and the file`s full path is <u>/AA/bb/web/cc/ff.txt</u><br>
	 *            then the path should be <u>cc/ff.txt</u>
	 * @return
	 */
	public static final IFile getFileUnderWebHome(IProject proj, String path) {
		String webHome = getWebHome(proj);
		if (webHome.length() == 0)
			return null;
		String fullPath = webHome + "/" + path;
		IResource res = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(fullPath);
		if (res instanceof IFile)
			return (IFile) res;
		return null;
	}

	/**
	 * if a project is a aurora project (has aurora nature),try to find its
	 * bmhome<br/>
	 * 1st. read its projectPropertyPage<br/>
	 * 2nd. try to find WEB-INF (then append '/classes')
	 * 
	 * @param proj
	 * @return the full path of bmhome (starts form the project root path)<br/>
	 *         if any exception occurred , returns ""
	 */
	public static final String getBMHome(IProject proj) {
		if (!isAuroraProject(proj))
			return "";
		String bmHome = null;
		try {
			bmHome = proj.getPersistentProperty(ProjectPropertyPage.BMQN);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (bmHome == null || bmHome.length() == 0) {
			IFolder wiFolder = searchWebInf(proj);
			if (wiFolder == null)
				return "";
			return wiFolder.getFullPath().toString() + "/classes";
		}
		return bmHome;
	}

	/**
	 * get a aurora project`s webhome<br/>
	 * 1st. try to read projectProperty<br/>
	 * 2nd. use {@link #searchWebInf(IResource)} and getParent
	 * 
	 * 
	 * @param proj
	 * @return id any exception occurred , returns ""
	 */
	public static final String getWebHome(IProject proj) {
		if (!isAuroraProject(proj))
			return "";
		String webHome = null;
		try {
			webHome = proj.getPersistentProperty(ProjectPropertyPage.WebQN);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (webHome == null || webHome.length() == 0) {
			IFolder wiFolder = searchWebInf(proj);
			if (wiFolder == null)
				return "";
			return wiFolder.getParent().getFullPath().toString();
		}
		return webHome;
	}

	/**
	 * test weather a project is a aurora project(not null and has aurora
	 * nature)
	 * 
	 * @param proj
	 * @return
	 */
	public static final boolean isAuroraProject(IProject proj) {
		if (proj == null)
			return false;
		try {
			return AuroraProjectNature.hasAuroraNature(proj);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * just use <i>accept</i> to search the project (of resource) WEB-INF
	 * folder,<br/>
	 * not care weather the project is a aurora project
	 * 
	 * @param resource
	 * @return if any exception occurred , returns null
	 */
	public static final IFolder searchWebInf(IResource resource) {
		if (resource == null)
			return null;
		IProject proj = resource.getProject();
		final IFolder[] results = { null };
		try {
			proj.accept(new IResourceVisitor() {

				public boolean visit(IResource res) throws CoreException {
					if (res.getName().equals("WEB-INF")) {
						results[0] = (IFolder) res;
						return false;
					}
					return true;
				}
			}, IResource.DEPTH_INFINITE, IResource.FOLDER);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return results[0];
	}

	/**
	 * method to get the web-inf folder<br/>
	 * use {@link #getWebHome(IProject)} to get webhome ,if success then append
	 * WEB-INF...
	 * 
	 * @param resource
	 * @return if any exception occurred , returns null
	 */
	public static final IFolder getWebInf(IResource resource) {
		if (resource == null)
			return null;
		String webHome = getWebHome(resource.getProject());
		if (webHome.length() == 0)
			return null;
		IResource res = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(webHome + "/WEB-INF");
		if (res instanceof IFolder)
			return (IFolder) res;
		return null;
	}
}
