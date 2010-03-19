/**
 * 
 */
package uncertain.ide.eclipse.action;

import java.util.ArrayList;
import java.util.Collection;
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

	public static void addElement(CompositeMap parentCM, String _prefix,
			String _uri, String _name) {

		CompositeMap newCM = new CompositeMap(_prefix, _uri, _name);
		parentCM.addChild(newCM);
		checkaddArrayNode(parentCM);
	}
	//如果是在空数组下添加节点，则添加这个空数组节点
	private static void checkaddArrayNode(CompositeMap parentCM) {
		Element element = Common.getSchemaManager().getElement(parentCM);
		if (element != null && element.isArray()) {
			QualifiedName nm = parentCM.getQName();
			if (CompositeUtil.findChild(parentCM.getParent(), nm) == null) {
				parentCM.getParent().addChild(parentCM);
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

	public static void removeElement(IViewerDirty viewer) {
		CompositeMap comp = viewer.getFocusData();
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

	public static void removePropertyAction(IPropertyCategory viewer) {
		int buttonID = Common.showConfirmDialogBox(null, Common.getString("delete.attribute.confirm"));
		switch (buttonID) {
		case SWT.OK:
			final CompositeMap data = viewer.getInput();
			AttributeValue av = viewer.getFocusData();
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

	public static void cutElement(IViewerDirty viewer) {
		CompositeMap cm = viewer.getFocusData();
		viewer.setSelectedData(cm);
	}

	public static void copyElement(IViewerDirty viewer) {
		CompositeMap cm = viewer.getFocusData();
		CompositeMap child = new CompositeMap(cm);
		child.setParent(cm.getParent());
		viewer.setSelectedData(child);

	}

	public static void pasteElement(IViewerDirty viewer) {
		CompositeMap selectedCm = viewer.getSelectedData();
		if (selectedCm == null)
			return;
		CompositeMap parentComp = viewer.getFocusData();
		if (!validNextNodeLegalWithAction(parentComp, selectedCm)) {
			return;
		}
		CompositeMap child = new CompositeMap(selectedCm);
		if (child != null) {
			parentComp.addChild(child);
			selectedCm.getParent().removeChild(selectedCm);
			checkaddArrayNode(parentComp);
		}
		selectedCm = null;
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
				MenuManager cascadingMenu = new ContextAddElementMenuManager(
						mDirtyObject, null, null, Common.getString("add.element.label"));
				manager.add(cascadingMenu);
				manager.add(new CopyElementAction(mDirtyObject,
						CopyElementAction.getDefaultImageDescriptor(),
						CopyElementAction.getDefaultText()));
				manager.add(new PasteAction(mDirtyObject, PasteAction
						.getDefaultImageDescriptor(), PasteAction
						.getDefaultText()));
				manager.add(new RemoveElementAction(mDirtyObject,
						RemoveElementAction.getDefaultImageDescriptor(),
						RemoveElementAction.getDefaultText()));
				manager.add(new RefreshAction(mDirtyObject, RefreshAction
						.getDefaultImageDescriptor(), Common.getString("refresh")));
			}
		});

		Menu menu = menuManager.createContextMenu(mDirtyObject.getControl());
		// Menu menu = menuManager.createMenuBar(mColumnViewer.)
		mDirtyObject.getControl().setMenu(menu);

	}

	public static void fillDNDListener(final IViewerDirty mDirtyObject) {

		DragSource ds = new DragSource(mDirtyObject.getControl(), DND.DROP_MOVE);
		ds.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
		ds.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
			}
		});

		DropTarget dt = new DropTarget(mDirtyObject.getControl(), DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
		dt.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				CompositeMap sourceCm = mDirtyObject.getFocusData();
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
					copyElement(mDirtyObject);
				} else if (e.stateMask == SWT.CTRL && e.keyCode == 'v') {
					pasteElement(mDirtyObject);
				} else if (e.keyCode == SWT.DEL) {
					CompositeMapAction.removeElement(mDirtyObject);
				}
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	public static List getAvailableSonElements(Element element,
			CompositeMap selectedCM) {
		if (element == null)
			return null;
		List sonElements = new LinkedList();
		if (element.isArray()) {
			IType type = element.getElementType();
			if (!(type instanceof Element))
				return sonElements;
			Element arrayType = Common.getSchemaManager().getElement(
					type.getQName());
			sonElements.add(arrayType);
			return sonElements;
		}
//		sonElements = element.getSonElements(Activator.getSchemaManager());
		sonElements = getSonElements(element,selectedCM);
		return sonElements;
	}

	private static List getSonElements(Element element, CompositeMap selectedCM) {
		Set schemaChilds = getSchemaChilds(element,Common.getSchemaManager());
		List availableSons = new ArrayList();

		if (schemaChilds != null) {
			Iterator ite = schemaChilds.iterator();
			while (ite.hasNext()) {
				Object object = ite.next();
				if (!(object instanceof Element))
					continue;
				Element ele = (Element) object;
				final QualifiedName qName = ele.getQName();
				if (ele.getMaxOccurs() == null) {
					availableSons.add(ele);
					continue;
				}
				int maxOccurs = Integer.valueOf(ele.getMaxOccurs()).intValue();
				int nowOccurs = getCountOfSonElement(selectedCM, qName);
				if (nowOccurs < maxOccurs) {
					availableSons.add(ele);
				}
			}
		}
		return availableSons;
	}
	private void print(Collection collection){
		if (collection != null) {
			Iterator ite = collection.iterator();
			while (ite.hasNext()) {
				Object object = ite.next();
				if (!(object instanceof Element))
					continue;
				Element ele = (Element) object;
				System.out.println("localnamE:" + ele.getLocalName());
				System.out.println("getMaxOccurs:" + ele.getMaxOccurs());
				
			}
		}
	}
	
	
	public static Set getSchemaChilds(Element element,SchemaManager manager){
		Set childs = new HashSet();
		
		Set sonElements = element.getChilds();
		for(Iterator cit = sonElements.iterator(); cit!=null && cit.hasNext();){
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

	public static int getCountOfSonElement(CompositeMap cm, QualifiedName qName) {
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
			CompositeMap son) {
		if (!validNextNodeLegal(element, son)) {
			String warning = "";
			if (element == null) {
				warning = Common.getString("parent.element.is.null");
			} else if (element == null) {
				warning = Common.getString("son.element.is.null");
			} else {
				warning = " " + element.getQName().getLocalName() + " "+Common.getString("undefined")
						 +son.getQName().getLocalName() + " "+Common.getString("son.element");
			}
			Common.showWarningMessageBox(null, warning);
			return false;
		}
		return true;
	}

	public static boolean validNextNodeLegal(CompositeMap element,
			CompositeMap son) {
		if (element == null || son == null)
			return false;
		Element em = Common.getSchemaManager().getElement(element);
		return validNextNodeLegal(em, son.getQName());
	}

	public static boolean validNextNodeLegal(Element element, QualifiedName son) {
		if (element == null || son == null)
			return false;
		if (element.isArray()) {
			QualifiedName array = element.getElementType().getQName();
			if (array.equals(son)) {
				return true;
			}
		}
		List sonElements = element.getSonElements(Common.getSchemaManager());
		if (sonElements != null) {
			Iterator ite = sonElements.iterator();
			while (ite.hasNext()) {
				Object object = ite.next();
				if (!(object instanceof Element))
					continue;
				Element ele = (Element) object;
				if (son.equals(ele.getQName()))
					return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
