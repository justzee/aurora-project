package aurora.ide.search.core;

import java.util.ArrayList;
import java.util.List;

import uncertain.composite.IterationHandle;
import aurora.ide.search.reference.IDataFilter;

public abstract class CompositeMapIteator implements IterationHandle {
	private IDataFilter filter;

	private List result = new ArrayList();

	public IDataFilter getFilter() {
		return filter;
	}

	public void setFilter(IDataFilter filter) {
		this.filter = filter;
	}

	public List getResult() {
		return result;
	}

}
