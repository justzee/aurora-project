package aurora.ide.refactoring;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;

import aurora.ide.search.core.Util;
import aurora.ide.search.reference.ReferenceSearchService;

public class BMDeleteParticipant extends DeleteParticipant {

	private IFile currentSourcefile;
	private String fileExtension;

	public BMDeleteParticipant() {
	}

	protected boolean initialize(Object element) {
		if (element instanceof IFile) {
			this.currentSourcefile = (IFile) element;
			fileExtension = ((IFile) element).getFileExtension();
			return "bm".equalsIgnoreCase(fileExtension);
		}
		return false;
	}

	public String getName() {
		return "BM Delete Participant";
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

	private List findRelations(IProgressMonitor pm) {
		IResource scope = Util.getScope(currentSourcefile);
		ReferenceSearchService seachService = new ReferenceSearchService(scope,
				currentSourcefile, null);
		seachService.setPostException(false);
		return seachService.service(pm);

	}
}
