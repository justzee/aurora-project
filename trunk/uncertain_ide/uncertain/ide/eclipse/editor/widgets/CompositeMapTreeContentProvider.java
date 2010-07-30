/*
 * Created on 2009-7-3
 */
package uncertain.ide.eclipse.editor.widgets;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.QualifiedName;
import uncertain.ide.LoadSchemaManager;
import uncertain.schema.Array;
import uncertain.schema.Element;

public class CompositeMapTreeContentProvider implements ITreeContentProvider {

	
	CompositeMap rootElement;
	public CompositeMapTreeContentProvider(CompositeMap rootElement) {
		super();
		this.rootElement = rootElement;
	}


	public Object[] getChildren(Object parentElement) {
		if (parentElement == null)
			return null;
		CompositeMap map = (CompositeMap) parentElement;
		List childs = new LinkedList(map.getChildsNotNull());

//		System.out.println(map.toXML());
		Element element = LoadSchemaManager.getSchemaManager().getElement(map);
		if (element != null) {
			List arrays = element.getAllArrays();
			if (arrays != null) {
				Iterator ite = arrays.iterator();
				while (ite.hasNext()) {
					Array uncetainArray = (Array) ite.next();
					String name = uncetainArray.getLocalName();
					CompositeMap newCM = new CompositeMap(map.getPrefix(),
							map.getNamespaceURI(), name);
					QualifiedName nm = newCM.getQName();
					if(CompositeUtil.findChild(map, nm)==null){
						newCM.setParent(map);
						childs.add(newCM);
//						System.out.println(map.toXML());
					}
				}
			}
		}
		if (childs == null)
			return null;
		else{
//			Collections.sort(childs);   
			return childs.toArray();
		}
	}

	public Object getParent(Object element) {
//		System.out.println("getParent...");
		if (element == null)
			return null;
		CompositeMap map = (CompositeMap) element;
//		return map.getParent();
		CompositeMap parent= map.getParent();
//		System.out.println("parent:"+parent.toXML());
		return parent;
	}

	public boolean hasChildren(Object element) {
		if (element == null)
			return false;
		CompositeMap map = (CompositeMap) element;

		List childs = map.getChilds();
		if(childs != null){
			return true;
		}
		// this element maybe have arrays
		else{
			Element cm = LoadSchemaManager.getSchemaManager().getElement(map);
			if(cm != null &&!cm.getAllArrays().isEmpty()){
				return true;
			}
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement == null)
			return null;

		CompositeMap map = (CompositeMap) inputElement;
//		if (map.getChild(rootElement) != null && !map.equals(rootElement)) {
//			return new Object[] { rootElement };
//		}
		if (map.equals(rootElement.getParent())&& !map.equals(rootElement)) {
			return new Object[] { rootElement };
		}
		List childs = new LinkedList(map.getChildsNotNull());

//		System.out.println(map.toXML());
		Element element = LoadSchemaManager.getSchemaManager().getElement(map);
		if (element != null) {
			List arrays = element.getAllArrays();
			if (arrays != null) {
				Iterator ite = arrays.iterator();
				while (ite.hasNext()) {
					Array uncetainArray = (Array) ite.next();
					String name = uncetainArray.getLocalName();
					QualifiedName qn = uncetainArray.getQName();
					CompositeMap newCM = new CompositeMap(qn.getPrefix(),
							qn.getNameSpace(), name);
					QualifiedName nm = newCM.getQName();
					if(CompositeUtil.findChild(map, nm)==null){
						newCM.setParent(map);
						childs.add(newCM);
//						System.out.println(map.toXML());
					}
				}
			}
		}
		if (childs == null)
			return null;
		else{
//			Collections.sort(childs);   
			return childs.toArray();
		}
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}
