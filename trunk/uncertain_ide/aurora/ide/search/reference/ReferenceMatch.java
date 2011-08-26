package aurora.ide.search.reference;

import org.eclipse.core.resources.IFile;

import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.ui.LineElement;

public class ReferenceMatch extends AbstractMatch {

	private IFile file;
	private MapFinderResult matchs;
	private LineElement line ;
	
	public IFile getFile() {
		return file;
	}

	public void setFile(IFile file) {
		this.file = file;
	}

	public MapFinderResult getMatchs() {
		return matchs;
	}

	public void setMatchs(MapFinderResult matchs) {
		this.matchs = matchs;
	}

	public ReferenceMatch(Object element, int offset, int length) {
	    super(element, offset, length);
	    this.file = (IFile) element;
	}

	public ReferenceMatch(IFile resource, int i, int j, LineElement line) {
		this(resource,i,j);
		this.line = line;
	}

	public LineElement getLineElement() {
		return line;
	}

}
