/*
 * Created on 2009-7-3
 */
package uncertain.ide.eclipse.editor.sxsd;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import aurora_ide.Activator;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Array;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import uncertain.schema.editor.CompositeMapEditor;

public class SxsdTreeContentProvider implements ITreeContentProvider {

	public SxsdTreeContentProvider() {
		super();
	}

	public Object[] getChildren(Object parentElement) {
		boolean debug = false;
		if (debug)
			System.out.println("getChildren:");
		if (parentElement == null)
			return null;
		CompositeMap map = (CompositeMap) parentElement;
		// System.out.println(map.toXML());
		List childs = map.getChilds();

		if (childs == null) {
			Element element = Activator.getSchemaManager().getElement(map);
			if (debug)
				System.out.println("element   ！");
			if (element != null) {
				List arrays = element.getAllArrays();
				// System.out.println("arrays..");
				if (arrays != null) {
					Iterator ite = arrays.iterator();
					while (ite.hasNext()) {
						// System.out.println(map.toXML());
						String name = (String) ite.next();
						CompositeMap newMap = new CompositeMap(name);
						map.addChild(newMap);
						childs = map.getChilds();
						// System.out.println("array:"+name);
						// System.out.println(newMap.toXML());
						return childs.toArray();
					}
				}
			}

			return null;
		} else
			return childs.toArray();
	}

	public Object getParent(Object element) {
		System.out.println("getParent:");
		if (element == null)
			return null;
		CompositeMap map = (CompositeMap) element;
		// System.out.println(map.toXML());
		return map.getParent();
	}

	public boolean hasChildren(Object element) {
		boolean debug = false;
		if (debug)
			System.out.println("hasChildren:");
		if (element == null)
			return false;
		CompositeMap map = (CompositeMap) element;
		if (debug)
			System.out.println(map.toXML());
		List childs = map.getChilds();
		boolean falg = childs != null;
		if (falg)
			return falg;
//		Element ele = mSchemaManager.getElement(map);
//		if (debug)
//			System.out.println("element   ！");
//		if (ele != null) {
//			List arrays = ele.getAllArrays();
//			if (arrays != null) {
//				Iterator ite = arrays.iterator();
//				while (ite.hasNext()) {
//					if (debug)
//						System.out.println(map.toXML());
//					Array uncetainArray = (Array) ite.next();
//					String name = uncetainArray.getLocalName();
//					CompositeMap newMap = new CompositeMap(name);
//					map.addChild(newMap);
//					childs = map.getChilds();
//
//					if (debug)
//						System.out.println("有子节点了：" + map.toXML());
//					return true;
//				}
//			}
//		}
		return falg;
	}

	public Object[] getElements(Object inputElement) {
		boolean debug = false;
		if (debug)
			System.out.println("getElements:");
		if (inputElement == null)
			return null;
		CompositeMap map = (CompositeMap) inputElement;
		if (debug)
			System.out.println("map:"+map.toXML());
//		isElement(map);
		List childs = map.getChilds();
		

		if (childs == null) {
			Element ele = Activator.getSchemaManager().getElement(map);
			if (ele != null) {
				if (debug)
					System.out.println("ele   ！");
				List arrays = ele.getAllArrays();
				if (arrays != null) {
					Iterator ite = arrays.iterator();
					while (ite.hasNext()) {
//						if (debug)
//							System.out.println(map.toXML());
						Array uncetainArray = (Array) ite.next();
						String name = uncetainArray.getLocalName();
						CompositeMap newMap = new CompositeMap(null,map.getNamespaceURI(),name);
						map.addChild(newMap);
						if (debug)
							System.out.println("有子节点了：" + map.toXML());
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
	public static void isElement(CompositeMap data){
		System.out.println(data.toXML());
//		Element element = mSchemaManager.getElement(data);
//		if(element != null){
//			System.out.println("it is a element.");
//		}
//		else{
//			System.out.println("it is not a element.");
//		}
        QualifiedName qname = data.getQName();
        System.out.println("qname:"+qname.getFullName());
        Element element = Activator.getSchemaManager().getElement(qname);
        if(element==null){
        	System.out.println("element is null");
            CompositeMap parent = data.getParent();
            if(parent!=null){
            	System.out.println("parent is not null");
                Element parent_element = Activator.getSchemaManager().getElement(parent);
                System.out.println("parent："+parent.toXML());
                if(parent_element!=null){
                	System.out.println("parent_element is not null");
                    element = parent_element.getElement(qname);
                }
            }
        }
		if(element != null){
		System.out.println("it is a element.");
		}
		else{
			System.out.println("it is not a element.");
		}

	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}
