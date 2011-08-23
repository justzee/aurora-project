package aurora.search.reference;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.search.ui.ISearchQuery;

import uncertain.composite.CompositeMap;
import aurora.search.core.AbstractSearchService;
import aurora.search.core.CompositeMapIteator;

public class ReferenceSearchService extends AbstractSearchService {

	public ReferenceSearchService(ISearchQuery query) {
		super(query);

	}

	protected CompositeMapIteator createIterationHandle() {
		return new ReferenceTypeFinder(bmReference);
	}

	protected IDataFilter getDataFilter(final IContainer scope,
			final Object source) {
		IDataFilter filter = new IDataFilter() {
			public boolean found(CompositeMap map, Object data) {
				if (data instanceof String) {
					Object pattern = getSearchPattern(scope, source);
					return pattern == null ? false : pattern
							.equals((String) data);
				}
				return false;
			}
		};
		return filter;
	}

	protected String createPattern(IContainer scope, Object source) {
		if (source instanceof IFile) {
			IPath path = ((IFile) source).getProjectRelativePath()
					.removeFileExtension();
			String[] segments = path.segments();
			StringBuilder result = new StringBuilder();
			StringBuilder _result = new StringBuilder();
			int classes_idx = -1;
			for (int i = 0; i < segments.length; i++) {
				_result.append(segments[i]);
				if (i != segments.length - 1)
					_result.append(".");
				if (classes_idx != -1) {
					result.append(segments[i]);
					if (i != segments.length - 1)
						result.append(".");
				}
				if ("classes".equals(segments[i])) {
					classes_idx = i;
				}
			}
			if (result.length() == 0) {
				result = _result;
			}
			return result.toString();
		}
		return null;
	}

}
