package aurora.search.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import aurora.search.reference.BMFieldReferenceService;
import aurora.search.reference.ReferenceSearchService;
import aurora.search.reference.ScreenDSReferenceService;

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

	public IStatus findReference(IResource scope, IFile sourceFile,
			IProgressMonitor monitor) {
		ReferenceSearchService service = new ReferenceSearchService(scope,
				sourceFile, query);
		service.service(monitor);
		return Status.OK_STATUS;
	}

	public IStatus findBMFieldReference(IResource scope, IFile sourceFile,
			String fieldName, IProgressMonitor monitor) {
		ReferenceSearchService service = new BMFieldReferenceService(scope,
				sourceFile, query, fieldName);
		service.service(monitor);
		return Status.OK_STATUS;
	}

	public IStatus findDSReference(IResource scope, IFile sourceFile,
			String datasetName, IProgressMonitor monitor) {
		ScreenDSReferenceService service = new ScreenDSReferenceService(scope,
				sourceFile, query, datasetName);
		service.service(monitor);
		return Status.OK_STATUS;
	}

}
