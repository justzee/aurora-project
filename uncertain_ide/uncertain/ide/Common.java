/**
 * 
 */
package uncertain.ide;

import org.eclipse.core.resources.IFile;

public class Common {

	/**
	 * @param args
	 */
	public static final String ServiceEditor = "uncertain.ide.eclipse.editor.ServiceEditor";
	public static final String NewProjectFile = "uncertain.service";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static String getIfileLocalPath(IFile ifile) {
		String fileFullPath = ifile.getProject().getLocation().toString()+"/"
				+ ifile.getProjectRelativePath().toString();
		return fileFullPath;
	}

}
