package aurora.ide.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;

import aurora.ide.search.core.Util;
import aurora.ide.search.reference.MultiSourceReferenceSearchService;
import aurora.ide.search.reference.ReferenceSearchService;

public class FolderDeleteParticipant extends DeleteParticipant {

	private IFolder currentFolder;
	private ScopeVisitor visitor;

	public FolderDeleteParticipant() {
	}

	protected boolean initialize(Object element) {
		// ifnature in
		if (element instanceof IFolder) {
			currentFolder = (IFolder) element;
			visitor = new ScopeVisitor();
			return isTakeIn(currentFolder);
		}
		return false;
	}

	private boolean isTakeIn(IFolder currentFolder) {
		try {
			currentFolder.accept(visitor);
			return visitor.isTakeIn();
		} catch (CoreException e) {
		}
		return false;
	}

	public String getName() {
		return "Folder Delete Participant";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		RefactoringStatus result = new RefactoringStatus();
		List findRelations = this.findRelations(pm);
		if (findRelations.size() > 0) {
			result.merge(RefactoringStatus.createInfoStatus("删除的文件会影响其他文件。"));
		}
		return result;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return null;
	}

	private class ScopeVisitor implements IResourceVisitor {
		private List<IFile> result = new ArrayList<IFile>();

		private boolean isTakeIn = true;

		public boolean visit(IResource resource) throws CoreException {
			if (resource.getType() == IResource.FILE) {
				boolean checkExtension = checkExtension(resource);
				if (checkExtension) {
					result.add((IFile) resource);
				}
				return false;
			}
			if (resource.getType() == IResource.FOLDER) {
				IFolder f = (IFolder) resource;
				//
				// TODO bm folder in properties page
				if ("classes".equals(f.getName())) {
					isTakeIn = false;
				}
			}
			return true;
		}

		public boolean isTakeIn() {
			return isTakeIn && result.size() > 0;
		}

		public List<IFile> getResult() {
			return result;
		}

		private boolean checkExtension(IResource resource) {
			IFile file = (IFile) resource;
			String fileExtension = file.getFileExtension();
			return "bm".equalsIgnoreCase(fileExtension);
		}
	}

	private List findRelations(IProgressMonitor pm) {
		List<IFile> result = visitor.getResult();
		IResource scope = Util.getScope(currentFolder);
		ReferenceSearchService seachService = new MultiSourceReferenceSearchService(
				scope, result.toArray(new IFile[result.size()]), null);
		seachService.setPostException(false);
		List relations = seachService.service(pm);
		return relations;
	}
}
