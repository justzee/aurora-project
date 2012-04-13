package aurora.ide.search.cache;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.ApplicationException;

public class CacheManager {
	private static CompositeMapCacher mapCacher;
	private static DocumentCacher documentCacher;

	public static final CompositeMapCacher getCompositeMapCacher() {
		if (mapCacher == null) {
			mapCacher = new CompositeMapCacher();
		}
		return mapCacher;
	}

	public static final DocumentCacher getDocumentCacher() {
		if (documentCacher == null) {
			documentCacher = new DocumentCacher();
		}
		return documentCacher;
	}

	public static CompositeMap getCompositeMap(IFile file)
			throws CoreException, ApplicationException {
		return getCompositeMapCacher().getCompositeMap(file);
	}

	public static CompositeMap getWholeBMCompositeMap(IFile file)
			throws CoreException, ApplicationException {
		return getCompositeMapCacher().getWholeCompositeMap(file);
	}

	public static IDocument getDocument(IFile file) throws CoreException {
		return getDocumentCacher().getDocument(file);
	}

}
