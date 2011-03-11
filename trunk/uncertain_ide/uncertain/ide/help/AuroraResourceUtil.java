/**
 * 
 */
package uncertain.ide.help;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.EngineInitiator;
import uncertain.core.UncertainEngine;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.project.propertypage.ProjectPropertyPage;
import uncertain.ocm.IObjectRegistry;
import aurora.ide.AuroraConstant;

public class AuroraResourceUtil{
    
	public static final String LineSeparator = System.getProperty("line.separator");
	public static final String xml_decl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
	public static String getIfileLocalPath(IFile ifile) {
		String fileFullPath = ifile.getLocation().toOSString();
		return fileFullPath;
	}
	public static String getLocalPathFromIPath(IPath path){
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		return resource.getLocation().toOSString();
		
	}
	public static IResource getIResourceSelection() {
		return getIResourceFromIStructuredSelection(getStructuredSelection());
	}
	public static IResource getIResourceFromIStructuredSelection(IStructuredSelection selection) {
		if(selection == null)
			return null;
		StructuredSelection currentSelection = new StructuredSelection(IDE
				.computeSelectedResources(selection));
		Iterator it = currentSelection.iterator();
		if (it.hasNext()) {
			Object object = it.next();
			IResource selectedResource = null;
			if (object instanceof IResource) {
				selectedResource = (IResource) object;
			} else if (object instanceof IAdaptable) {
				selectedResource = (IResource) ((IAdaptable) object)
						.getAdapter(IResource.class);
			}
			if (selectedResource != null) {
				if (selectedResource.getType() == IResource.FILE) {
					selectedResource = selectedResource.getParent();
				}
				if (selectedResource.isAccessible()) {
					return selectedResource;
				}
			}
		}
		return null;
	}
	public static IStructuredSelection getStructuredSelection(){
		IStructuredSelection selectionToPass = Activator.getDefault().getStructuredSelection();
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return selectionToPass;
        ISelection selection = window.getSelectionService().getSelection();
        if (selection instanceof IStructuredSelection) {
        	selectionToPass = (IStructuredSelection)selection;
        }else{
            // Build the selection from the IFile of the editor
            IWorkbenchPart part = window.getPartService().getActivePart();
            if (part instanceof IEditorPart) {
                IEditorInput input = ((IEditorPart) part).getEditorInput();
                Class fileClass = IFile.class;
                if (input != null && fileClass != null) {
                    Object file = Platform.getAdapterManager().getAdapter(input, fileClass);
                    if (file != null) {
                        selectionToPass = new StructuredSelection(file);
                    }
                }
            }
        }
        return selectionToPass;
	}
	public static IProject getIProjectFromSelection(){
		IResource selection =  getIResourceSelection();
		if(selection != null)
			return selection.getProject();
		return null;
	}
	public static IProject getIProjectFromActiveEditor() {
		IEditorInput input = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getEditorInput();
		IFile ifile = ((IFileEditorInput) input).getFile();
		IProject project = ifile.getProject();
		return project;
	}
	public static UncertainEngine initUncertainProject(IProject project) throws ApplicationException{
		String webBasePath = ProjectPropertyPage.getWebBaseLocalDir(project);
		if(webBasePath == null)
			return null;
		File home_path = new File(webBasePath);
		File config_path = new File(home_path, "WEB-INF");
		EngineInitiator ei = new EngineInitiator(home_path, config_path);
		try {
			ei.init();
		} catch (Exception e) {
			throw new ApplicationException("启用EngineInitiator失败!",e);
		}
		UncertainEngine uncertainEngine = ei.getUncertainEngine();
		return uncertainEngine;
	}
	public static Connection getDBConnection(IProject project) throws ApplicationException{
		UncertainEngine ue = initUncertainProject(project);
		if(ue == null)
			throw new ApplicationException("获取UncertainProject失败!");
		IObjectRegistry mObjectRegistry = ue.getObjectRegistry();
		DataSource ds = (DataSource) mObjectRegistry
				.getInstanceOfType(DataSource.class);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ApplicationException("获取数据库连接失败!请查看"+AuroraConstant.DbConfigFileName+"是否配置正确.",e);
		}
		return conn;
	}
	public static String getRegisterPath(IFile file) throws ApplicationException{
		if(file == null)
			return null;
		char fileSeparatorChar = '/';
		if(file.getName().endsWith("."+AuroraConstant.ScreenFileExtension)){
			String fileName = AuroraResourceUtil.getIfileLocalPath(file);
			String rootDir = ProjectPropertyPage.getWebBaseLocalDir(file.getProject());
			int webLocation = fileName.indexOf(rootDir);
			String registerPath = fileName.substring(webLocation + rootDir.length()+1);
			registerPath = registerPath.replace(File.separatorChar, fileSeparatorChar);
			return registerPath;
		}else if(file.getName().endsWith("."+AuroraConstant.BMFileExtension)){
				String fileName = AuroraResourceUtil.getIfileLocalPath(file);
				String rootDir = ProjectPropertyPage.getBMBaseLocalDir(file.getProject());
				int webLocation = fileName.indexOf(rootDir);
				int endIndex = fileName.lastIndexOf(".");
				String registerPath = fileName.substring(webLocation + rootDir.length()+1,endIndex);
				registerPath = registerPath.replace(File.separatorChar, '.');
				return registerPath;
			}
	  return file.getName();
			
	}
	public static CompositeMap loadFromResource(IResource file) throws ApplicationException {
		if(file == null || !file.exists()){
			return null;
		}
		String fullLocationPath =file.getLocation().toOSString();
		CompositeLoader cl = CompositeLoader.createInstanceForOCM();
		cl.setSaveNamespaceMapping(true);
		CompositeMap bmData;
		try {
			bmData = cl.loadByFile(fullLocationPath);
		} catch (IOException e) {
			throw new ApplicationException("文件路径"+fullLocationPath+"不存在!",e);
		} catch (SAXException e) {
			throw new ApplicationException("文件"+fullLocationPath+"格式不正确!",e);
		}
		return bmData;
	}
	public static List getBMSFromProject(IProject project) throws ApplicationException{
		List bmList = new LinkedList();
		if(project == null)
			return bmList;
		String bmBaseDir = ProjectPropertyPage.getBMBaseDir(project);
		if(bmBaseDir == null)
			return bmList;
		IResource bmRoot = ResourcesPlugin.getWorkspace().getRoot().findMember(bmBaseDir);
		if(bmRoot == null || !bmRoot.exists()||(!(bmRoot instanceof IContainer)))
			return bmList;
		IContainer parent = (IContainer)bmRoot;
		iteratorResource(parent,bmList);
		return bmList;
	}
	private static void iteratorResource(IContainer parent,List bmList){
		try {
			IResource[] childs =  parent.members();
			for(int i=0;i<childs.length;i++){
				IResource child = childs[i];
				if(child.exists()&&child.getName().toLowerCase().endsWith("."+AuroraConstant.BMFileExtension)){
					bmList.add(child);
				}
				if(child instanceof IContainer){
					iteratorResource((IContainer)child,bmList);
				}
			}
		} catch (CoreException e) {
			CustomDialog.showExceptionMessageBox(e);
		}
	}
	public static CompositeLoader getCompsiteLoader(){
		CompositeLoader cl = CompositeLoader.createInstanceForOCM();
		cl.setSaveNamespaceMapping(true);
		cl.setSupportXInclude(false);
		
		CompositeLoader projectCl = CompositeLoader.createInstanceForOCM();
		projectCl.setSaveNamespaceMapping(true);
		projectCl.setSupportXInclude(false);
		IProject project = getIProjectFromSelection();
		if(project != null){
			projectCl.setBaseDir(project.getLocation().toFile().getParent().toString()+File.separator);
			cl.addExtraLoader(projectCl);
		}
		CompositeLoader curentDircl = CompositeLoader.createInstanceForOCM();
		curentDircl.setSaveNamespaceMapping(true);
		curentDircl.setSupportXInclude(false);
		IFile currentFile = getFileFromSelection();
		if(currentFile != null){
			curentDircl.setBaseDir(currentFile.getLocation().toOSString());
			cl.addExtraLoader(curentDircl);
		}
		return cl;
	}
	public static IFile getFileFromSelection(){
		IStructuredSelection selection = Activator.getDefault().getStructuredSelection();
		if(selection == null || !(selection instanceof IFile))
			return null;
		return (IFile)selection;
	}
}
