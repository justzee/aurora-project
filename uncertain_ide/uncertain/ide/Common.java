/**
 * 
 */
package uncertain.ide;

import org.eclipse.core.resources.IFile;

public class Common {

	public static void main(String[] args) {
	}

	public static String getIfileLocalPath(IFile ifile) {
		String fileFullPath = ifile.getLocation().toOSString();
		return fileFullPath;
	}

}
