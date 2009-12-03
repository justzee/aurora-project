package uncertain.ide.eclipse.editor.sxsd;

import uncertain.ide.eclipse.editor.MainFormEditor;

public class SxsdEditor extends MainFormEditor {

	public SxsdEditor() {
		super();
		mainFormPage = new SxsdPage(this);
	}
}