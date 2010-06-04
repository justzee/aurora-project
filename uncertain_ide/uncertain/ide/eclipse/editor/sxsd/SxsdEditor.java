package uncertain.ide.eclipse.editor.sxsd;

import uncertain.ide.eclipse.editor.CompositeMapTreeEditor;

public class SxsdEditor extends CompositeMapTreeEditor {

	public void initTreePage(){
		treePage = new SxsdTreePage(this);
	}
}