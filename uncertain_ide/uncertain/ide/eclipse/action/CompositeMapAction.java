/**
 * 
 */
package uncertain.ide.eclipse.action;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import aurora_ide.Activator;
import uncertain.composite.CompositeMap;
import uncertain.schema.Array;
import uncertain.schema.Element;
import uncertain.schema.SchemaConstant;

public class CompositeMapAction {

	/**
	 * @param args
	 */
	public static void addElement(CompositeMap parentCM, String _prefix,
			String _uri, String _name) {

		CompositeMap newCM = new CompositeMap(_prefix, _uri, _name);
		parentCM.addChild(newCM);
		addElementArray(newCM);
	}

	private static void addElementArray(CompositeMap parentCM) {
		Element element = Activator.getSchemaManager().getElement(parentCM);
		if (element != null) {
			List arrays = element.getAllArrays();
			if (arrays != null) {
				Iterator ite = arrays.iterator();
				while (ite.hasNext()) {
					Array uncetainArray = (Array) ite.next();
					String name = uncetainArray.getLocalName();
					CompositeMap newCM = new CompositeMap(parentCM.getPrefix(),
							parentCM.getNamespaceURI(), name);
					parentCM.addChild(newCM);
				}
			}
		}
	}
	public static void removeElement(IViewerDirty mDirtyObject) {
		Shell shell = new Shell();
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap comp = (CompositeMap) obj;
		CompositeMap comp = mDirtyObject.getFocusData();
		if (comp != null) {
			Element em = Activator.getSchemaManager().getElement(comp);
			if (em != null && em.isArray()) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
						| SWT.OK);
				messageBox.setText("Warning");
				messageBox.setMessage("不能删除数组元素");
				messageBox.open();
				return;
			}
		}

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK
				| SWT.CANCEL);
		messageBox.setText("Warning");
		messageBox.setMessage("确认删除此节点吗?");
		int buttonID = messageBox.open();
		switch (buttonID) {
		case SWT.OK:
			if (comp != null) {
				comp.getParent().removeChild(comp);
			}
			mDirtyObject.setDirty(true);
			mDirtyObject.refresh();
		case SWT.CANCEL:
			break;
		}
	}
	public static void cutElement(IViewerDirty mDirtyObject) {
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap cm = (CompositeMap) obj;
		CompositeMap cm = mDirtyObject.getFocusData();
//		selectedCm = cm;
//		selectedCm.setNameSpaceURI(SchemaConstant.SCHEMA_NAMESPACE);
		mDirtyObject.setSelectedData(cm);
	}

	public static void copyElement(IViewerDirty mDirtyObject) {
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap cm = new CompositeMap((CompositeMap) obj);
		CompositeMap cm = mDirtyObject.getFocusData();
//		selectedCm = cm;
//		selectedCm.setNameSpaceURI(SchemaConstant.SCHEMA_NAMESPACE);
		mDirtyObject.setSelectedData(cm);

	}

	public static void pasteElement(IViewerDirty mDirtyObject) {
		CompositeMap selectedCm  = mDirtyObject.getSelectedData();
		if (selectedCm == null)
			return;
	
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap parentComp = (CompositeMap) obj;
		CompositeMap parentComp = mDirtyObject.getFocusData();
//		CompositeMap child = new CompositeMap(selectedCm);
		CompositeMap child = new CompositeMap(selectedCm);

		if (child != null) {
			parentComp.addChild(child);
			Element em = Activator.getSchemaManager().getElement(child);
			if (em == null) {
				parentComp.removeChild(child);
				Shell shell = new Shell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
						| SWT.OK);
				messageBox.setText("Warning");
				messageBox.setMessage("此节点不能包含此子节点.");
				messageBox.open();
				return;
			} else if (selectedCm.getParent() != null)
				selectedCm.getParent().removeChild(selectedCm);
		}
		selectedCm = null;
//		makeDirty();
		mDirtyObject.setDirty(true);
		mDirtyObject.refresh();

	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
