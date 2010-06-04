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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.IContainer;
import uncertain.schema.Element;

public class ToolBarAddElementListener implements Listener {
	private ToolBar toolBar;
	private Menu menu;
	private ToolItem item;

	private IContainer container;

	public ToolBarAddElementListener(ToolBar toolBar, Menu menu, ToolItem item,
			 IContainer container) {
		this.toolBar = toolBar;
		this.menu = menu;
		this.item = item;
		this.container = container;

	}

	public void handleEvent(Event event) {
		if (event.detail == SWT.ARROW) {
			// 获得当前选中的节点
			ISelection selection = ((ColumnViewer)container.getViewer()).getSelection();
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			final CompositeMap selectedCM = (CompositeMap) obj;
			if(selectedCM == null)return;

			// 清空原先的子菜单
			MenuItem[] mi = menu.getItems();
			for (int i = 0; i < mi.length; i++) {
				mi[i].dispose();
			}

			Element element = Common.getSchemaManager().getElement(
					selectedCM);

			if (element == null) {
				Common.showWarningMessageBox(null, Common.getString("undefined.self.element"));
				return;
			}

			List childElements = 	CompositeMapAction.getAvailableChildElements(element,selectedCM);
			if (childElements != null) {
				Iterator ite = childElements.iterator();
				while (ite.hasNext()) {
					Object object = ite.next();
					if(! (object instanceof Element))
						continue;
					Element ele = (Element) object;
					final QualifiedName qName = ele.getQName();
					MenuItem itemPush = new MenuItem(menu, SWT.PUSH);
					itemPush.addListener(SWT.Selection, new AddElementListener(container, selectedCM, qName));
					String text = Common.getElementFullName(selectedCM, qName);
					itemPush.setText(text);
					itemPush.setImage(getIcon());
				}
			}

			Rectangle rect = item.getBounds();
			Point pt = new Point(rect.x, rect.y + rect.height);
			pt = toolBar.toDisplay(pt);
			menu.setLocation(pt.x, pt.y);
			menu.setVisible(true);
		}
	}
	private Image getIcon(){
		return Activator.getImageDescriptor(Common.getString("element.icon")).createImage();
	}
}
