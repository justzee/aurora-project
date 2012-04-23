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

import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;
import aurora.bm.BusinessModel;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.bm.ExtendModelFactory;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;

public class CompositeMapCacher implements IResourceChangeListener,
		IResourceDeltaVisitor {

	private class ProjectCatcher {
		private Map<IFile, CompositeMap> catchMap = new HashMap<IFile, CompositeMap>();

		private Map<IFile, CompositeMap> wholeBMMap = new HashMap<IFile, CompositeMap>();

		private IProject project;

		private ProjectCatcher(IProject project) {
			this.project = project;
		}

		private synchronized CompositeMap getCompositeMap(IFile file)
				throws CoreException, ApplicationException {
			CompositeMap map = catchMap.get(file);
			if (map == null) {
				map = load(file);
				if (map != null) {
					catchMap.put(file, map);
				}
			}
			return map;
		}

		private synchronized CompositeMap getWholeCompositeMap(IFile file)
				throws CoreException, ApplicationException {
			CompositeMap map = wholeBMMap.get(file);
			if (map == null) {
				map = loadWholeBM(file);
				if (map != null) {
					// wholeBMMap.put(file, map);
				}
			}
			return map;
		}

		private IProject getProject() {
			return project;
		}

		private CompositeMap loadWholeBM(IFile file) throws CoreException,
				ApplicationException {
			CompositeMap bm = ((CacheCompositeMap) getCompositeMap(file))
					.getRealMap();
			BusinessModel r = createResult(bm, file);
			return new CacheCompositeMap(
					(CommentCompositeMap) r.getObjectContext());
		}

		private BusinessModel createResult(CompositeMap config, IFile file) {
			ExtendModelFactory factory = new ExtendModelFactory(
					OCManager.getInstance(), file);
			return factory.getModel(config);
		}

		private CompositeMap load(IFile file) throws CoreException,
				ApplicationException {

			IDocument document = CacheManager.getDocumentCacher().getDocument(
					file);
			if (document == null)
				return null;
			CompositeMap loaderFromString = CompositeMapUtil
					.loaderFromString(document.get());
			return new CacheCompositeMap((CommentCompositeMap) loaderFromString);
		}

		private synchronized CompositeMap remove(IFile file) {
			// wholeBMMap.remove(file);
			return catchMap.remove(file);
		}
	}

	private Map<IProject, ProjectCatcher> catcher = new HashMap<IProject, ProjectCatcher>();

	public CompositeMapCacher() {
		AuroraPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public CompositeMap getCompositeMap(IFile file) throws CoreException,
			ApplicationException {
		ProjectCatcher projectCatcher = getProjectCatcher(file);
		return (CompositeMap) projectCatcher.getCompositeMap(file);
	}

	public CompositeMap getWholeCompositeMap(IFile file) throws CoreException,
			ApplicationException {
		ProjectCatcher projectCatcher = getProjectCatcher(file);
		return (CompositeMap) projectCatcher.getWholeCompositeMap(file);
	}

	private CompositeMap remove(IFile file) {
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
		if (delta == null)
			return;
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
