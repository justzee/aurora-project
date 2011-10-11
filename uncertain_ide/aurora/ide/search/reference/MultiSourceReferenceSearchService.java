package aurora.ide.search.reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.ui.ISearchQuery;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.search.core.CompositeMapIteator;
import aurora.ide.search.ui.LineElement;

public class MultiSourceReferenceSearchService extends ReferenceSearchService {

	private List<String> patterns;

	private Map<CompositeMap, String> patternMap = new HashMap<CompositeMap, String>();

	public MultiSourceReferenceSearchService(IResource scope, IFile[] sources,
			ISearchQuery query) {
		super(scope, null, query);
		this.patterns = this.createPatterns(this.getRoots(), sources);
	}

	private List<String> createPatterns(IResource[] roots, IFile[] sources) {
		List<String> patterns = new ArrayList<String>();
		for (IFile source : sources) {
			Object p = this.createPattern(roots, source);
			if (p instanceof String) {
				patterns.add((String) p);
			}
		}
		return patterns;
	}

	@Override
	protected CompositeMapIteator createIterationHandle(IFile file) {
		return new ReferenceTypeFinder(bmReference);
	}

	@Override
	protected boolean bmRefMatch(CompositeMap map, Attribute attrib) {
		Object data = map.get(attrib.getName());
		boolean contains = patterns.contains(data);
		if (contains) {
			patternMap.put(map, data.toString());
		}
		return contains;
	}

	@Override
	protected List createLineMatches(MapFinderResult r, LineElement l,
			IFile file, Object pattern) throws CoreException {
		String p = this.patternMap.get(r.getMap());
		if (p != null) {
			return super.createLineMatches(r, l, file, p);
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	protected Object createPattern(IResource[] roots, Object source) {
		if(source ==null){
			return "Aurora Multi-References";
		}
		return super.createPattern(roots, source);
	}

}
