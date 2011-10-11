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

import aurora.ide.search.core.CompositeMapInDocument;

public class RemoveElementRefactoring extends Refactoring {

	private CompositeMapInDocument[] lines;

	private Map<IFile, TextFileChange> changeMap = new HashMap<IFile, TextFileChange>();

	public RemoveElementRefactoring(CompositeMapInDocument[] lines) {
		this.lines = lines;
		init(lines);
	}

	private void init(CompositeMapInDocument[] lines) {
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
		for (CompositeMapInDocument line : lines) {

			TextFileChange textFileChange = this.getTextFileChange(line
					.getFile());
			IRegion start = line.getStart();
			IRegion end = line.getEnd();
			int length = end.getOffset() - start.getOffset() + end.getLength();
			ReplaceEdit child = new ReplaceEdit(start.getOffset(), length, "");
			textFileChange.addEdit(child);

		}

		changes.addAll(changeMap.values().toArray(
				new TextFileChange[changeMap.size()]));

		return changes;
	}

	private TextFileChange getTextFileChange(IFile file) {
		TextFileChange textFileChange = changeMap.get(file);
		if (textFileChange == null) {
			textFileChange = new TextFileChange("Remove Element", file);
			textFileChange.setSaveMode(TextFileChange.FORCE_SAVE);
			textFileChange.setEdit(new MultiTextEdit());
			changeMap.put(file, textFileChange);
		}
		return textFileChange;
	}

	@Override
	public String getName() {
		return "Remove ELement";
	}

}
