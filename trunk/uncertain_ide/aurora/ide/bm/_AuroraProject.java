package aurora.ide.bm;


import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.SystemException;
import aurora.ide.helpers.UncertainEngineUtil;

import uncertain.core.UncertainEngine;

public class _AuroraProject implements IRunnableWithProgress {
	private IProject project;
	private UncertainEngine uncertainEngine ;
	private ApplicationException runtiemException ;
	public Exception getRuntiemException() {
		return runtiemException;
	}
	public _AuroraProject(IProject project){
		this.project = project;
	}
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask(LocaleMessage.getString("try.to.init.aurora.project.engine.please.wait"),
				IProgressMonitor.UNKNOWN);
			try {
				uncertainEngine = UncertainEngineUtil.initUncertainProject(project);
			} catch (ApplicationException e) {
				runtiemException = e;
			}
			monitor.done();

	}
	public UncertainEngine getUncertainEngine() throws ApplicationException{
		try {
			new ProgressMonitorDialog(null).run(true, true, this);
		} catch (InvocationTargetException e) {
			throw new SystemException(e);
		} catch (InterruptedException e) {
			throw new SystemException(e);
		}
		String errorMessage = "启用Uncertain引擎失败!";
		if(uncertainEngine == null){
			if(runtiemException != null){
				throw runtiemException;
			}else{
				throw new ApplicationException(errorMessage);
			}
		}
		return uncertainEngine;		
	}
}
