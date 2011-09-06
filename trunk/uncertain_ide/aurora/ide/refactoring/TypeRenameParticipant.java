package aurora.ide.refactoring;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.ReplaceEdit;

import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.ReferenceSearchService;

public class TypeRenameParticipant extends RenameParticipant {

	private IFile currentSourcefile;
	private String fileExtension;

	public TypeRenameParticipant() {
	}

	protected boolean initialize(Object element) {
		
		if (element instanceof IFile) {
			this.currentSourcefile = (IFile) element;
			fileExtension = ((IFile) element).getFileExtension();
//			return "bm".equalsIgnoreCase(fileExtension)
//					|| "screen".equalsIgnoreCase(fileExtension);
			return false;
		}
		if (element instanceof IFolder) {
			// is web-inf
		}
		return false;
	}

	public String getName() {
		return "AuroraRenameParticipant";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		RefactoringStatus result = new RefactoringStatus();
		String newName = this.getArguments().getNewName();
		if (!newName.toLowerCase().endsWith(fileExtension.toLowerCase())) {
			result.merge(RefactoringStatus.createFatalErrorStatus("文件扩展名错误 : "
					+ newName));
		}

		return result;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		if ("bm".equalsIgnoreCase(fileExtension)) {
			return createBMChange(pm);
		}
		if ("screen".equalsIgnoreCase(fileExtension)) {
			return createScreenChange(pm);
		}
		return null;

	}

	private Change createScreenChange(IProgressMonitor pm) {
		// TODO Auto-generated method stub
		return null;
	}

	private Change createBMChange(IProgressMonitor pm) {
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
			TextFileChange textFileChange = new TextFileChange("File Changed ",
					file);
			textFileChange.setSaveMode(TextFileChange.FORCE_SAVE);
			ReplaceEdit edit = new ReplaceEdit(object.getOriginalOffset(),
					object.getOriginalLength(), this.getArguments()
							.getNewName().replace(".bm", ""));
			textFileChange.setEdit(edit);
			changes.add(textFileChange);
		}
		return changes;
	}

}
