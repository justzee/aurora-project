package aurora.ide.search.reference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.search.ui.ISearchQuery;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.search.core.AbstractSearchService;
import aurora.ide.search.core.CompositeMapIteator;

public class ScreenDSReferenceService extends AbstractSearchService {

	private String datasetName;

	public ScreenDSReferenceService(IResource scope, Object source,
			ISearchQuery query, String datasetName) {
		super(new IResource[] { scope }, source, query);
		this.datasetName = datasetName;
	}

	protected CompositeMapIteator createIterationHandle(IFile resource) {
		return new ReferenceTypeFinder(datasetReference);
	}

	protected IDataFilter getDataFilter(final IResource[] roots,
			final Object source) {
		IDataFilter filter = new IDataFilter() {
			public boolean found(CompositeMap map, Attribute attrib) {
				Object pattern = getSearchPattern(roots, source);
				Object data = map.get(attrib.getName());
				return pattern == null ? false : pattern.equals(data);
			}

		};
		return filter;
	}

	protected Object createPattern(IResource[] roots, Object source) {
		return datasetName;
	}

}
