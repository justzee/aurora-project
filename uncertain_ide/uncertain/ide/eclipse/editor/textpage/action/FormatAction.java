package uncertain.ide.eclipse.editor.textpage.action;

import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import uncertain.ide.eclipse.editor.textpage.TextPage;
import uncertain.ide.help.AuroraResourceUtil;
import uncertain.ide.help.CustomDialog;

public class FormatAction implements IEditorActionDelegate {

	IEditorPart activeEditor ;
	public FormatAction() {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
	}

	public void run(IAction action) {
		if(activeEditor == null||!(activeEditor instanceof TextPage)){
			CustomDialog.showErrorMessageBox("这个类不是"+TextPage.class.getName());
			return;
		}
		TextPage tp = (TextPage)activeEditor;
		IDocument document = tp.getInputDocument();
		String content = document.get();
		if(content == null){
			return;
		}
		CompositeLoader cl = CompositeLoader.createInstanceForOCM();
		cl.setSaveNamespaceMapping(true);
		try {
			CompositeMap data = cl.loadFromString(content);
			String formatContent = AuroraResourceUtil.xml_decl + XMLOutputter.defaultInstance().toXML(data, true);
			tp.refresh(formatContent);
		} catch (IOException e) {
			CustomDialog.showErrorMessageBox("解析"+content+"错误！");
		} catch (SAXException e) {
			CustomDialog.showErrorMessageBox("解析"+content+"错误！");
		}
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
