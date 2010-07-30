/**
 * 
 */
package uncertain.ide;

import org.eclipse.core.resources.IFile;

public class Common {

	public static void main(String[] args) {
	}

	public static String getIfileLocalPath(IFile ifile) {
		String fileFullPath = ifile.getProject().getLocation().toString() + "/"
				+ ifile.getProjectRelativePath().toString();
		return fileFullPath;
	}

}
