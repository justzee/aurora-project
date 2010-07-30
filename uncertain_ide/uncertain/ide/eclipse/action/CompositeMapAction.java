/**
 * 
 */
package uncertain.ide.eclipse.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.QualifiedName;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.schema.Array;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.IType;
import uncertain.schema.Namespace;
import uncertain.schema.Schema;
import uncertain.schema.SchemaManager;

public class CompositeMapAction {

	/**
	 * @param args
	 */
	private CompositeMapAction instance;

	private CompositeMapAction() {

	}

	public CompositeMapAction getInstance() {
		if (instance != null)
			return instance;
		else {
			instance = new CompositeMapAction();
			return instance;
		}

	}

	public static String getElementFullName(CompositeMap cm, QualifiedName qName) {
		String text = null;
		String prefix = getPrefix(cm, qName);
		String localName = qName.getLocalName();
		if (prefix != null)
			text = prefix + ":" + localName;
		else
			text = localName;
		return text;
	}

	public static String getPrefix(CompositeMap cm, QualifiedName qName) {
		if (qName == null) {
			return null;
		}
		if (cm == null) {
			return qName.getPrefix();
		}
		Map prefix_mapping = CompositeUtil.getPrefixMapping(cm);
		String getNameSpace = qName.getNameSpace();
		Object uri_ot = prefix_mapping.get(getNameSpace);
		if (uri_ot != null)
			return (String) uri_ot;
		else
			return qName.getPrefix();
	}

	public static CompositeMap addElement(CompositeMap parent, String prefix,
			String uri, String name) {

		CompositeMap child = new CompositeMap(prefix, uri, name);
		parent.addChild(child);
		addArrayNode(parent);
		return child;
	}
	public static void addArrayNode(CompositeMap parent) {
		Element element = LoadSchemaManager.getSchemaManager().getElement(parent);
		if (element != null && element.isArray()) {
			QualifiedName qName = parent.getQName();
			if (CompositeUtil.findChild(parent.getParent(), qName) == null) {
				parent.getParent().addChild(parent);
			}
		}
	}

	public static void addElementArray(CompositeMap parentCM) {
		Element element = LoadSchemaManager.getSchemaManager().getElement(parentCM);
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
	public static List getAvailableChildElements(Element element,
			CompositeMap selectedCM) {
		if (element == null)
			return null;
		List childElements = new LinkedList();
		if (element.isArray()) {
			IType type = element.getElementType();
			if (!(type instanceof Element))
				return childElements;
			Element arrayType = LoadSchemaManager.getSchemaManager().getElement(
					type.getQName());
			childElements.add(arrayType);
			return childElements;
		}
		childElements = getChildElements(element,selectedCM);
		return childElements;
	}

	private static List getChildElements(Element element, CompositeMap selectedCM) {
		Set schemaChilds = getSchemaChilds(element,LoadSchemaManager.getSchemaManager());
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
				warning = LocaleMessage.getString("parent.element.is.null");
			} else if (element == null) {
				warning = LocaleMessage.getString("child.element.is.null");
			} else {
				warning = " " + element.getQName().getLocalName() + " "+LocaleMessage.getString("undefined")
						 +child.getQName().getLocalName() + " "+LocaleMessage.getString("child.element");
			}
			CustomDialog.showWarningMessageBox(null, warning);
			return false;
		}
		return true;
	}

	public static boolean validNextNodeLegal(CompositeMap element,
			CompositeMap child) {
		if (element == null || child == null)
			return false;
		Element em = LoadSchemaManager.getSchemaManager().getElement(element);
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
		List childElements = element.getChildElements(LoadSchemaManager.getSchemaManager());
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
	// TODO 未使用
	public Set getMaxOcuss(Element element, SchemaManager manager) {
		Set allChildElements = new HashSet();
		Set childElements = element.getChilds();
		for (Iterator cit = childElements.iterator(); cit != null
				&& cit.hasNext();) {
			Object node = cit.next();
			if (!(node instanceof ComplexType))
				continue;
			ComplexType ct = (ComplexType) node;
			if (ct instanceof Element) {
				Element new_name = (Element) ct;
				allChildElements.add(new_name);
			} else {
				allChildElements.addAll(manager.getElementsOfType(ct));
			}
		}
		List complexTypes = element.getAllExtendedTypes();
		if (complexTypes == null)
			return allChildElements;
		for (Iterator cit = complexTypes.iterator(); cit != null
				&& cit.hasNext();) {
			ComplexType ct = (ComplexType) cit.next();
			// System.out.println("ExtendedTypes:"+ct.getLocalName());
			if (ct instanceof Element) {
				Element new_name = (Element) ct;
				allChildElements.addAll(getMaxOcuss(new_name, manager));
			}
			// else{
			// complexTypes.addAll(manager.getElementsOfType(ct));
			// }

		}
		return allChildElements;
	}

	public static Namespace getQualifiedName(CompositeMap root,String prefix) throws Exception{
		 Map namespace_mapping = CompositeUtil.getPrefixMapping(root);
		 Schema schema = new Schema();
		 Namespace[] ns = getNameSpaces(namespace_mapping);
		 schema.addNameSpaces(ns);
		 Namespace nameSpace = schema.getNamespace(prefix);
		 return nameSpace;
	}
	
	private static Namespace[] getNameSpaces(Map namespaceToPrefix) {
		if (namespaceToPrefix == null)
			return null;

		Namespace[] namespaces = new Namespace[namespaceToPrefix.keySet()
				.size()];
		Iterator elements = namespaceToPrefix.keySet().iterator();
		int i = 0;
		while (elements.hasNext()) {
			Object element = elements.next();
			Namespace namespace = new Namespace();
			namespace.setPrefix(namespaceToPrefix.get(element).toString());
			namespace.setUrl(element.toString());
			namespaces[i] = namespace;
		}
		return namespaces;
	}
	public static void main(String[] args) {
	}

}
