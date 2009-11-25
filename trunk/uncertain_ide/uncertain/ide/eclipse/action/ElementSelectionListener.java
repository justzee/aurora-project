package uncertain.ide.eclipse.action;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.AuroraPropertyArrayEditor;
import uncertain.ide.eclipse.editor.AuroraPropertyEditor;
import uncertain.schema.Element;

public class ElementSelectionListener implements ISelectionChangedListener {

	TabFolder mTabFolder;
	AuroraPropertyEditor mPropertyEditor;
	AuroraPropertyArrayEditor mPropertyArrayEditor;
	IViewerDirty mColumnViewerDirtyAction;
	Text mInnerText;
	
	
	
	public ElementSelectionListener(TabFolder tabFolder,
			AuroraPropertyEditor propertyEditor,
			AuroraPropertyArrayEditor propertyArrayEditor,
			IViewerDirty columnViewerDirtyAction, Text innerText) {
		super();
		mTabFolder = tabFolder;
		mPropertyEditor = propertyEditor;
		mPropertyArrayEditor = propertyArrayEditor;
		mColumnViewerDirtyAction = columnViewerDirtyAction;
		mInnerText = innerText;
	}



	public void selectionChanged(SelectionChangedEvent event) {
		TreeSelection selection = (TreeSelection) event.getSelection();
		CompositeMap data = (CompositeMap) selection.getFirstElement();
		mColumnViewerDirtyAction.setFocusData(data);

		if (data == null)
			return;
		Element em = Activator.getSchemaManager().getElement(data);
		if (em != null && em.isArray()) {
			mPropertyEditor.clearAll();
			mPropertyArrayEditor.createEditor(mTabFolder, data);
			mTabFolder.getItem(1).setControl(
					mPropertyArrayEditor.getTableViewer().getControl());
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
			// System.out.println(data.getText());
			mTabFolder.setSelection(2);
			mTabFolder.layout(true);
		} else {
			mInnerText.setText("");
		}
	}

}
