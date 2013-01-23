package aurora.ide.bm.editor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

public class BMEditorActionBarContributor extends
		MultiPageEditorActionBarContributor {

	@Override
	public void setActivePage(IEditorPart activeEditor) {
//		this.setActiveEditor(activeEditor);
		System.out.println(activeEditor);
	}

}
