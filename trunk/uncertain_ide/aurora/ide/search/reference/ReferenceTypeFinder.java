package aurora.ide.search.reference;

import helpers.ApplicationException;
import helpers.LoadSchemaManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.search.core.CompositeMapIteator;

public class ReferenceTypeFinder extends CompositeMapIteator {

	private QualifiedName referenceType;

	public int process(CompositeMap map) {
		try {
			List match = getMatch(map);
			if (match != null) {
				this.getResult().add(new MapFinderResult(map, match));
			}
		} catch (ApplicationException e) {
			e.printStackTrace();

		}
		return IterationHandle.IT_CONTINUE;
	}

	protected List getMatch(CompositeMap map) throws ApplicationException {
		List matchs = new ArrayList();
		boolean isMacth = false;
		Element element = LoadSchemaManager.getSchemaManager().getElement(map);
		if (element != null) {
			List attrib_list = element.getAllAttributes();
			for (Iterator it = attrib_list.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
				IType attributeType = attrib.getAttributeType();
				boolean referenceOf = isReferenceType(attributeType);
				if (referenceOf) {
					boolean found = true;
					IDataFilter filter = getFilter();
					if (filter != null) {
						found = filter.found(map, attrib);
					}
					if (found) {
						isMacth = true;
						matchs.add(attrib);
					}
				}
			}
			if (isMacth) {
				return matchs;
			}
		}

		return null;
	}

	protected boolean isReferenceType(IType attributeType) {
		if (attributeType instanceof SimpleType) {
			return referenceType.equals(((SimpleType) attributeType)
					.getReferenceTypeQName());
		}
		return false;
	}

	public ReferenceTypeFinder(QualifiedName referenceType) {
		this.referenceType = referenceType;
	}

}
