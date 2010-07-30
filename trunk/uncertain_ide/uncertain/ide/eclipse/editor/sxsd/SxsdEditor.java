package uncertain.ide.eclipse.editor.sxsd;

import uncertain.ide.eclipse.editor.CompositeMapTreeEditor;
import uncertain.ide.eclipse.editor.CompositeMapTreePage;

public class SxsdEditor extends CompositeMapTreeEditor {

	public CompositeMapTreePage initTreePage() {
		SxsdTreePage treePage = new SxsdTreePage(this);
		return treePage;
	}


}