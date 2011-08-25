package aurora.search.reference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.search.ui.ISearchQuery;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.search.core.AbstractSearchService;
import aurora.search.core.CompositeMapIteator;

public class ReferenceSearchService extends AbstractSearchService {

	public ReferenceSearchService(IResource scope, Object source,
			ISearchQuery query) {
		super(scope, source, query);
	}

	protected CompositeMapIteator createIterationHandle(IFile file) {
		Object source = this.getSource();
		if (source instanceof IFile) {
			String fileExtension = ((IFile) source).getFileExtension();
			if ("bm".equalsIgnoreCase(fileExtension)) {
				return new ReferenceTypeFinder(bmReference);
			}
			if ("screen".equalsIgnoreCase(fileExtension)) {
				return new ReferenceTypeFinder(screenReference);
			}
		}
		return null;

	}

	protected IDataFilter getDataFilter(final IResource scope,
			final Object source) {
		IDataFilter filter = new IDataFilter() {
			public boolean found(CompositeMap map, Attribute attrib) {
				Object pattern = getSearchPattern(scope, source);
				Object data = map.get(attrib.getName());
				return pattern == null ? false : pattern.equals(data);
			}
		};
		return filter;
	}

	protected Object createPattern(IResource scope, Object source) {
		if (source instanceof IFile) {
			IFile file = (IFile) source;
			String fileExtension = file.getFileExtension();
			if ("bm".equalsIgnoreCase(fileExtension)) {
				return getBMPKG(scope, file);
			}
			if ("screen".equalsIgnoreCase(fileExtension)) {
				return getScreenPKG(scope, file);
			}

		}
		return null;
	}

	private Object getScreenPKG(IResource scope, IFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object getBMPKG(IResource scope, IFile file) {
		IPath path = file.getProjectRelativePath().removeFileExtension();
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

}
