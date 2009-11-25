/**
 * 
 */
package uncertain.ide.eclipse.action;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchActionConstants;

import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.ActionLabelManager;
import uncertain.schema.Array;
import uncertain.schema.Element;

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

	public static void addElementArray(CompositeMap parentCM) {
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
	public static void removeElement(IViewerDirty viewer) {
		Shell shell = new Shell();
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap comp = (CompositeMap) obj;
		CompositeMap comp = viewer.getFocusData();
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
			viewer.refresh(true);
		case SWT.CANCEL:
			break;
		}
	}
	public static void cutElement(IViewerDirty viewer) {
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap cm = (CompositeMap) obj;
		CompositeMap cm = viewer.getFocusData();
//		selectedCm = cm;
//		selectedCm.setNameSpaceURI(SchemaConstant.SCHEMA_NAMESPACE);
		viewer.setSelectedData(cm);
	}

	public static void copyElement(IViewerDirty viewer) {
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap cm = new CompositeMap((CompositeMap) obj);
		CompositeMap cm = viewer.getFocusData();
//		selectedCm = cm;
//		selectedCm.setNameSpaceURI(SchemaConstant.SCHEMA_NAMESPACE);
		viewer.setSelectedData(cm);

	}

	public static void pasteElement(IViewerDirty viewer) {
		CompositeMap selectedCm  = viewer.getSelectedData();
		if (selectedCm == null)
			return;
	
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap parentComp = (CompositeMap) obj;
		CompositeMap parentComp = viewer.getFocusData();
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
		viewer.refresh(true);

	}
	/**
	 * 生成菜单Menu，并将两个Action传入
	 */
	public static void fillContextMenu(final IViewerDirty mDirtyObject) {
		/*
		 * 加入两个Action对象到菜单管理器
		 */
		MenuManager mgr = new MenuManager("#PopupMenu");
		MenuManager menuManager = (MenuManager) mgr; // 类型转换一下，注意参数是接口
		mgr.setRemoveAllWhenShown(true);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(new Separator(
						IWorkbenchActionConstants.MB_ADDITIONS));
				MenuManager cascadingMenu = new ContextAddElementMenuManager(mDirtyObject,null,null,
						"添加子节点");
				manager.add(cascadingMenu);
				manager.add(new CopyAction(mDirtyObject,CopyAction.getDefaultImageDescriptor(),CopyAction.getDefaultText()));
				manager.add(new PasteAction(mDirtyObject,PasteAction.getDefaultImageDescriptor(),PasteAction.getDefaultText()));
				manager.add(new RemoveElementAction(mDirtyObject,RemoveElementAction.getDefaultImageDescriptor(),RemoveElementAction.getDefaultText()));
				manager.add(new RefreshAction(mDirtyObject,RefreshAction.getDefaultImageDescriptor(),ActionLabelManager.getText(ActionLabelManager.REFRESH)));
			}
		});

		Menu menu = menuManager.createContextMenu(mDirtyObject.getControl());
		// Menu menu = menuManager.createMenuBar(mColumnViewer.)
		mDirtyObject.getControl().setMenu(menu);

	}

	public static void fillDNDListener(final IViewerDirty mDirtyObject) {

		DragSource ds = new DragSource(mDirtyObject.getControl(),
				DND.DROP_MOVE);
		ds.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
		ds.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
//				ISelection selection = mDirtyObject.getObject().getSelection();
//				Object obj = ((IStructuredSelection) selection)
//						.getFirstElement();
//				CompositeMap cm = new CompositeMap((CompositeMap) obj);
				// System.out.println("拽："+cm.toXML());
				// event.data = cm.toXML();
				// event.data = cm;
			}
		});

		DropTarget dt = new DropTarget(mDirtyObject.getControl(),
				DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
		dt.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				// System.out.println("放在这里");
//				ISelection selection = mDirtyObject.getObject().getSelection();
//				Object obj = ((IStructuredSelection) selection)
//						.getFirstElement();
//				CompositeMap sourceCm = (CompositeMap) obj;
				CompositeMap sourceCm = mDirtyObject.getFocusData();
				// System.out.println("cm:"+sourceCm.toXML());
				CompositeMap objectCm = (CompositeMap) event.item.getData();
				// CompositeMap sourceCm = (CompositeMap) event.data;
				// CompositeMap sourceCm = new CompositeMap();
				// sourceCm.setText((String)event.data);

				if (objectCm.equals(sourceCm)) {
					System.out.println("一样的！");
					return;
				}
				CompositeMap childCm = new CompositeMap(sourceCm);
				
				
				if (childCm != null) {
					objectCm.addChild(childCm);
					Element em = Activator.getSchemaManager().getElement(childCm);
					if (em == null) {
						objectCm.removeChild(childCm);
						Shell shell = new Shell();
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
								| SWT.OK);
						messageBox.setText("Warning");
						messageBox.setMessage("此节点不能包含此子节点.");
						messageBox.open();
						return;
					} else if (sourceCm.getParent() != null)
						sourceCm.getParent().removeChild(sourceCm);
				}
				
//				sourceCm.getParent().removeChild(sourceCm);
//
//				objectCm.addChild(childCm);
				mDirtyObject.refresh(true);
			}

			public void dragEnter(DropTargetEvent event) {
				// System.out.println(event.getSource());

			}
		});
	}

	public static void fillKeyListener(final IViewerDirty mDirtyObject) {
			TreeViewer treeViewer = (TreeViewer) mDirtyObject.getObject();
			treeViewer.getTree().addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (e.stateMask == SWT.CTRL && e.keyCode == 'c') {
//						ISelection selection = mDirtyObject.getObject().getSelection();
//						Object obj = ((IStructuredSelection) selection)
//								.getFirstElement();
//						CompositeMap cm = new CompositeMap((CompositeMap) obj);
						CompositeMap cm = mDirtyObject.getFocusData();
						mDirtyObject.setSelectedData(cm);
//						selectedCm = cm;
					}
					if (e.stateMask == SWT.CTRL && e.keyCode == 'v') {
						if (mDirtyObject.getSelectedData() == null)
							return;
//						ISelection selection = mDirtyObject.getObject().getSelection();
//						Object obj = ((IStructuredSelection) selection)
//								.getFirstElement();
//						CompositeMap parentComp = (CompositeMap) obj;
						CompositeMap parentComp = mDirtyObject.getFocusData();
						
						if (mDirtyObject.getSelectedData() != null) {
							parentComp.addChild(mDirtyObject.getSelectedData());
							Element em = Activator.getSchemaManager().getElement(mDirtyObject.getSelectedData());
							if (em == null) {
								parentComp.removeChild(mDirtyObject.getSelectedData());
								Shell shell = new Shell();
								MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
										| SWT.OK);
								messageBox.setText("Warning");
								messageBox.setMessage("此节点不能包含此子节点.");
								messageBox.open();
								return;
							} else 
								mDirtyObject.setSelectedData(null);//selectedCm = null;
						}
						mDirtyObject.refresh(true);

					}

				}

				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub

				}
			});

	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
