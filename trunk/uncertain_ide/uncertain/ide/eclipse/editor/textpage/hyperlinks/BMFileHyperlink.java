package uncertain.ide.eclipse.editor.textpage.hyperlinks;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.ide.IDE;

import uncertain.ide.Activator;
import uncertain.ide.eclipse.project.propertypage.ProjectPropertyPage;
import uncertain.ide.help.AuroraResourceUtil;
import uncertain.ide.help.CustomDialog;

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
		IDocument doc = viewer.getDocument();
		char ch = File.separatorChar;
		String classPath = null;
		try {
			classPath = doc.get(region.getOffset(), region.getLength());
			String bmPath = classPath.replace('.', ch) + ".bm";
			IProject project = AuroraResourceUtil.getIProjectFromActiveEditor();
			String bmFileDir;
			bmFileDir = ProjectPropertyPage.getBMBaseDir(project);
			String fullPath = bmFileDir + File.separator + bmPath;
			IPath path = new Path(fullPath);
			IFile file = null;
			IResource member = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(path);
			if (member != null) {
				path = member.getProjectRelativePath();
				file = project.getFile(path);
			}
			if (file == null) {
				CustomDialog.showErrorMessageBox("获取文件失败！");
				return;
			}
			IDE.openEditor(Activator.getActivePage(), file);
		} catch (Exception e) {
			CustomDialog.showErrorMessageBox(e);
		}
	}
}
