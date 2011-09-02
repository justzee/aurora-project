package aurora.ide.search.cache;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.textpage.XMLDocumentProvider;

public class DocumentCacher implements IResourceChangeListener,
		IResourceDeltaVisitor {

	private class ProjectCatcher {
		private Map<IFile, IDocument> catchMap = new HashMap<IFile, IDocument>();
		private IProject project;

		private ProjectCatcher(IProject project) {
			this.project = project;
		}

		private synchronized IDocument getDocument(IFile file)
				throws CoreException {
			IDocument document = catchMap.get(file);
			if (document == null) {
				document = load(file);
				if (document != null) {
					catchMap.put(file, document);
				}
			}
			return document;
		}

		private IProject getProject() {
			return project;
		}

		protected IDocument load(IFile file) throws CoreException {
			FileEditorInput element = new FileEditorInput(file);
			XMLDocumentProvider provider = new XMLDocumentProvider();
			provider.connect(element);
			IDocument document = provider.getDocument(element);
			return document;

		}

		private synchronized IDocument remove(IFile file) {
			return catchMap.remove(file);
		}
	}

	private Map<IProject, ProjectCatcher> catcher = new HashMap<IProject, ProjectCatcher>();

	public DocumentCacher() {
		AuroraPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public IDocument getDocument(IFile file) throws CoreException {
		ProjectCatcher projectCatcher = getProjectCatcher(file);
		return projectCatcher.getDocument(file);
	}

	private IDocument remove(IFile file) {
		return getProjectCatcher(file).remove(file);
	}

	private synchronized ProjectCatcher getProjectCatcher(IFile file) {
		IProject project = file.getProject();
		ProjectCatcher projectCatcher = catcher.get(project);
		if (projectCatcher == null) {
			projectCatcher = new ProjectCatcher(project);
			catcher.put(project, projectCatcher);
		}
		return projectCatcher;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		try {
			delta.accept(this);
		} catch (CoreException e) {
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			this.remove(file);
			return false;
		}
		return true;
	}

}
