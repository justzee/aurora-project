package aurora.ide.refactoring;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;

public class TextFileChangeManager {
	private Map<IFile, TextFileChange> changeMap = new HashMap<IFile, TextFileChange>();

	public TextFileChange getTextFileChange(IFile file) {
		TextFileChange textFileChange = changeMap.get(file);
		if (textFileChange == null) {
			textFileChange = new TextFileChange("File Changed ", file);
			textFileChange.setSaveMode(TextFileChange.FORCE_SAVE);
			textFileChange.setEdit(new MultiTextEdit());
			changeMap.put(file, textFileChange);
		}
		return textFileChange;
	}

	public Map<IFile, TextFileChange> getChangeMap() {
		return changeMap;
	}

	public TextFileChange[] getAllChanges() {
		return changeMap.values().toArray(new TextFileChange[changeMap.size()]);
	}
}
