package uncertain.ide.eclipse.editor.textpage;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.IViewer;

public class TextPage extends TextEditor implements IViewer {
	protected static final String textPageId = "textPage";
	public static final String textPageTitle = "Source File";
	private boolean syc = false;
	private ColorManager colorManager;
	private FormEditor editor;
	public TextPage(FormEditor editor, String id, String title) {
//		super(editor, id, title);
		this.editor = editor;
		setPartName(title);
		setContentDescription(title);
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}

	public TextPage(FormEditor editor) {
		this(editor, textPageId, textPageTitle);
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getSourceViewer().getTextWidget().addModifyListener(
				new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						if (syc) {
							syc = false;
							return;
						}
						refresh(true);
					}
				});
	}

	public void refresh(boolean dirty) {
		if (dirty)
			getEditor().editorDirtyStateChanged();
	}
	private FormEditor getEditor(){
		return editor;
	}

	public void refresh(String newContent) {
		syc = true;
		if (!newContent.equals(getSourceViewer().getTextWidget().getText())) {
			getSourceViewer().getTextWidget().setText(newContent);
		}
	}
	public String getContent() {
		return getSourceViewer().getTextWidget().getText();
	}

	public boolean canLeaveThePage() {
		if (!checkContentFormat()) {
			return false;
		}
		return true;
	}

	private boolean checkContentFormat() {
		CompositeLoader loader = new CompositeLoader();
		try {
			loader.loadFromString(getContent());
		} catch (IOException e) {
			return false;
		} catch (SAXException e) {
			return false;
		}
		return true;
	}

	public int getCursorLine() {
		return getSourceViewer().getTextWidget().getLineAtOffset(
				getSourceViewer().getSelectedRange().x);
	}

	protected File getFile() {
		IFile ifile = ((IFileEditorInput) getEditor().getEditorInput())
				.getFile();
		String fileName = Common.getIfileLocalPath(ifile);
		return new File(fileName);
	}

	public int getOffsetFromLine(int lineNumber) {
		int offset = 0;
		try {
			offset = getInputDocument().getLineOffset(lineNumber);
		} catch (BadLocationException e) {
			try {
				offset = getInputDocument().getLineOffset(lineNumber - 1);
			} catch (BadLocationException e1) {
			}
		}
		return offset;
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	private IDocument getInputDocument() {
		IDocument document = getDocumentProvider().getDocument(getInput());
		return document;
	}

	public IEditorInput getInput() {
		return getEditorInput();
	}
}