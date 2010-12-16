/**
 * 
 */
package uncertain.ide.util;

import org.eclipse.core.resources.IFile;

public class Common {

	public static String getIfileLocalPath(IFile ifile) {
		String fileFullPath = ifile.getLocation().toOSString();
		return fileFullPath;
	}

}
