package sxsd.editor;

import editor.CompositeMapTreeEditor;
import editor.CompositeMapTreePage;

public class SxsdEditor extends CompositeMapTreeEditor {

	public CompositeMapTreePage initTreePage() {
		SxsdTreePage treePage = new SxsdTreePage(this);
		return treePage;
	}


}