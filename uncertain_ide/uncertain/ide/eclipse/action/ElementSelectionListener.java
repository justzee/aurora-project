package uncertain.ide.eclipse.action;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;

import uncertain.composite.CompositeMap;
import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.JavaScriptLineStyler;
import uncertain.ide.eclipse.editor.PropertyArrayEditor;
import uncertain.ide.eclipse.editor.PropertyEditor;
import uncertain.schema.Element;

public class ElementSelectionListener implements ISelectionChangedListener {

	CTabFolder mTabFolder;
	PropertyEditor mPropertyEditor;
	PropertyArrayEditor mPropertyArrayEditor;
	IViewerDirty mColumnViewerDirtyAction;
	StyledText mInnerText;
	JavaScriptLineStyler mLineStyler;
	
	
	public ElementSelectionListener(CTabFolder tabFolder,
			PropertyEditor propertyEditor,
			PropertyArrayEditor propertyArrayEditor,
			IViewerDirty columnViewerDirtyAction, StyledText innerText,
			JavaScriptLineStyler lineStyler) {
		super();
		mTabFolder = tabFolder;
		mPropertyEditor = propertyEditor;
		mPropertyArrayEditor = propertyArrayEditor;
		mColumnViewerDirtyAction = columnViewerDirtyAction;
		mInnerText = innerText;
		mLineStyler =lineStyler;
	}



	public void selectionChanged(SelectionChangedEvent event) {
		TreeSelection selection = (TreeSelection) event.getSelection();
		CompositeMap data = (CompositeMap) selection.getFirstElement();
		mColumnViewerDirtyAction.setFocusData(data);

		if (data == null)
			return;
		Element em = Common.getSchemaManager().getElement(data);
		if (em != null && em.isArray()) {
			mPropertyEditor.clearAll();
			mPropertyArrayEditor.createEditor(mTabFolder,data);
			mTabFolder.getItem(1).setControl(
					mPropertyArrayEditor.getControl());
			mTabFolder.setSelection(1);
			mTabFolder.layout(true);

		} else {
			mPropertyArrayEditor.clearAll();
			mPropertyEditor.createEditor(mTabFolder, data);
			mTabFolder.getItem(0).setControl(mPropertyEditor.getControl());
			mTabFolder.setSelection(0);
			mTabFolder.layout(true);

		}
		String a = data.getText();
		if (a != null && !a.trim().equals("")) {
			mInnerText.setText(data.getText());
			mLineStyler.parseBlockComments(data.getText());
			// System.out.println(data.getText());
			mTabFolder.setSelection(2);
			mTabFolder.layout(true);
		} else {
			mInnerText.setText("");
		}
	}

}
