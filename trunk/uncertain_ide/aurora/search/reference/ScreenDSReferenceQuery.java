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

public class ScreenDSReferenceQuery extends AbstractSearchQuery {

	private AuroraSearchResult fResult;
	private IResource scope;
	private IFile sourceFile;
	private String datasetName;

	public ScreenDSReferenceQuery(IResource scope, IFile sourceFile,
			String datasetName) {
		super();
		this.scope = scope;
		this.sourceFile = sourceFile;
		this.datasetName = datasetName;
	}

	public IStatus run(IProgressMonitor monitor)
			throws OperationCanceledException {
		AbstractTextSearchResult textResult = (AbstractTextSearchResult) getSearchResult();
		textResult.removeAll();
		SearchEngine engine = new SearchEngine(this);
		engine.findDSReference(scope, sourceFile, datasetName, monitor);
//		engine.findBMFieldReference(scope, sourceFile, datasetName, monitor);
		return Status.OK_STATUS;
	}

	public String getLabel() {
		return "DataSet Reference : " + sourceFile.getName();
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
		ScreenDSReferenceService service = new ScreenDSReferenceService(
				this.scope, this.sourceFile, this, this.datasetName);
		return service;
	}

}
