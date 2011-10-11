package aurora.ide.refactoring;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

public class ReplaceRefactoring extends Refactoring {

	private RefactoringReplaceInfo[] infos;

	private Map<IFile, TextFileChange> changeMap = new HashMap<IFile, TextFileChange>();

	public ReplaceRefactoring(RefactoringReplaceInfo[] infos) {
		this.infos = infos;
		init(infos);
	}

	private void init(RefactoringReplaceInfo[] lines) {
		// lines[0].g
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		CompositeChange changes = new CompositeChange("aurora changes");
		changes.markAsSynthetic();
		for (RefactoringReplaceInfo info : infos) {

			TextFileChange textFileChange = this.getTextFileChange(info
					.getFile());
			IRegion region = info.getRegion();
			ReplaceEdit child = new ReplaceEdit(region.getOffset(), region.getLength(), info.getReplaceWith());
			textFileChange.addEdit(child);

		}

		changes.addAll(changeMap.values().toArray(
				new TextFileChange[changeMap.size()]));

		return changes;
	}

	private TextFileChange getTextFileChange(IFile file) {
		TextFileChange textFileChange = changeMap.get(file);
		if (textFileChange == null) {
			textFileChange = new TextFileChange("Replace Refactoring", file);
			textFileChange.setSaveMode(TextFileChange.FORCE_SAVE);
			textFileChange.setEdit(new MultiTextEdit());
			changeMap.put(file, textFileChange);
		}
		return textFileChange;
	}

	@Override
	public String getName() {
		return "Replace Refactoring";
	}

}
