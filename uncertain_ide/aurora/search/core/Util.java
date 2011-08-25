package aurora.search.core;

import helpers.LoadSchemaManager;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;

public class Util {
	public static Object getReferenceModelPKG(CompositeMap map) {
		Element element = LoadSchemaManager.getSchemaManager().getElement(map);
		if (element != null) {
			List attrib_list = element.getAllAttributes();
			for (Iterator it = attrib_list.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
				IType attributeType = attrib.getAttributeType();
				boolean referenceOf = isBMReference(attributeType);
				if (referenceOf) {
					Object data = map.get(attrib.getName());
					return data;
				}
			}
		}
		return null;
	}

	public static boolean isBMReference(IType attributeType) {
		if (attributeType instanceof SimpleType) {
			return AbstractSearchService.bmReference
					.equals(((SimpleType) attributeType)
							.getReferenceTypeQName());
		}
		return false;
	}

	public static IContainer findWebInf(IResource resource) {

		if (null == resource) {
			return null;
		}
		String name = resource.getName();
		if (resource.getType() == IResource.FOLDER && "WEB-INF".equals(name)) {
			return (IContainer) resource;
		} else {
			return findWebInf(resource.getParent());
		}

	}
}
