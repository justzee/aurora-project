package aurora.ide.editor.textpage.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.IEditorPart;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DialogUtil;

public class CFormatAction extends Action {

	private IEditorPart activeEditor;
	private FormatJS formatJS;

	public CFormatAction() {
		this.setActionDefinitionId("aurora.ide.editor.format.collaborateEditor");
		formatJS = new FormatJS();
	}

	@Override
	public void run() {
		try {
			boolean fJS = isInJS();
			if (fJS) {
				formatJS.run(null);
			} else {
				formatXML(null);
			}
		} catch (BadLocationException e) {
		}
	}

	private boolean isInJS() throws BadLocationException {
		TextPage tp = (TextPage) activeEditor;
		IDocument document = tp.getInputDocument();
		ITypedRegion region = document.getPartition(tp.getSelectedRange().x);
		ITypedRegion parentRegion = document
				.getPartition(region.getOffset() - 1);
		String parentNode = document.get(parentRegion.getOffset(),
				parentRegion.getLength());
		if (!XMLPartitionScanner.XML_CDATA.equals(region.getType())
				|| !parentNode.toLowerCase().matches("<script( .*){0,1}>")) {

			return false;
		}
		return true;
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
		formatJS.setActiveEditor(action, targetEditor);
	}

	public void formatXML(IAction action) {
		if (activeEditor == null || !(activeEditor instanceof TextPage)) {
			DialogUtil.showErrorMessageBox("这个类不是" + TextPage.class.getName());
			return;
		}
		TextPage tp = (TextPage) activeEditor;

		IDocument document = tp.getInputDocument();
		String content = document.get();
		if (content == null) {
			return;
		}
		int cursorLine = tp.getCursorLine();
		CompositeLoader cl = AuroraResourceUtil.getCompsiteLoader();
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			CompositeMap data = cl.loadFromStream(is);
			String formatContent = AuroraResourceUtil.xml_decl
					+ XMLOutputter.defaultInstance().toXML(data, true);
			tp.refresh(formatContent);
		} catch (IOException e) {
e.printStackTrace();
		} catch (SAXException e) {
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				DialogUtil.showExceptionMessageBox("关闭" + is + "错误！", e);
			}
		}
		document = tp.getInputDocument();
		;
		try {
			int offset = document.getLineOffset(cursorLine);
			int length = document.getLineLength(cursorLine);
			if (offset == 0 || length == 0)
				return;
			tp.setHighlightRange(offset, length, true);
		} catch (BadLocationException e) {
			DialogUtil.showExceptionMessageBox(e);
		}

	}

}
