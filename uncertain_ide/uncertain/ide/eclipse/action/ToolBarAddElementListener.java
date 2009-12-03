package uncertain.ide.eclipse.action;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.schema.Element;

public class ToolBarAddElementListener implements Listener {
	private ToolBar toolBar;
	private Menu menu;
	private ToolItem item;
//	private ColumnViewer mColumnViewer;
	private IViewerDirty mDirtyObject;

	public ToolBarAddElementListener(ToolBar toolBar, Menu menu, ToolItem item,
			 IViewerDirty mDirtyObject) {
		this.toolBar = toolBar;
		this.menu = menu;
		this.item = item;
//		this.mColumnViewer = mColumnViewer;
		this.mDirtyObject = mDirtyObject;

	}

	public void handleEvent(Event event) {
		if (event.detail == SWT.ARROW) {
			// 获得当前选中的节点
			ISelection selection = mDirtyObject.getObject().getSelection();
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			final CompositeMap selectedCM = (CompositeMap) obj;
			if(selectedCM == null)return;

			// 清空原先的子菜单
			MenuItem[] mi = menu.getItems();
			for (int i = 0; i < mi.length; i++) {
				mi[i].dispose();
			}

			Element element = Activator.getSchemaManager().getElement(
					selectedCM);

			if (element == null) {
				Shell shell = new Shell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
						| SWT.OK);
				messageBox.setText("信息");
				messageBox.setMessage("此元素本身没有定义。");
				messageBox.open();
				return;
			}
			List sonElements = element.getAllElements();
			if (sonElements != null) {
				Iterator ite = sonElements.iterator();
				while (ite.hasNext()) {
					Element ele = (Element) ite.next();
					final QualifiedName qName = ele.getQName();
					//判读最多出现次数
//					selectedCM.getChild(name)
//					if(ele.getMaxOccurs())
					
					MenuItem itemPush = new MenuItem(menu, SWT.PUSH);
					itemPush.addListener(SWT.Selection, new AddElementListener(mDirtyObject, selectedCM, qName));
					itemPush.setText(ele.getLocalName());
					itemPush.setImage(getIcon());
				}
			}

			if (element.isArray()) {
				final QualifiedName qName = element.getElementType().getQName();
				final String elementType = element.getElementType().getQName()
						.getLocalName();
				MenuItem itemPush = new MenuItem(menu, SWT.PUSH);
//				itemPush.addListener(SWT.Selection, new AddElementListener(
//						mColumnViewer, mDirtyObject, selectedCM, null, null,
//						elementType));
				itemPush.addListener(SWT.Selection, new AddElementListener(mDirtyObject, selectedCM, qName));
				itemPush.setText(elementType);
				itemPush.setImage(getIcon());
			}

			Rectangle rect = item.getBounds();
			Point pt = new Point(rect.x, rect.y + rect.height);
			pt = toolBar.toDisplay(pt);
			menu.setLocation(pt.x, pt.y);
			menu.setVisible(true);
		}
	}
	private Image getIcon(){
		Image icon = Activator.getImageDescriptor("icons/element_obj.gif").createImage();
		return icon;
	}
}
