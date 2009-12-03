/*
 * Created on 2009-7-3
 */
package uncertain.ide.eclipse.editor;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.schema.Array;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;

public class TreeContentProvider implements ITreeContentProvider {

	
	CompositeMap rootElement;
	public TreeContentProvider(CompositeMap rootElement) {
		super();
		this.rootElement = rootElement;
	}


	public Object[] getChildren(Object parentElement) {
		if (parentElement == null)
			return null;
		CompositeMap map = (CompositeMap) parentElement;
		List childs = map.getChilds();

		if (childs == null) {
			Element element = Activator.getSchemaManager().getElement(map);
			if (element != null) {
				List arrays = element.getAllArrays();
				if (arrays != null) {
					Iterator ite = arrays.iterator();
					while (ite.hasNext()) {
						String name = (String) ite.next();
						CompositeMap newMap = new CompositeMap(name);
						map.addChild(newMap);
						childs = map.getChilds();
						return childs.toArray();
					}
				}
			}

			return null;
		} else
			return childs.toArray();
	}

	public Object getParent(Object element) {
		if (element == null)
			return null;
		CompositeMap map = (CompositeMap) element;
		return map.getParent();
	}

	public boolean hasChildren(Object element) {

		if (element == null)
			return false;
		CompositeMap map = (CompositeMap) element;

		List childs = map.getChilds();
		boolean falg = childs != null;
		return falg;
	}

	public Object[] getElements(Object inputElement) {

		if (inputElement == null)
			return null;
		// CompositeMap root = new CompositeMap();

		CompositeMap map = (CompositeMap) inputElement;
		if (map.getChild(rootElement) != null && !map.equals(rootElement)) {
			return new Object[] { rootElement };
		}
		List childs = map.getChilds();
//		Element element = Activator.getSchemaManager().getElement(map);
//		if (element != null) {
//			List arrays = element.getAllArrays();
//			if (arrays != null) {
//				Iterator ite = arrays.iterator();
//				while (ite.hasNext()) {
//					Array uncetainArray = (Array) ite.next();
//					String name = uncetainArray.getLocalName();
//					CompositeMap newCM = new CompositeMap(map.getPrefix(),
//							map.getNamespaceURI(), name);
//					if(map.getChild(newCM)== null)
//						map.addChild(newCM);
//				}
//			}
//		}
		if (childs == null) {
			Element ele = Activator.getSchemaManager().getElement(map);
			if (ele != null) {
				List arrays = ele.getAllArrays();
				if (arrays != null) {
					Iterator ite = arrays.iterator();
					while (ite.hasNext()) {
						Array uncetainArray = (Array) ite.next();
						String name = uncetainArray.getLocalName();
						CompositeMap newMap = new CompositeMap(null, map
								.getNamespaceURI(), name);
						map.addChild(newMap);
					}
				}
			}
		}

		childs = map.getChilds();

		if (childs == null)
			return null;
		else
			return childs.toArray();
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}
