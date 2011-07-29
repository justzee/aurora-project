package editor.textpage;

import helpers.AuroraResourceUtil;
import helpers.DialogUtil;
import helpers.ExceptionUtil;
import helpers.LocaleMessage;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.IEncodingSupport;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.internal.util.Util;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.MarkerRulerAction;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import editor.core.IViewer;
import editor.textpage.js.validate.JavascriptDocumentListener;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

public class TextPage extends TextEditor implements IViewer {
	/** The ID of this editor as defined in plugin.xml */
	public static final String EDITOR_ID = "aurora.ide.editor.textpage";

	/** The ID of the editor context menu */
	public static final String EDITOR_CONTEXT = EDITOR_ID + ".context";

	/** The ID of the editor ruler context menu */
	public static final String RULER_CONTEXT = EDITOR_CONTEXT + ".ruler";

	public static final String AnnotationType = "aurora.ide.text.valid";
	protected static final String textPageId = "textPage";
	public static final String textPageTitle = LocaleMessage
			.getString("source.file");
	private boolean syc = false;
	private ColorManager colorManager;
	private FormEditor editor;
	private boolean modify = false;
	private boolean ignorceSycOnce = false;
	private List annotatioList = new LinkedList();
	private IAnnotationModel annotationModel;

	public TextPage() {
		super();
	}

	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT);
		setRulerContextMenuId(RULER_CONTEXT);
	}

	// add by shiliyan
	public Object getAdapter(Class adapter) {
		if (Display.getCurrent() != null
				&& IAnnotationModel.class.equals(adapter)) {
			return this.getAnnotationModel();
		}
		return super.getAdapter(adapter);
	}

	// add by shiliyan
	public boolean isIgnorceSycOnce() {
		return ignorceSycOnce;
	}

	public void setIgnorceSycOnce(boolean ignorceSycOnce) {
		this.ignorceSycOnce = ignorceSycOnce;
	}

	public TextPage(FormEditor editor, String id, String title) {
		// super(editor, id, title);
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

	private IAnnotationModel getAnnotationModel() {
		if (annotationModel != null)
			return annotationModel;
		annotationModel = getDocumentProvider().getAnnotationModel(getInput());
		if (annotationModel == null) {
			annotationModel = new AnnotationModel();
			annotationModel.connect(getInputDocument());
		}
		return annotationModel;
	}

	private void clearHistory() {
		for (Iterator it = annotatioList.iterator(); it.hasNext();) {
			annotationModel.removeAnnotation((Annotation) it.next());
		}
		annotatioList.clear();
	}

	private void updateAnnotation(SAXException e) {
		Throwable rootCause = ExceptionUtil.getRootCause(e);
		if (rootCause == null || !(rootCause instanceof SAXParseException))
			return;
		SAXParseException parseEx = (SAXParseException) e;
		String errorMessage = ExceptionUtil.getExceptionTraceMessage(e);
		int lineNum = parseEx.getLineNumber() - 1;
		int lineOffset = getOffsetFromLine(lineNum);
		int lineLength = Math.max(getLengthOfLine(lineNum), 1);
		Position pos = new Position(lineOffset, lineLength);
		Annotation annotation = new Annotation(AnnotationType, false,
				errorMessage);
		annotationModel.addAnnotation(annotation, pos);
		annotatioList.add(annotation);
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		// add by shiliyan
		if ("screen".equals(this.getFile().getFileExtension())) {
			getInputDocument().addDocumentListener(
					new JavascriptDocumentListener(this));
		}

		// add by shiliyan
		getInputDocument().addDocumentListener(new IDocumentListener() {

			public void documentChanged(DocumentEvent event) {
				if (syc) {
					syc = false;
					return;
				}
				annotationModel = getAnnotationModel();
				clearHistory();
				try {
					AuroraResourceUtil.getCompsiteLoader().loadFromString(
							getInputDocument().get(), "UTF-8");
				} catch (IOException e) {
					DialogUtil.showExceptionMessageBox(e);
				} catch (SAXException e) {
					updateAnnotation(e);
				}
				refresh(true);
			}

			public void documentAboutToBeChanged(DocumentEvent event) {

			}
		});
		// getSourceViewer().addTextListener(new ITextListener() {
		// public void textChanged(TextEvent event) {
		// if (syc) {
		// syc = false;
		// return;
		// }
		// // 过滤超链接等事件触发
		// if (event.getDocumentEvent() == null) {
		// return;
		// }
		// refresh(true);
		// }
		// });
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		ProjectionSupport projectionSupport = new ProjectionSupport(viewer,
				getAnnotationAccess(), getSharedColors());
		projectionSupport.install();
		// turn projection mode on
		viewer.doOperation(ProjectionViewer.TOGGLE);

	}

	public void refresh(boolean dirty) {
		if (dirty) {
			getEditor().editorDirtyStateChanged();
			setModify(true);
		}
	}

	private FormEditor getEditor() {
		return editor;
	}

	public void refresh(String newContent) {
		if (!newContent.equals(getSourceViewer().getTextWidget().getText())) {
			syc = true;
			getSourceViewer().getTextWidget().setText(newContent);
		}
	}

	public void setSyc(boolean isSyc) {
		syc = isSyc;
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
		CompositeMap content = null;
		CompositeLoader loader = AuroraResourceUtil.getCompsiteLoader();
		;
		try {
			content = loader.loadFromString(getContent());
		} catch (IOException e) {
			return false;
		} catch (SAXException e) {
			return false;
		}
		getSourceViewer().getTextWidget().setText(content.toXML());
		return true;
	}

	public int getCursorLine() {
		return getSourceViewer().getTextWidget().getLineAtOffset(
				getSourceViewer().getSelectedRange().x);
	}

	public Point getSelectedRange() {
		return getSourceViewer().getSelectedRange();
	}

	public IFile getFile() {
		IFile ifile = ((IFileEditorInput) getEditor().getEditorInput())
				.getFile();
		return ifile;
	}

	public int getOffsetFromLine(int lineNumber) {
		int offset = 0;
		if (lineNumber < 0)
			return offset;
		try {
			offset = getInputDocument().getLineOffset(lineNumber);
			if (offset >= getInputDocument().getLength())
				return getOffsetFromLine(lineNumber - 1);
		} catch (BadLocationException e) {
			return getOffsetFromLine(lineNumber - 1);
		}
		return offset;
	}

	public int getLineOfOffset(int offset) {
		try {
			return getInputDocument().getLineOfOffset(offset);
		} catch (BadLocationException e) {
			return -1;
		}
	}

	public int getLengthOfLine(int lineNumber) {
		int length = 0;
		if (lineNumber < 0)
			return length;
		try {
			length = getInputDocument().getLineLength(lineNumber);
		} catch (BadLocationException e) {
			try {
				length = getInputDocument().getLineLength(lineNumber - 1);
			} catch (BadLocationException e1) {
			}
		}
		return length;
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	public IDocument getInputDocument() {
		IDocument document = getDocumentProvider().getDocument(getInput());
		return document;
	}

	public IEditorInput getInput() {
		return getEditorInput();
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.AbstractTextEditor#createSourceViewer(org.eclipse
	 * .swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler,
	 * int)
	 */
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		ISourceViewer viewer = new ProjectionViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);

		return viewer;
	}

	protected void createActions() {
		super.createActions();
		// 必须手工加，而不能通过pulgin.xml配置，因为textEditor
		// 作为MultiPageEditorPart后，在点击左侧垂直条的时候，AbstractTextEditor.findContributedAction()中getSite().getId()总是为"",判断失效。
		Action action = new MarkerRulerAction(
				ResourceBundle
						.getBundle("org.eclipse.ui.texteditor.ConstructedTextEditorMessages"),
				"Editor.ManageBookmarks.", this, getVerticalRuler(),
				IMarker.BOOKMARK, true);
		setAction(ITextEditorActionConstants.RULER_DOUBLE_CLICK, action);
	}
}