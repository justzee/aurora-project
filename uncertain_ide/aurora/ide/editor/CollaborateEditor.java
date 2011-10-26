package aurora.ide.editor;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.ResourceUtil;

import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.XMLTextFileDocumentProvider;
import aurora.ide.editor.textpage.action.CFormatAction;

public class CollaborateEditor extends TextPage {

	public static final String EDITOR_ID = "aurora.ide.editor.CollaborateEditor";

	public CollaborateEditor() {
		this(null, EDITOR_ID, "远程协作编辑器");
	}

	public CollaborateEditor(FormEditor editor, String id, String title) {
		super(editor, id, title);
//		this.setDocumentProvider((IDocumentProvider)null);
		this.setDocumentProvider(new XMLTextFileDocumentProvider());
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId("#TextEditorContext");
		setRulerContextMenuId("#TextRulerContext");
//this.setk
	}

	@Override
	public void refresh(boolean dirty) {
		// do nothing
	}

	@Override
	protected void createActions() {
		super.createActions();
		CFormatAction action = new CFormatAction();
		action.setActiveEditor(null, this);
		IContextService service = (IContextService)this.getSite().getService(IContextService.class);
		Collection activeContextIds = service.getActiveContextIds();
		a.d.d().println(activeContextIds);
		IKeyBindingService keyBindingService = this.getEditorSite().getKeyBindingService();
//		service.
		this.setAction("format", action);
	}

	@Override
	public IFile getFile() {
		return ResourceUtil.getFile(getEditorInput());
	}

	@Override
	public IEditorInput getInput() {
		return super.getInput();
	}

}
