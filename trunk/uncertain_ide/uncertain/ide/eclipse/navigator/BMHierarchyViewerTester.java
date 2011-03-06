package uncertain.ide.eclipse.navigator;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import uncertain.ide.help.CustomDialog;
import aurora.ide.AuroraConstant;

public class BMHierarchyViewerTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof BMFile)
			return true;
		if (receiver instanceof IFile){
			IFile file = (IFile)receiver;
			if(file.getName().toLowerCase().endsWith("."+AuroraConstant.BMFileExtension)){
				return true;
			}
		}
		if (!(receiver instanceof IContainer))
			return false;
		IContainer container = (IContainer) receiver;
		try {
			return isValidDir(container);
		} catch (CoreException e) {
			CustomDialog.showErrorMessageBox(e);
		}
		return false;
	}

	private  boolean isValidDir(IContainer container) throws CoreException {
		IResource[] childs = container.members();
		for(int i= 0;i<childs.length;i++){
			IResource child = childs[i];
			if(child.getName().toLowerCase().endsWith("."+AuroraConstant.BMFileExtension)){
				return true;
			}
		}
		return false;
	}
}
