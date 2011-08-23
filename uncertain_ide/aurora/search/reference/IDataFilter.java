package aurora.search.reference;

import uncertain.composite.CompositeMap;

public interface IDataFilter {
	boolean found(CompositeMap map,Object data);
}
