package uncertain.ide.eclipse.editor.textpage.Hyperlink;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.wizards.ProjectProperties;

public class BMFileHyperlink implements IHyperlink {
	private IRegion region;
	private ITextViewer viewer;
	
	public BMFileHyperlink(IRegion region, ITextViewer viewer) {
		this.region = region;
		this.viewer = viewer;
	}
	
	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getHyperlinkText() {
		return null;
	}

	public String getTypeLabel() {
		return null;
	}

	public void open() {
		// get doc
		IDocument doc = viewer.getDocument();
		try {
			String path = doc.get(region.getOffset(), region.getLength());
			char ch = File.separatorChar;
			path = path.replace('.', ch);
			path = path+".bm";
			String bmFileDir = ProjectProperties.getBMBaseDir();
			String fullPath = bmFileDir + File.separator + path;
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
			IFile ifile = ((IFileEditorInput) input).getFile();
			IProject project = ifile.getProject();
			String fullFile = (new File(fullPath)).getAbsolutePath();
			String projectFile = (new File(project.getLocation().toOSString())).getAbsolutePath();
			if(fullFile.indexOf(projectFile) == -1){
				return ;
			}
			String filePath = fullFile.substring(projectFile.length());
			IFile java_file = project.getFile(filePath);
			IDE.openEditor(page, java_file);           
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
	}
}
