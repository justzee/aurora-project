package uncertain.ide.eclipse.action;

import java.io.File;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IResource;

import uncertain.pkg.PackageManager;

public class VisablePropertyTester extends PropertyTester {
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if(!(receiver instanceof IResource))
			return false;
		IResource type = (IResource) receiver;
		File selectedFile = type.getLocation().toFile();
        if(!selectedFile.isDirectory())
            return false;
		File[] files = selectedFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory() && PackageManager.isPackageDirectory(file))
				return true;
		}
		return false;

	}
}
