package aurora.ide.refactoring;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.ReferenceSearchService;

public class BMMoveParticipant extends MoveParticipant {
	private IFile currentSourcefile;
	private String fileExtension;
	private TextFileChangeManager changeManager;
	private IFolder moveTO;
	private boolean check;

	public BMMoveParticipant() {
	}

	@Override
	protected boolean initialize(Object element) {
		Object destination = this.getArguments().getDestination();
		if (element instanceof IFile && destination instanceof IFolder) {
			this.currentSourcefile = (IFile) element;
			fileExtension = ((IFile) element).getFileExtension();
			changeManager = new TextFileChangeManager();
			moveTO = (IFolder) destination;
			if (currentSourcefile.getParent().equals(moveTO)
					|| !currentSourcefile.getProject().equals(
							moveTO.getProject())) {
				// 目标目录相同，不参与
				// 工程不同不参与
				return false;
			}
			return "bm".equalsIgnoreCase(fileExtension);
		}
		return false;
	}

	@Override
	public String getName() {
		return "BM Move Participant";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		this.check = true;
		RefactoringStatus result = new RefactoringStatus();
		Object destination = this.getArguments().getDestination();
		if (destination instanceof IResource) {
			IPath path = ((IResource) destination).getProjectRelativePath();
			String pkg = Util.toRelativeClassesPKG(path);
			if (pkg.length() == 0) {
				this.check = false;
				result.merge(RefactoringStatus
						.createInfoStatus("目标目录不属于classes,Aurora重构不会进行,请Cancel后重新选择。"));
			}
		}
		return result;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		// bug:select->preview->back->select another->preview
		// getArguments().getDestination() did nont changed
		if (check) {
			return createBMChange(pm);
		}
		return null;
	}

	private Change createBMChange(IProgressMonitor pm) throws CoreException {
		IResource scope = Util.getScope(currentSourcefile);
		ReferenceSearchService seachService = new ReferenceSearchService(scope,
				currentSourcefile, null);
		seachService.setPostException(false);
		List relations = seachService.service(pm);
		CompositeChange changes = new CompositeChange("aurora changes");
		changes.markAsSynthetic();
		for (int i = 0; i < relations.size(); i++) {
			AbstractMatch object = (AbstractMatch) relations.get(i);
			IFile file = (IFile) object.getElement();
			TextFileChange textFileChange = changeManager
					.getTextFileChange(file);
			textFileChange.addEdit(createTextEdit(object));
		}
		changes.addAll(changeManager.getAllChanges());
		return changes;
	}

	public TextEdit createTextEdit(AbstractMatch match) throws CoreException {
		int offset = match.getOriginalOffset();
		int length = match.getOriginalLength();
		String fileName = currentSourcefile.getProjectRelativePath()
				.removeFileExtension().lastSegment();
		IPath newPath = this.moveTO.getProjectRelativePath().append(fileName);
		String newPkg = Util.toRelativeClassesPKG(newPath);
		ReplaceEdit edit = new ReplaceEdit(offset, length, newPkg);
		return edit;
	}

	public IDocument getDocument(IFile file) throws CoreException {
		return CacheManager.getDocumentCacher().getDocument(file);
	}

}
