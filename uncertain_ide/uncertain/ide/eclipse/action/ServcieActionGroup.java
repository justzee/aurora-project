package uncertain.ide.eclipse.action;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;

import uncertain.composite.CompositeMap;
import uncertain.schema.Array;
import uncertain.schema.Element;
import uncertain.schema.SchemaConstant;
import uncertain.schema.editor.AttributeValue;
import aurora_ide.Activator;

public class ServcieActionGroup extends ActionGroup {

	private CompositeMap selectedCm;

	private IViewerDirty mDirtyObject;


	public ServcieActionGroup(IViewerDirty mDirtyObject) {
		this.mDirtyObject = mDirtyObject;
	}










	/**
	 * 生成菜单Menu，并将两个Action传入
	 */
	public void fillContextMenu() {
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
				manager.add(new RefreshAction(mDirtyObject));
				manager.add(new CopyAction(mDirtyObject));
				manager.add(new PasteAction(mDirtyObject));
				manager.add(new RemoveElementAction(mDirtyObject));
			}
		});

		Menu menu = menuManager.createContextMenu(mDirtyObject.getControl());
		// Menu menu = menuManager.createMenuBar(mColumnViewer.)
		mDirtyObject.getControl().setMenu(menu);

	}

	public void fillDNDListener() {

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
					// return;
				}
				CompositeMap childCm = new CompositeMap(sourceCm);
				sourceCm.getParent().removeChild(sourceCm);

				objectCm.addChild(childCm);
				setDirty(true);
				mDirtyObject.refresh();
			}

			public void dragEnter(DropTargetEvent event) {
				// System.out.println(event.getSource());

			}
		});
	}

	public void fillKeyListener() {
		if (mDirtyObject instanceof TreeViewer) {
			TreeViewer treeViewer = (TreeViewer) mDirtyObject;
			treeViewer.getTree().addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (e.stateMask == SWT.CTRL && e.keyCode == 'c') {
//						ISelection selection = mDirtyObject.getObject().getSelection();
//						Object obj = ((IStructuredSelection) selection)
//								.getFirstElement();
//						CompositeMap cm = new CompositeMap((CompositeMap) obj);
						CompositeMap cm = mDirtyObject.getFocusData();
						selectedCm = cm;
					}
					if (e.stateMask == SWT.CTRL && e.keyCode == 'v') {
						if (selectedCm == null)
							return;
//						ISelection selection = mDirtyObject.getObject().getSelection();
//						Object obj = ((IStructuredSelection) selection)
//								.getFirstElement();
//						CompositeMap parentComp = (CompositeMap) obj;
						CompositeMap parentComp = mDirtyObject.getFocusData();
						parentComp.addChild(selectedCm);
						setDirty(true);
						selectedCm = null;
						mDirtyObject.refresh();

					}

				}

				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub

				}
			});
		}

	}



	/**
	 * 得到一个图像的方法
	 */
	public ImageDescriptor getImageDesc(String fileName) {
		try {
			String filePath = "icons/" + fileName;
			return Activator.getImageDescriptor(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * 将Action包装成ActionContributionItem类的方法。实际上Action加入到
	 * ToolBarManager或MenuManager里时，也做了ActionContributionItem的包装，
	 * 大家可以看它ToolBarManager的add(IAction)的源代码即知。
	 */
	ActionContributionItem createActionContributionItem(IAction action) {
		ActionContributionItem aci = new ActionContributionItem(action);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);// 显示图像+文字
		return aci;
	}

	private void setDirty(boolean dirty) {
		mDirtyObject.setDirty(dirty);
	}
}
