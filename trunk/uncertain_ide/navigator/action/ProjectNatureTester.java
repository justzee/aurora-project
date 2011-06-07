package navigator.action;

import ide.AuroraProjectNature;
import helpers.DialogUtil;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;


public class ProjectNatureTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		
		if (!(receiver instanceof IProject))
			return false;
		IProject proejct = (IProject)receiver;
		if(!proejct.isOpen())
			return false;
		if(args == null ||args.length<1)
			return false;
		Object arg = args[0];
		try {
			return arg.equals(new Boolean(AuroraProjectNature.hasAuroraNature(proejct)));
		} catch (CoreException e) {
			DialogUtil.showExceptionMessageBox(e);
			return false;
		}
	}
}
