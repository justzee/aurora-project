package aurora.ide.search.reference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.search.ui.ISearchQuery;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.search.core.AbstractSearchService;
import aurora.ide.search.core.CompositeMapIteator;
import aurora.ide.search.core.Util;

public class ReferenceSearchService extends AbstractSearchService implements
		IDataFilter {

	private IResource scope;

	public ReferenceSearchService(IResource scope, Object source,
			ISearchQuery query) {
		super(new IResource[] { scope }, source, query);
		this.scope = scope;
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

	public boolean found(CompositeMap map, Attribute attrib) {
		IType attributeType = attrib.getAttributeType();
		if (attributeType instanceof SimpleType) {
			QualifiedName referenceTypeQName = ((SimpleType) attributeType)
					.getReferenceTypeQName();
			if (bmReference.equals(referenceTypeQName)) {
				return bmRefMatch(map, attrib);
			}
			if (screenReference.equals(referenceTypeQName)) {
				return screenRefMatch(map, attrib);
			}
		}
		return false;

	}

	private boolean screenRefMatch(CompositeMap map, Attribute attrib) {
		IFile file = this.getFile(map.getRoot());
		Object pkg = map.get(attrib.getName());
		IFile findScreenFile = Util.findScreenFile(file, pkg);
		return this.getSource().equals(findScreenFile);
	}

	private boolean bmRefMatch(CompositeMap map, Attribute attrib) {
		Object pattern = getSearchPattern(this.getRoots(), this.getSource());
		Object data = map.get(attrib.getName());
		return pattern == null ? false : pattern.equals(data);
	}

	protected IDataFilter getDataFilter(final IResource[] scope,
			final Object source) {
		return this;
	}

	protected Object createPattern(IResource[] roots, Object source) {
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
		return file.getName();
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
