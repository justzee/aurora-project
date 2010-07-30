package uncertain.ide.eclipse.editor.bm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import uncertain.core.EngineInitiator;
import uncertain.core.UncertainEngine;
import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.wizards.UncertainWebAppPathDialog;

public class UncertainProject implements IRunnableWithProgress {
	private IProject project;
	private String projectFullPath;
	private UncertainEngine uncertainEngine ;
	public final static String prejectFileNotExistsErrorMessage = "Please define the "
		+ UncertainDataBase.projectFile + " file first !";
	public UncertainProject(IProject project){
		this.project = project;
	}
	public UncertainProject(String projectFullPath){
		this.projectFullPath = projectFullPath;
	}
	private UncertainEngine initUncertainProject(IProject project) throws Exception {
		IFile file = project.getFile(UncertainDataBase.projectFile);
		if (!file.exists()) {
			throw new RuntimeException(prejectFileNotExistsErrorMessage);
		}
		String fileFullPath = Common.getIfileLocalPath(file);
		File root = new File(fileFullPath);
		Properties props = new Properties();
		props.load(new FileInputStream(root));
		String uncertain_project_dir = (String) props
				.get(UncertainDataBase.uncertain_project_dir);
		UncertainEngine  uncertainEngine =  initUncertainProject(uncertain_project_dir);
		return uncertainEngine;
	}
	private UncertainEngine initUncertainProject(String projectFullPath) throws Exception {
		File home_path = new File(projectFullPath);
		File config_path = new File(home_path, "WEB-INF");
		EngineInitiator ei = new EngineInitiator(home_path, config_path);
		ei.init();
		UncertainEngine uncertainEngine = ei.getUncertainEngine();
		return uncertainEngine;
	}
	public UncertainEngine initUncertainProject() throws Exception{
		UncertainEngine uncertainEngine  = null;
		if(project != null)
			uncertainEngine = initUncertainProject(project);
		else if(projectFullPath !=null){
			uncertainEngine = initUncertainProject(projectFullPath);
		}
		return uncertainEngine;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask("Try to init Aurora Project Engine,Please wait...",
				IProgressMonitor.UNKNOWN);
			try {
				uncertainEngine = initUncertainProject();
			} catch (Exception e) {
				/**
				 * Must use throw ,but not CustomDialog,because CustomDialog will create new Shell,
				 * that action will cause error!				
				 */
				throw new RuntimeException(e);
			}
			monitor.done();

	}

	public UncertainEngine getUncertainEngine() {
		return uncertainEngine;
	}
	
	public static UncertainEngine getUncertainEngine(String projectFullPath) throws Exception{
		UncertainProject up = new UncertainProject(projectFullPath);
		new ProgressMonitorDialog(null).run(true, false, up);
		return up.getUncertainEngine();
	}
	public static UncertainEngine getUncertainEngine(IProject project) throws Exception{
		UncertainProject up = new UncertainProject(project);
		try {
			new ProgressMonitorDialog(null).run(true, false, up);
		} catch (InvocationTargetException e) {
			String message = CustomDialog.getExceptionMessage(e);
			if(message != null && (message.indexOf(UncertainProject.prejectFileNotExistsErrorMessage)!=-1)){
				if(UncertainWebAppPathDialog.createWebAppDialog(project)){
					new ProgressMonitorDialog(null).run(true, false, up);
				}
			}
		}
		return up.getUncertainEngine();
	}
	
	public static void main(String[] args){
		try {
			UncertainEngine uncertainEngine = UncertainProject.getUncertainEngine("F:/MyWork/workspace/project/zj/Now/uncertain ide/sample_web");
			System.out.println(uncertainEngine);
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
	}
	public  static void createProjectFile(IProject project,String projectPath) throws CoreException{
		IFile file = project.getFile(UncertainDataBase.projectFile);
		String fileFullPath = Common.getIfileLocalPath(file);
		File root = new File(fileFullPath);
		if (!root.exists()) {
			try {
				if(root.createNewFile()){
					 Properties props = new Properties();
					 props.put(UncertainDataBase.uncertain_project_dir, projectPath);
					 props.store(new FileOutputStream(root), "uncertina project properties");
				}
				
			} catch (IOException e) {
				 CustomDialog.showExceptionMessageBox(e);
			}
		}
		project.refreshLocal(IResource.DEPTH_ONE, null);
	}
}
