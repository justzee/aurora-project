package uncertain.ide.eclipse.editor.sxsd;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewer;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.forms.editor.FormPage;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.action.IViewerDirty;
import uncertain.schema.Array;
import uncertain.schema.Element;
import uncertain.schema.editor.AttributeValue;
import aurora_ide.Activator;

public class SxsdActionGroup extends ActionGroup {
	private ColumnViewer mColumnViewer;
	private FormPage mformPage;
	private CompositeMap selectedComp;
	Menu menuBar;
	private IViewerDirty mDirtyObject;
	public SxsdActionGroup() {

	}

	public SxsdActionGroup(ColumnViewer columnViewer) {
		this.mColumnViewer = columnViewer;
	}

	public SxsdActionGroup(ColumnViewer columnViewer, FormPage formPage) {
		this.mColumnViewer = columnViewer;
		mformPage = formPage;
	}

	class ToolBarAddElementListener implements Listener {
		ToolBar toolBar;
		Menu menu;
		ToolItem item;

		public ToolBarAddElementListener(ToolBar toolBar, Menu menu,
				ToolItem item) {
			this.toolBar = toolBar;
			this.menu = menu;
			this.item = item;

		}

		public void handleEvent(Event event) {
			final boolean debug = false;
			Image icon = Activator.getImageDescriptor(
					"icons/element_obj.gif").createImage();
			if (event.detail == SWT.ARROW) {

				ISelection selection = mColumnViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				final CompositeMap comp = (CompositeMap) obj;

				MenuItem[] mi = menu.getItems();
				for (int i = 0; i < mi.length; i++) {
					mi[i].dispose();
				}
				if (debug)
					System.out.println("map..." + comp.toXML());
				String raw_name = comp.getRawName();
				if (debug)
					System.out.println("raw_name:" + raw_name);

				Element element = Activator.getSchemaManager().getElement(comp);
				if (element == null) {
					Shell shell = new Shell();
					MessageBox messageBox = new MessageBox(shell,
							SWT.ICON_WARNING | SWT.OK);
					messageBox.setText("信息");
					messageBox.setMessage("此元素没有子节点。");
					messageBox.open();
					return;
				}
				if (element.isArray()) {
					final String elementType = element.getElementType()
							.getQName().getLocalName();
					MenuItem itemPush = new MenuItem(menu, SWT.PUSH);
					itemPush.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							addElement(comp, elementType);

						}
					});
					itemPush.setText(elementType);
					itemPush.setImage(icon);
				} else if (element != null) {
					List arrays = element.getAllElements();
					if (debug)
						System.out.println("arrays.size():" + arrays.size());
					if (arrays != null) {
						if (debug)
							System.out.println("arrays..");
						Iterator ite = arrays.iterator();
						while (ite.hasNext()) {
							if (debug)
								System.out.println("hasNext..");
							final Element ele = (Element) ite.next();
							MenuItem itemPush = new MenuItem(menu, SWT.PUSH);
							itemPush.addListener(SWT.Selection, new Listener() {
								public void handleEvent(Event event) {
									addElement(comp, ele.getLocalName());

								}
							});
							itemPush.setText(ele.getLocalName());
							itemPush.setImage(icon);
						}
					}
				}
				Rectangle rect = item.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = toolBar.toDisplay(pt);
				menu.setLocation(pt.x, pt.y);
				menu.setVisible(true);
			}
		}

	}
	class ContextAddElementMenuManager extends MenuManager{
		public ContextAddElementMenuManager(String label){
			super(label);
			createActions();
		}
		private void createActions(){
			ISelection selection = mColumnViewer.getSelection();
			Object obj = ((IStructuredSelection) selection)
					.getFirstElement();
			final CompositeMap comp = (CompositeMap) obj;
			
			Element element = Activator.getSchemaManager().getElement(comp);
			if (element == null) {
				return;
			}
			
			if (element.isArray()) {
				final String elementType = element.getElementType()
						.getQName().getLocalName();
				
				this.add(new AddElementAction(comp,elementType));

			} else if (element != null) {
				List arrays = element.getAllElements();
				if (arrays != null) {
					Iterator ite = arrays.iterator();
					while (ite.hasNext()) {
						final Element ele = (Element) ite.next();
						this.add(new AddElementAction(comp, ele.getLocalName()));
					}
				}
			}
		}
	}
	
	private void addElement(CompositeMap parent, String elementName) {
		boolean debug = true;
		CompositeMap newComp = new CompositeMap(SxsdPage.namespacePrefix,
				parent.getNamespaceURI(), elementName);
		parent.addChild(newComp);
		createSonArray(newComp);
		if (debug)
			System.out.println("now comp:" + parent.toXML());
		makeDirty();
		mColumnViewer.refresh();
	}

	private void removeElement() {
		Shell shell = new Shell();
		ISelection selection = mColumnViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		CompositeMap comp = (CompositeMap) obj;
		if (comp != null) {
			Element em = Activator.getSchemaManager().getElement(comp);
			if (em.isArray()) {
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
			makeDirty();
			mColumnViewer.refresh();
		case SWT.CANCEL:
			break;
		}
	}

	private void cutElement() {
		ISelection selection = mColumnViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		CompositeMap cm = (CompositeMap) obj;
		selectedComp = cm;
		selectedComp.setNameSpaceURI(SxsdPage.namespaceUrl);
	}

	private void copyElement() {
		ISelection selection = mColumnViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		CompositeMap cm = new CompositeMap((CompositeMap) obj);
		selectedComp = cm;
		selectedComp.setNameSpaceURI(SxsdPage.namespaceUrl);

	}

	private void pasteElement() {
		if (selectedComp == null)
			return;
		ISelection selection = mColumnViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		CompositeMap parentComp = (CompositeMap) obj;
		CompositeMap child = new CompositeMap(selectedComp);

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
			} else if (selectedComp.getParent() != null)
				selectedComp.getParent().removeChild(selectedComp);
		}
		selectedComp = null;
		makeDirty();
		mColumnViewer.refresh();

	}

	public void fillElementToolBar(Composite shell) {

		ToolBar toolBar = new ToolBar(shell, SWT.RIGHT | SWT.FLAT);
		// 创建一个toolBar的管理器
		Menu menu = new Menu(shell);

		ToolItem addItem = new ToolItem(toolBar, SWT.DROP_DOWN);
		setToolItemShowProperty(addItem, "添加子节点", "icons/add_obj.gif");
		addItem.addListener(SWT.Selection, new ToolBarAddElementListener(
				toolBar, menu, addItem));

		final ToolItem removeItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(removeItem, "删除", "icons/delete_obj.gif");
		removeItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				removeElement();

			}
		});

		final ToolItem cutItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(cutItem, "剪切", "icons/cut.gif");
		cutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cutElement();
			}
		});

		final ToolItem copyItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(copyItem, "复制", "icons/copy.gif");
		copyItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				copyElement();
			}
		});

		final ToolItem pasteItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(pasteItem, "粘贴", "icons/paste.gif");
		pasteItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				pasteElement();
			}
		});

		toolBar.pack();
		((ViewForm) shell).setTopLeft(toolBar); // 顶端边缘：工具栏
	}

	private void setToolItemShowProperty(ToolItem toolItem, String text,
			String iconPath) {
		if (text != null && !text.equals(""))
			toolItem.setText(text);
		if (iconPath != null && !iconPath.equals("")) {
			Image icon = Activator.getImageDescriptor(iconPath).createImage();
			toolItem.setImage(icon);
		}

	}

	/**
	 * 生成菜单Menu，并将两个Action传入
	 */
	public void fillContextMenu(IMenuManager mgr) {
		/*
		 * 加入两个Action对象到菜单管理器
		 */
		MenuManager menuManager = (MenuManager) mgr; // 类型转换一下，注意参数是接口
		mgr.setRemoveAllWhenShown(true);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(new Separator(
						IWorkbenchActionConstants.MB_ADDITIONS));
				MenuManager cascadingMenu = new ContextAddElementMenuManager("添加子节点");
				manager.add(cascadingMenu);
				manager.add(new RefreshAction());
				manager.add(new CopyAction());
				manager.add(new PasteAction());
				manager.add(new RemoveElementAction());
			}
		});

		Menu menu = menuManager.createContextMenu(mColumnViewer.getControl());
		// Menu menu = menuManager.createMenuBar(mColumnViewer.)
		mColumnViewer.getControl().setMenu(menu);

	}

	public static void createSonArray(CompositeMap parent) {
		final boolean debug = false;
		Element ele = Activator.getSchemaManager().getElement(parent);
		if (ele != null) {
			if (debug)
				System.out.println("ele   ！");
			List arrays = ele.getAllArrays();
			if (arrays != null) {
				Iterator ite = arrays.iterator();
				while (ite.hasNext()) {
					// if (debug)
					// System.out.println(map.toXML());
					Array uncetainArray = (Array) ite.next();
					String name = uncetainArray.getLocalName();
					CompositeMap newMap = new CompositeMap(parent.getPrefix(),
							parent.getNamespaceURI(), name);
					parent.addChild(newMap);
					if (debug)
						System.out.println("有子节点了：" + parent.toXML());
				}
			}
		}
	}

	public void fillDNDListener() {

		DragSource ds = new DragSource(mColumnViewer.getControl(),
				DND.DROP_MOVE);
		ds.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
		ds.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
				ISelection selection = mColumnViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				CompositeMap cm = new CompositeMap((CompositeMap) obj);
				// System.out.println("拽："+cm.toXML());
				// event.data = cm.toXML();
				// event.data = cm;
			}
		});

		DropTarget dt = new DropTarget(mColumnViewer.getControl(),
				DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
		dt.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				// System.out.println("放在这里");
				ISelection selection = mColumnViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				CompositeMap sourceCm = (CompositeMap) obj;
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
				makeDirty();
				mColumnViewer.refresh();
			}

			public void dragEnter(DropTargetEvent event) {
				// System.out.println(event.getSource());

			}
		});
	}

	public void fillKeyListener() {
		if (mColumnViewer instanceof TreeViewer) {
			TreeViewer treeViewer = (TreeViewer) mColumnViewer;
			treeViewer.getTree().addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (e.stateMask == SWT.CTRL && e.keyCode == 'c') {
						ISelection selection = mColumnViewer.getSelection();
						Object obj = ((IStructuredSelection) selection)
								.getFirstElement();
						CompositeMap cm = new CompositeMap((CompositeMap) obj);
						selectedComp = cm;
					}
					if (e.stateMask == SWT.CTRL && e.keyCode == 'v') {
						if (selectedComp == null)
							return;
						ISelection selection = mColumnViewer.getSelection();
						Object obj = ((IStructuredSelection) selection)
								.getFirstElement();
						CompositeMap parentComp = (CompositeMap) obj;
						parentComp.addChild(selectedComp);
						makeDirty();
						selectedComp = null;
						mColumnViewer.refresh();

					}

				}

				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub

				}
			});
		}

	}

	/**
	 * 刷新的Action类
	 */
	private class RefreshAction extends Action {
		public RefreshAction() {
			// 给Action设置图像。getImageDesc为自定义方法，得到一个图像
			// setHoverImageDescriptor(getImageDesc("refresh.gif"));
			setImageDescriptor(getImageDesc("refresh.gif"));
			setText("刷新");
		}

		public void run() {
			mColumnViewer.refresh();// 调用表格的刷新方法
		}
	}

	private class RemoveElementAction extends Action {
		public RemoveElementAction() {
			// 正常情况下的图标
			setHoverImageDescriptor(getImageDesc("delete_obj.gif"));
			// 置灰（removeAction.setEnabled(false)）情况下的图标
			// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
			setText("删除子节点");
		}

		/**
		 * 这里演示了如何从表格中删除所选的记录（可选多个）
		 */
		public void run() {
			removeElement();

		}
	}

	private class RemovePropertyAction extends Action {
		public RemovePropertyAction() {
			// 正常情况下的图标
			setHoverImageDescriptor(getImageDesc("delete_obj.gif"));
			// 置灰（removeAction.setEnabled(false)）情况下的图标
			// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
			setText("删除属性");
		}

		/**
		 * 这里演示了如何从表格中删除所选的记录（可选多个）
		 */
		public void run() {
			Shell shell = new Shell();
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
					| SWT.OK | SWT.CANCEL);
			messageBox.setText("Warning");
			messageBox.setMessage("确认删除此属性吗?");
			int buttonID = messageBox.open();
			switch (buttonID) {
			case SWT.OK:
				final CompositeMap data = (CompositeMap) mColumnViewer
						.getInput();
				System.out.println(data.toXML());
				ISelection selection = mColumnViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				AttributeValue av = (AttributeValue) obj;
				String propertyName = av.getAttribute().getLocalName();
				System.out.println(propertyName);
				data.remove(propertyName);

				makeDirty();
				mColumnViewer.refresh();
			case SWT.CANCEL:
				break;
			}

		}
	}

	private class AddElementAction extends Action {
		CompositeMap parent;
		String elementName;
		public AddElementAction(CompositeMap parent,String elementName) {
			// 正常情况下的图标
			setHoverImageDescriptor(getImageDesc("element_obj.gif"));
			// 置灰（removeAction.setEnabled(false)）情况下的图标
			// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
			setText(elementName);
			this.parent=parent;
			this.elementName=elementName;
		}

		/**
		 * 这里演示了如何从表格中删除所选的记录（可选多个）
		 */
		public void run() {
			addElement(parent, elementName);
		}
	}

	private class AddPropertyAction extends Action {
		public AddPropertyAction() {
			// 正常情况下的图标
			setHoverImageDescriptor(getImageDesc("add_obj.gif"));
			// 置灰（removeAction.setEnabled(false)）情况下的图标
			// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
			setText("添加属性");
		}

		/**
		 * 这里演示了如何从表格中删除所选的记录（可选多个）
		 */
		public void run() {
			final CompositeMap data = (CompositeMap) mColumnViewer.getInput();
			System.out.println(data.toXML());
			final Shell shell = new Shell();
			shell.setSize(400, 200);

			Label propertyLabe = new Label(shell, SWT.NONE);
			propertyLabe.setText("属性名:");
			propertyLabe.setBounds(20, 20, 50, 30);

			final Text propertyText = new Text(shell, SWT.SHADOW_IN);
			propertyText.setBounds(80, 20, 300, 20);

			Label valueLabel = new Label(shell, SWT.NONE);
			valueLabel.setText("值:");
			valueLabel.setBounds(20, 50, 50, 30);

			final Text valueText = new Text(shell, SWT.SHADOW_IN);
			valueText.setBounds(80, 50, 300, 20);

			final Button ok = new Button(shell, SWT.PUSH);
			ok.setText("OK");
			ok.setBounds(220, 120, 70, 25);

			final Button cancel = new Button(shell, SWT.PUSH);
			cancel.setText("Cancel");
			cancel.setBounds(300, 120, 70, 25);
			SelectionListener listener = new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub

				}

				public void widgetSelected(SelectionEvent e) {
					Widget w = e.widget;
					if (w == ok) {
						System.out.println("ok........");
						System.out.println(propertyText.getText() + ":"
								+ valueText.getText());
						data.put(propertyText.getText(), valueText.getText());
						System.out.println(data.toXML());
						makeDirty();
						mColumnViewer.refresh();
						shell.dispose();
					} else if (w == cancel) {
						shell.dispose();
					}

				}
			};
			ok.addSelectionListener(listener);
			cancel.addSelectionListener(listener);
			shell.open();

			mColumnViewer.refresh();
		}
	}

	private class CopyAction extends Action {
		public CopyAction() {
			setHoverImageDescriptor(getImageDesc("copy.gif"));
			setText("复制");
		}

		public void run() {
			ISelection selection = mColumnViewer.getSelection();
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			CompositeMap cm = new CompositeMap((CompositeMap) obj);
			selectedComp = cm;
		}
	}

	private class PasteAction extends Action {
		public PasteAction() {
			this.setHoverImageDescriptor(getImageDesc("paste.gif"));
			// setAccelerator(SWT.CTRL+'V');
			setText("粘贴");
		}

		public void run() {
			if (selectedComp == null)
				return;
			ISelection selection = mColumnViewer.getSelection();
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			CompositeMap parentComp = (CompositeMap) obj;
			CompositeMap child = selectedComp;
			selectedComp = null;
			if (child != null)
				parentComp.addChild(child);
			makeDirty();
			mColumnViewer.refresh();
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

	private void makeDirty() {
		if (mformPage != null)
			((SxsdPage) mformPage).makeDirty();
	}
}
