package aurora.search.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.Match;


public class AuroraSearchQuery extends AbstractSearchQuery  {

	private AuroraSearchResult fResult;

	public IStatus run(IProgressMonitor monitor)
			throws OperationCanceledException {
		SearchEngine engine= new SearchEngine();
//		engine.search(null, null, null);
		AuroraSearchResult searchResult = (AuroraSearchResult)this.getSearchResult();
		Match match = new Match ("xyz",1,2);
	
		searchResult.addMatch(match);
		match = new Match ("def",1,2);
	
		searchResult.addMatch(match);
		match = new Match ("ccc",1,2);
		
		searchResult.addMatch(match);
		match = new Match ("aaa",1,2);
		
		searchResult.addMatch(match);
		match = new Match ("xxx",1,2);
		
		searchResult.addMatch(match);
		match = new Match ("bbb",1,2);
	
		searchResult.addMatch(match);
		match = new Match ("yyy",1,2);
		searchResult.addMatch(match);
		
		//TextSearchEngine.create().search(null, null, null, null);
		
		return new Status(IStatus.OK, "aa", 0, "aa", null);
	}

	public String getLabel() {
		return "Aurora Search Query";
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	protected AbstractSearchResult getAruroraSearchResult()  {
		if (fResult == null) {
			AuroraSearchResult result = new AuroraSearchResult(this);
			fResult = result;
		}
		return fResult;
	}

	protected ISearchService getSearchService() {
		// TODO Auto-generated method stub
		return null;
	}

}
