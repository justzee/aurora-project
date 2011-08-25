package aurora.search.reference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import aurora.search.core.AbstractSearchQuery;
import aurora.search.core.AbstractSearchResult;
import aurora.search.core.AuroraSearchResult;
import aurora.search.core.ISearchService;
import aurora.search.core.SearchEngine;

public class FileReferenceQuery extends AbstractSearchQuery {

	private AuroraSearchResult fResult;
	private IResource scope;
	private IFile sourceFile;

	public FileReferenceQuery(IResource scope, IFile sourceFile) {
		super();
		this.scope = scope;
		this.sourceFile = sourceFile;
	}

	public IStatus run(IProgressMonitor monitor)
			throws OperationCanceledException {
		AbstractTextSearchResult textResult = (AbstractTextSearchResult) getSearchResult();
		textResult.removeAll();

		SearchEngine engine = new SearchEngine(this);
		engine.findReference(scope, sourceFile, monitor);
		return Status.OK_STATUS;
	}

	public String getLabel() {
		return "File Reference : "
				+ sourceFile.getName();
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	protected AbstractSearchResult getAruroraSearchResult() {
		if (fResult == null) {
			AuroraSearchResult result = new AuroraSearchResult(this);
			fResult = result;
		}
		return fResult;
	}

	protected ISearchService getSearchService() {
		ReferenceSearchService service = new ReferenceSearchService(this.scope,this.sourceFile,this);
		return service;
	}
	

}
