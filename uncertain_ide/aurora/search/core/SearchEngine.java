package aurora.search.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import aurora.search.reference.ReferenceSearchService;

public class SearchEngine {

	private AbstractSearchQuery query;

	public SearchEngine() {

	}

	// public IStatus search(IContainer scope, Pattern searchPattern,
	// IProgressMonitor monitor) {
	// ReferenceSearchService service = new ReferenceSearchService();
	// service.search();
	// return new Status(IStatus.OK, "aa", 0, "aa", null);
	// }

	public SearchEngine(AbstractSearchQuery query) {
		this.query = query;
	}

	public IStatus findReference(IContainer scope, IFile sourceFile,
			IProgressMonitor monitor) {
		ReferenceSearchService service = new ReferenceSearchService(query);
		service.service(scope, sourceFile, monitor);
		return Status.OK_STATUS;
	}

}
