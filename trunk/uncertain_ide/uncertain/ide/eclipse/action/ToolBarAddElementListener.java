package uncertain.ide.eclipse.action;

import java.util.Iterator;
import java.util.List;

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
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.CompositeMapViewer;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.schema.Element;

public class ToolBarAddElementListener implements Listener {
	private ToolBar toolBar;
	private Menu menu;
	private ToolItem item;
	private CompositeMapViewer viewer;
	public ToolBarAddElementListener(ToolBar toolBar, Menu menu, ToolItem item,
			CompositeMapViewer container) {
		this.toolBar = toolBar;
		this.menu = menu;
		this.item = item;
		this.viewer = container;

	}

	public void handleEvent(Event event) {
		if (event.detail == SWT.ARROW) {
			CompositeMap selectedCM = viewer.getFocus();
			if(selectedCM == null)return;
			MenuItem[] mi = menu.getItems();
			for (int i = 0; i < mi.length; i++) {
				mi[i].dispose();
			}

			Element element = LoadSchemaManager.getSchemaManager().getElement(
					selectedCM);

			if (element == null) {
				CustomDialog.showWarningMessageBox(null, LocaleMessage.getString("undefined.self.element"));
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
					itemPush.addListener(SWT.Selection, new AddElementListener(viewer, selectedCM, qName));
					String text = CompositeMapAction.getElementFullName(selectedCM, qName);
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
		return Activator.getImageDescriptor(LocaleMessage.getString("element.icon")).createImage();
	}
}
