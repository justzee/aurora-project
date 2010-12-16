package uncertain.ide.eclipse.editor.bm;

import java.io.File;
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
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.config.ProjectProperties;
import uncertain.ide.util.Common;
import uncertain.ide.util.LocaleMessage;

public class AuroraProject implements IRunnableWithProgress {
	private String projectFullPath;
	private UncertainEngine uncertainEngine ;
	public AuroraProject(String projectFullPath){
		this.projectFullPath = projectFullPath;
	}
	private UncertainEngine initUncertainProject(String projectFullPath) throws Exception {
		File home_path = new File(projectFullPath);
		File config_path = new File(home_path, "WEB-INF");
		EngineInitiator ei = new EngineInitiator(home_path, config_path);
		ei.init();
		UncertainEngine uncertainEngine = ei.getUncertainEngine();
		return uncertainEngine;
	}
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask(LocaleMessage.getString("try.to.init.aurora.project.engine.please.wait"),
				IProgressMonitor.UNKNOWN);
			try {
				uncertainEngine = initUncertainProject(projectFullPath);
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
		AuroraProject up = new AuroraProject(projectFullPath);
		new ProgressMonitorDialog(null).run(true, true, up);
		return up.getUncertainEngine();
	}
	public static UncertainEngine getUncertainEngine(IProject project) throws Exception {
		String uncertain_project_dir = ProjectProperties.getWebBaseDir(project);
		return getUncertainEngine(uncertain_project_dir);
	}

	public  static void createProjectFile(IProject project,String projectPath) throws CoreException{
		IFile file = project.getFile(ProjectProperties.propertyFileName);
		String fileFullPath = Common.getIfileLocalPath(file);
		File root = new File(fileFullPath);
		if (!root.exists()) {
			try {
				if(root.createNewFile()){
					 Properties props = new Properties();
					 props.put(ProjectProperties.web_base_dir, projectPath);
					 props.store(new FileOutputStream(root), "uncertina project properties");
				}
				
			} catch (IOException e) {
				 CustomDialog.showExceptionMessageBox(e);
			}
		}
		project.refreshLocal(IResource.DEPTH_ONE, null);
	}
	
	public static void main(String[] args){
		try {
			UncertainEngine uncertainEngine = AuroraProject.getUncertainEngine("F:/MyWork/workspace/project/zj/Now/uncertain ide/sample_web");
			System.out.println(uncertainEngine);
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
	}
}
