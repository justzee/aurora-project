/**
 * 
 */
package uncertain.ide.eclipse.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import org.eclipse.ui.IWorkbenchActionConstants;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.QualifiedName;
import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.ICategoryContainer;
import uncertain.ide.eclipse.editor.IContainer;
import uncertain.schema.Array;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.IType;
import uncertain.schema.SchemaManager;
import uncertain.schema.editor.AttributeValue;

public class CompositeMapAction {

	/**
	 * @param args
	 */
	private CompositeMapAction instance;

	private CompositeMapAction() {

	}

	// TODO 准备单例化这个类
	public CompositeMapAction getInstance() {
		if (instance != null)
			return instance;
		else {
			instance = new CompositeMapAction();
			return instance;
		}

	}

	public static CompositeMap addElement(CompositeMap parent, String prefix,
			String uri, String name) {

		CompositeMap child = new CompositeMap(prefix, uri, name);
		parent.addChild(child);
		addArrayNode(parent);
		return child;
	}
	//如果是在空数组下添加节点，则添加这个空数组节点
	private static void addArrayNode(CompositeMap parent) {
		Element element = Common.getSchemaManager().getElement(parent);
		if (element != null && element.isArray()) {
			QualifiedName qName = parent.getQName();
			if (CompositeUtil.findChild(parent.getParent(), qName) == null) {
				parent.getParent().addChild(parent);
			}
		}
	}

	public static void addElementArray(CompositeMap parentCM) {
		Element element = Common.getSchemaManager().getElement(parentCM);
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

	public static void removeElement(IContainer viewer) {
		CompositeMap comp = (CompositeMap)viewer.getFocus();
		if (comp != null) {
			Element em = Common.getSchemaManager().getElement(comp);
			if (em != null && em.isArray()) {
				if (comp.getChildsNotNull().size() > 0) {
					int buttonID = Common.showConfirmDialogBox(null,
							Common.getString("clear.array.question"));
					switch (buttonID) {
					case SWT.OK:
						if (comp != null) {
							comp.getChildsNotNull().clear();
							viewer.refresh(true);
							return;
						}
						viewer.refresh(true);
					case SWT.CANCEL:
						return;
					}
				}
				Common.showWarningMessageBox(null, Common.getString("can.not.delete.array.hint"));
				return;
			}
		}
		int buttonID = Common.showConfirmDialogBox(null, Common.getString("delete.element.confirm"));
		switch (buttonID) {
		case SWT.OK:
			if (comp != null) {
				CompositeMap parentCM = comp.getParent();
				// System.out.println(parentCM.toXML());
				Element element = Common.getSchemaManager().getElement(
						parentCM);
				if (element.isArray()) {
					comp.getParent().removeChild(comp);
					if (parentCM.getChilds() == null
							|| parentCM.getChilds().size() == 0) {
						parentCM.getParent().removeChild(parentCM);
						// System.out.println(parentCM.toXML());
					}
				} else {
					comp.getParent().removeChild(comp);
				}
			}
			viewer.refresh(true);
		case SWT.CANCEL:
			break;
		}
	}

	public static void removePropertyAction(ICategoryContainer viewer) {
		int buttonID = Common.showConfirmDialogBox(null, Common.getString("delete.attribute.confirm"));
		switch (buttonID) {
		case SWT.OK:
			final CompositeMap data = viewer.getInput();
			AttributeValue av = (AttributeValue)viewer.getFocus();
			if (av == null)
				return;
			String propertyName = av.getAttribute().getLocalName();
			// System.out.println(propertyName);
			data.remove(propertyName);
			viewer.refresh(true);
		case SWT.CANCEL:
			break;
		}
	}

	public static void cutElement(IContainer viewer) {
		CompositeMap cm = (CompositeMap)viewer.getFocus();
		viewer.setSelection(cm);
	}

	public static void copyElement(IContainer viewer) {
		CompositeMap cm = (CompositeMap)viewer.getFocus();
		CompositeMap child = new CompositeMap(cm);
		child.setParent(cm.getParent());
		viewer.setSelection(child);

	}

	public static void pasteElement(IContainer viewer) {
		CompositeMap selectedCm = (CompositeMap)viewer.getSelection();
		if (selectedCm == null)
			return;
		CompositeMap parentComp = (CompositeMap)viewer.getFocus();
		if (!validNextNodeLegalWithAction(parentComp, selectedCm)) {
			return;
		}
		CompositeMap child = new CompositeMap(selectedCm);
		if (child != null) {
			parentComp.addChild(child);
			selectedCm.getParent().removeChild(selectedCm);
			addArrayNode(parentComp);
		}
		selectedCm = null;
		viewer.refresh(true);

	}

	/**
	 * 生成菜单Menu，并将两个Action传入
	 */
	public static void fillContextMenu(final IContainer container) {
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
				MenuManager childElements = addChildElements(container);
				manager.add(childElements);
				manager.add(new CopyElementAction(container,
						CopyElementAction.getDefaultImageDescriptor(),
						CopyElementAction.getDefaultText()));
				manager.add(new PasteAction(container, PasteAction
						.getDefaultImageDescriptor(), PasteAction
						.getDefaultText()));
				manager.add(new RemoveElementAction(container,
						RemoveElementAction.getDefaultImageDescriptor(),
						RemoveElementAction.getDefaultText()));
				manager.add(new RefreshAction(container, RefreshAction
						.getDefaultImageDescriptor(), Common.getString("refresh")));
			}
		});

		Menu menu = menuManager.createContextMenu(container.getControl());
		// Menu menu = menuManager.createMenuBar(mColumnViewer.)
		container.getControl().setMenu(menu);

	}
	private static MenuManager addChildElements(IContainer container){
		MenuManager childElementMenus = new MenuManager(Common.getString("add.element.label")); 
		final CompositeMap comp = (CompositeMap)container.getFocus();
		Element element = Common.getSchemaManager().getElement(comp);
		if (element == null) {
			return childElementMenus;
		}
		List childElements = CompositeMapAction.getAvailableChildElements(element, comp);
		if (childElements != null) {
			Iterator ite = childElements.iterator();
			while (ite.hasNext()) {
				final Element ele = (Element) ite.next();
				final QualifiedName qName = ele.getQName();
				String text = Common.getElementFullName(comp, qName);
				childElementMenus.add(new AddElementAction(container, comp, ele.getQName(),
						AddElementAction.getDefaultImageDescriptor(), text));

			}
		}
		return childElementMenus;
	}

	public static void fillDNDListener(final IContainer container) {

		DragSource ds = new DragSource(container.getControl(), DND.DROP_MOVE);
		ds.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
		ds.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
			}
		});

		DropTarget dt = new DropTarget(container.getControl(), DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
		dt.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				CompositeMap sourceCm = (CompositeMap)container.getFocus();
				if (sourceCm == null)
					return;
				CompositeMap objectCm = (CompositeMap) event.item.getData();
				if (objectCm == null)
					return;
				if (objectCm.equals(sourceCm)
						&& objectCm.toXML().equals(sourceCm.toXML())) {
					return;
				}
				if (!validNextNodeLegalWithAction(objectCm, sourceCm)) {
					return;
				}
				CompositeMap childCm = new CompositeMap(sourceCm);

				if (childCm != null) {
					objectCm.addChild(childCm);
					if (sourceCm.getParent() != null)
						sourceCm.getParent().removeChild(sourceCm);
				}
				container.refresh(true);
			}

			public void dragEnter(DropTargetEvent event) {
				// System.out.println(event.getSource());

			}
		});
	}

	public static void fillKeyListener(final IContainer container) {
		TreeViewer treeViewer = (TreeViewer) container.getViewer();
		treeViewer.getTree().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && e.keyCode == 'c') {
					copyElement(container);
				} else if (e.stateMask == SWT.CTRL && e.keyCode == 'v') {
					pasteElement(container);
				} else if (e.keyCode == SWT.DEL) {
					CompositeMapAction.removeElement(container);
				}
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	public static List getAvailableChildElements(Element element,
			CompositeMap selectedCM) {
		if (element == null)
			return null;
		List childElements = new LinkedList();
		if (element.isArray()) {
			IType type = element.getElementType();
			if (!(type instanceof Element))
				return childElements;
			Element arrayType = Common.getSchemaManager().getElement(
					type.getQName());
			childElements.add(arrayType);
			return childElements;
		}
		childElements = getChildElements(element,selectedCM);
		return childElements;
	}

	private static List getChildElements(Element element, CompositeMap selectedCM) {
		Set schemaChilds = getSchemaChilds(element,Common.getSchemaManager());
		List availableChilds = new ArrayList();

		if (schemaChilds != null) {
			Iterator ite = schemaChilds.iterator();
			while (ite.hasNext()) {
				Object object = ite.next();
				if (!(object instanceof Element))
					continue;
				Element ele = (Element) object;
				final QualifiedName qName = ele.getQName();
				if (ele.getMaxOccurs() == null) {
					availableChilds.add(ele);
					continue;
				}
				int maxOccurs = Integer.valueOf(ele.getMaxOccurs()).intValue();
				int nowOccurs = getCountOfChildElement(selectedCM, qName);
				if (nowOccurs < maxOccurs) {
					availableChilds.add(ele);
				}
			}
		}
		return availableChilds;
	}
	public static Set getSchemaChilds(Element element,SchemaManager manager){
		Set childs = new HashSet();
		
		Set childElements = element.getChilds();
		for(Iterator cit = childElements.iterator(); cit!=null && cit.hasNext();){
			Object node = cit.next();
			if(!(node instanceof ComplexType))
				continue;
			ComplexType context = (ComplexType)node;
			//context instanceof Element结果永远是true，所以要获得它的原始定义
			ComplexType original = manager.getComplexType(context.getQName());
			if (original instanceof Element) {
				Element new_name = (Element) context;
				childs.add(new_name);
			}
			else{
				childs.addAll(manager.getElementsOfType(original));
			}
		}
		List complexTypes = element.getAllExtendedTypes();
		if(complexTypes == null)
			return childs;
		for(Iterator cit = complexTypes.iterator(); cit!=null && cit.hasNext();){
			ComplexType ct = (ComplexType)cit.next();
//			System.out.println("ExtendedTypes:"+ct.getLocalName());
			if (ct instanceof Element) {
				Element new_name = (Element) ct;
				childs.addAll(getSchemaChilds(new_name,manager));
			}
//			else{
//				complexTypes.addAll(manager.getElementsOfType(ct));
//			}
				
		}
		return childs;
	}

	public static int getCountOfChildElement(CompositeMap cm, QualifiedName qName) {
		List childs = cm.getChildsNotNull();
		int count = 0;
		Iterator it = childs.iterator();
		for (; it.hasNext();) {
			CompositeMap node = (CompositeMap) it.next();
			if (node.getQName().equals(qName)) {
				count++;
			}
		}
		return count;
	}

	public static boolean validNextNodeLegalWithAction(CompositeMap element,
			CompositeMap child) {
		if (!validNextNodeLegal(element, child)) {
			String warning = "";
			if (element == null) {
				warning = Common.getString("parent.element.is.null");
			} else if (element == null) {
				warning = Common.getString("child.element.is.null");
			} else {
				warning = " " + element.getQName().getLocalName() + " "+Common.getString("undefined")
						 +child.getQName().getLocalName() + " "+Common.getString("child.element");
			}
			Common.showWarningMessageBox(null, warning);
			return false;
		}
		return true;
	}

	public static boolean validNextNodeLegal(CompositeMap element,
			CompositeMap child) {
		if (element == null || child == null)
			return false;
		Element em = Common.getSchemaManager().getElement(element);
		return validNextNodeLegal(em, child.getQName());
	}

	public static boolean validNextNodeLegal(Element element, QualifiedName child) {
		if (element == null || child == null)
			return false;
		if (element.isArray()) {
			QualifiedName array = element.getElementType().getQName();
			if (array.equals(child)) {
				return true;
			}
		}
		List childElements = element.getChildElements(Common.getSchemaManager());
		if (childElements != null) {
			Iterator ite = childElements.iterator();
			while (ite.hasNext()) {
				Object object = ite.next();
				if (!(object instanceof Element))
					continue;
				Element ele = (Element) object;
				if (child.equals(ele.getQName()))
					return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
