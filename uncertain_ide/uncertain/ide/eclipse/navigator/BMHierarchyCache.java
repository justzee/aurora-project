package uncertain.ide.eclipse.navigator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.AuroraProjectNature;
import uncertain.ide.eclipse.project.propertypage.ProjectPropertyPage;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.AuroraResourceUtil;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.SystemException;
import aurora.ide.AuroraConstant;

public class BMHierarchyCache {
	private Map projectBMFileMap = new HashMap();
	private static final String ExtendAttrName = "extend";
	private static BMHierarchyCache instance;
	private BMHierarchyCache() {
	}
	public synchronized static BMHierarchyCache getInstance() {
		if (instance == null) {
			instance = new BMHierarchyCache();
		}
		return instance;
	}

	public void initProject(IProject project) throws ApplicationException {
		try {
			if(!hasAuroraNature(project))
				return;
		} catch (CoreException e) {
			CustomDialog.showErrorMessageBox(e);
			return;
		}
		String bmBaseDir = ProjectPropertyPage.getBMBaseDir(project);
		if (bmBaseDir == null) {
			throw new ApplicationException("请检查BM目录设置！");
		}
		IResource bmDir = ResourcesPlugin.getWorkspace().getRoot().findMember(bmBaseDir);
		if (bmDir == null) {
			throw new ApplicationException(bmBaseDir + "资源不存在");
		}
		if (!(bmDir instanceof IContainer)) {
			throw new ApplicationException(bmBaseDir + "不是目录");
		}
		IContainer bmContainer = (IContainer) bmDir;
		iteratorResource(bmContainer);
	}
	private boolean hasAuroraNature(Object obj) throws CoreException {
		if (!(obj instanceof IResource))
			return false;
		IResource resource = (IResource) obj;
		if (resource.getProject() == null || !AuroraProjectNature.hasAuroraNature(resource.getProject())) {
			return false;
		}
		return true;
	}
	private void iteratorResource(IContainer parent) throws ApplicationException {
		try {
			IResource[] members = parent.members();
			for (int i = 0; i < members.length; i++) {
				IResource child = members[i];
				if (child.getName().toLowerCase().endsWith("." + AuroraConstant.BMFileExtension)) {
					createLinkFile(child);
				}
				if (child instanceof IContainer) {
					iteratorResource((IContainer) child);
				}
			}
		} catch (CoreException e) {
			throw new SystemException(e);
		}
	}
	public String getExtendValue(IResource bmFile) throws ApplicationException {
		if (bmFile == null) {
			throw new ApplicationException(bmFile + "文件不能为空！");
		}
		CompositeLoader cl = AuroraResourceUtil.getCompsiteLoader();
		String localPath = bmFile.getLocation().toOSString();
		cl.setSaveNamespaceMapping(true);
		CompositeMap bmData;
		try {
			bmData = cl.loadByFile(localPath);
		} catch (IOException e) {
			throw new ApplicationException("请查看" + localPath + "文件是否存在.");
		} catch (SAXException e) {
			throw new ApplicationException("请查看" + localPath + "文件格式是否正确！");
		}
		String extendValue = bmData.getString(ExtendAttrName);
		return extendValue;
		// orgData(bmFile,extendValue);

	}
	private BMFile createLinkFile(IResource bmFile) throws ApplicationException {
		String extendValue = "";
		try {
			extendValue = getExtendValue(bmFile);
		} catch (ApplicationException e) {
//			CustomDialog.showErrorMessageBox(e);
			return null;
		}
		IPath thisKey = bmFile.getFullPath();
		Map ifileMap = getProjectBMNotNull(bmFile.getProject());
		Object obj = ifileMap.get(thisKey);
		if (obj != null)
			return (BMFile) obj;
		if (extendValue == null) {
			BMFile thisFile = new BMFile(null, bmFile.getFullPath());
			ifileMap.put(thisKey, thisFile);
			return thisFile;
		}
		IResource parent = getBMResourceFromClassPath(bmFile.getProject(), extendValue);
		BMFile thisFile = new BMFile(parent.getFullPath(), bmFile.getFullPath());
		ifileMap.put(bmFile.getFullPath(), thisFile);

		BMFile parentFile = createLinkFile(parent);
		if (parentFile != null)
			parentFile.addSubBMFile(thisFile);
		return thisFile;
	}
	private IResource getBMResourceFromClassPath(IProject project, String bmClassPath) throws ApplicationException {
		String filePath = bmClassPath.replace('.', File.separatorChar) + ".bm";
		String fileFullPath = ProjectPropertyPage.getBMBaseDir(project) + File.separator + filePath;
		Path keyPath = new Path(fileFullPath);
		return ResourcesPlugin.getWorkspace().getRoot().findMember(keyPath);
	}
	public BMFile getBMLinkFile(Object file) throws ApplicationException {
		if (file instanceof IFile) {
			IFile resource = (IFile) file;
			return searchBMLinkFile(resource);
		} else if (file instanceof BMFile) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(((BMFile) file).getPath());
			return searchBMLinkFile(resource);
		} else if (file instanceof IPath) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember((IPath) file);
			return searchBMLinkFile(resource);
		} else if (file instanceof IResource) {
			return searchBMLinkFile((IResource) file);
		} else {
			throw new ApplicationException("请检查对象是" + "IFile或者BMFile类型!");
		}
	}
	private BMFile searchBMLinkFile(IResource resource) throws ApplicationException {
		if (resource == null || !resource.exists())
			return null;
		if (!isInitedProject(resource.getProject())) {
			initProject(resource.getProject());
		}
		Map ifileMap = getProjectBMNotNull(resource.getProject());
		Object obj = ifileMap.get(resource.getFullPath());
		if (obj == null) {
			return null;
			// throw new
			// ApplicationException("找不到资源"+resource.getFullPath().toString()+"!请检查Cache方法！");
		}
		return (BMFile) obj;
	}
	public void removeInitProject(IProject project) {
		projectBMFileMap.remove(project);
	}
	public void removeResource(IProject project) {
		// projectBMFileMap.remove(project);
	}
	public Map getProjectBMNotNull(IProject project) {
		Object obj = projectBMFileMap.get(project);
		if (obj == null) {
			obj = new HashMap();
		}
		projectBMFileMap.put(project, obj);
		return (Map) obj;
	}
	public boolean isInitedProject(IProject project) {
		return projectBMFileMap.containsKey(project);
	}
	public Object[] getBMFilesFromResources(IResource[] resources) throws ApplicationException {
		List fileList = new LinkedList();
		BMHierarchyViewerTester test = new BMHierarchyViewerTester();
		for (int i = 0; i < resources.length; i++) {
			IResource child = resources[i];
			if (!test.test(child, null, null, null)) {
				fileList.add(resources[i]);
				continue;
			}
			BMFile bmFile = searchBMLinkFile(child);
			if (bmFile != null) {
				fileList.add(bmFile);
			} else {
				fileList.add(child);
			}
		}
		return fileList.toArray();
	}
}
