/**
 * 
 */
package uncertain.ide.eclipse.editor;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.core.IViewer;
import uncertain.ide.help.AuroraResourceUtil;
import uncertain.ide.help.CustomDialog;

/**
 * @author linjinxiao
 * 
 */
public abstract class CompositeMapPage extends FormPage implements IViewer {
	public CompositeMapPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	public abstract void setContent(CompositeMap content);
	public abstract CompositeMap getContent();
	public abstract String getFullContent();
	protected File getFile() {
		IFile ifile = ((IFileEditorInput) getEditor().getEditorInput()).getFile();
		String fileName = AuroraResourceUtil.getIfileLocalPath(ifile);
		return new File(fileName);
	}
	public void doSave(IProgressMonitor monitor) {
		IFile ifile = ((IFileEditorInput) getEditorInput()).getFile();
		try {
			ifile.refreshLocal(IResource.DEPTH_ZERO, null);
		} catch (CoreException e) {
			CustomDialog.showErrorMessageBox(e);
		}
	}
}
