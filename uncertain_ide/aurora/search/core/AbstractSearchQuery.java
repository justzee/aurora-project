package aurora.search.core;

import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

public abstract class AbstractSearchQuery implements ISearchQuery {

	final public ISearchResult getSearchResult() {
		
		return getAruroraSearchResult();
	}

	protected abstract AbstractSearchResult getAruroraSearchResult() ;
	protected abstract ISearchService getSearchService() ;


}
