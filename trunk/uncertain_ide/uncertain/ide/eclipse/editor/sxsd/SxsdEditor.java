package uncertain.ide.eclipse.editor.sxsd;

import uncertain.ide.eclipse.editor.AuroraEditor;

public class SxsdEditor extends AuroraEditor {

	public SxsdEditor() {
		super();
		auroraPage = new SxsdPage(this);
	}
}