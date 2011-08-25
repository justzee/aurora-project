package aurora.search.reference;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;

public interface IDataFilter {
	boolean found(CompositeMap map,Attribute attrib);
}
