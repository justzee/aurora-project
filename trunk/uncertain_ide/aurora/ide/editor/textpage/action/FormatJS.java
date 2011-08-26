package aurora.ide.editor.textpage.action;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.format.JSBeautifier;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.helpers.DialogUtil;

import uncertain.composite.XMLOutputter;

public class FormatJS implements IEditorActionDelegate {

	IEditorPart activeEditor ;
	public FormatJS() {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
	}

	public void run(IAction action) {
		if(activeEditor == null||!(activeEditor instanceof TextPage)){
			DialogUtil.showErrorMessageBox("这个类不是"+TextPage.class.getName());
			return;
		}
		TextPage tp = (TextPage)activeEditor;
		IDocument document = tp.getInputDocument();
		int cursorLine = tp.getCursorLine();
		try {
			ITypedRegion region = document.getPartition(tp.getSelectedRange().x);
			ITypedRegion parentRegion = document.getPartition(region.getOffset()-1);
			String parentNode = document.get(parentRegion.getOffset(), parentRegion.getLength());
			if(!XMLPartitionScanner.XML_CDATA.equals(region.getType())||!"<script>".equalsIgnoreCase(parentNode)){
				DialogUtil.showErrorMessageBox("此区域非javascript代码");
				return;
			}
			int begin = region.getOffset()+"<![CDATA[".length();
			int length = region.getLength()-"<![CDATA[".length()-"]]>".length();
			String jsCode = document.get(begin,length);
			if(jsCode == null || "".equals(jsCode))
				return;
			JSBeautifier bf = new JSBeautifier();
			String indent = XMLOutputter.DEFAULT_INDENT+XMLOutputter.DEFAULT_INDENT+XMLOutputter.DEFAULT_INDENT;
			jsCode = ("\n"+bf.beautify(jsCode, bf.opts)).replaceAll("\n", "\n"+indent);
			document.replace(begin, length, jsCode);
		} catch (Throwable e) {
			DialogUtil.showExceptionMessageBox(e);
			return ;
		}
		try {
			int offset = document.getLineOffset(cursorLine);
			int length = document.getLineLength(cursorLine);
			if(offset==0||length==0)
				return;
			tp.setHighlightRange(offset, length, true);
		} catch (BadLocationException e) {
			DialogUtil.showExceptionMessageBox(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
