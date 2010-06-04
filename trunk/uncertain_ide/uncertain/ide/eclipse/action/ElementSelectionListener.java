package uncertain.ide.eclipse.action;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Label;

import uncertain.composite.CompositeMap;
import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.IContainer;
import uncertain.ide.eclipse.editor.textpage.JavaScriptLineStyler;
import uncertain.ide.eclipse.editor.widgets.PropertyGridViewer;
import uncertain.ide.eclipse.editor.widgets.PropertyHashViewer;
import uncertain.schema.Element;

public class ElementSelectionListener implements ISelectionChangedListener {

	CTabFolder mTabFolder;
	PropertyHashViewer mPropertyEditor;
	PropertyGridViewer mPropertyArrayEditor;
	IContainer container;
	StyledText mInnerText;
	JavaScriptLineStyler mLineStyler;
	Label elementDocument;
	private boolean validError = false;
	
	private boolean postChangeDiabled = false;
	
	public ElementSelectionListener(CTabFolder tabFolder,
			PropertyHashViewer propertyEditor,
			PropertyGridViewer propertyArrayEditor,
			IContainer container, StyledText innerText,
			JavaScriptLineStyler lineStyler,
			Label elementDocument) {
		super();
		mTabFolder = tabFolder;
		mPropertyEditor = propertyEditor;
		mPropertyArrayEditor = propertyArrayEditor;
		this.container = container;
		mInnerText = innerText;
		mLineStyler =lineStyler;
		this.elementDocument = elementDocument;
	}

	public boolean postChangeDiabled(){
		return postChangeDiabled;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		if(validError){
			validError = false;
			return;
		}
		TreeSelection selection = (TreeSelection) event.getSelection();
		CompositeMap data = (CompositeMap) selection.getFirstElement();
		try {
			mPropertyEditor.clearAll();
			mPropertyArrayEditor.clearAll();
		} catch (Exception e) {
			validError = true;
			postChangeDiabled = true;
			((TreeViewer)container.getViewer()).setSelection(new StructuredSelection(container.getFocus()));
//			throw new RuntimeException(e.getLocalizedMessage(),e);
			return;
		}
		container.setFocus(data);
		if (data == null)
			return;
		Element em = Common.getSchemaManager().getElement(data);
		elementDocument.setText("");
		if(em != null ){
			String document = em.getDocument();
			if(document !=null)
				elementDocument.setText(document);
		}
		if (em != null && em.isArray()) {
//			mPropertyEditor.clearAll();
			mPropertyArrayEditor.createEditor(mTabFolder,data);
			mTabFolder.getItem(1).setControl(
					mPropertyArrayEditor.getControl());
			mTabFolder.setSelection(1);
			mTabFolder.layout(true);

		} else {
//			mPropertyEditor.clearAll();
//			mPropertyArrayEditor.clearAll();
		    mPropertyEditor.setData(data);
			mTabFolder.setSelection(0);
			mTabFolder.layout(true);

		}
		String a = data.getText();
		if (a != null && !a.trim().equals("")) {
			mInnerText.setText(data.getText());
			mLineStyler.parseBlockComments(data.getText());
			mTabFolder.setSelection(2);
			mTabFolder.layout(true);
		} else {
			mInnerText.setText("");
		}
		postChangeDiabled = false;
	}

}
