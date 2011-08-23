package aurora.search.core;

import helpers.ApplicationException;
import helpers.CompositeMapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.part.FileEditorInput;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.util.resource.Location;
import aurora.search.reference.IDataFilter;
import aurora.search.reference.MapFinderResult;
import aurora.search.reference.ReferenceMatch;
import aurora.search.ui.LineElement;
import editor.textpage.XMLDocumentProvider;

abstract public class AbstractSearchService implements ISearchService {
	public final static QualifiedName bmReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "model");
	public final static QualifiedName fieldReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "field");
	private Map documentMap = new HashMap();
	private Map compositeMap = new HashMap();

	private String pattern;

	private class Visitor implements IResourceVisitor {

		private LinkedList result = new LinkedList();

		private IProgressMonitor monitor;

		private String pattern;

		private IDataFilter filter;

		public Visitor(IProgressMonitor monitor, IContainer scope, Object source) {
			this.filter = getDataFilter(scope, source);
			this.monitor = monitor;
			this.pattern = getSearchPattern(scope, source);
		}

		public LinkedList getResult() {
			return result;
		}

		public boolean visit(IResource resource) throws CoreException {

			if (monitor.isCanceled()) {
				return false;
			}

			if (resource.getType() == IResource.PROJECT) {
				return true;
			}

			if (resource.getType() == IResource.FOLDER) {
				return true;
			}

			if (resource.getType() == IResource.FILE) {
				boolean checkExtension = checkExtension(resource);
				if (!checkExtension) {
					return false;
				}

				monitor.worked(1);
				monitor.setTaskName(resource.getName());
				CompositeMap bm;
				try {
					IDocument document = createDocument((IFile) resource);
					bm = CompositeMapUtil.loaderFromString(document.get());
					compositeMap.put(resource, bm);
					CompositeMapIteator finder = createIterationHandle();
					finder.setFilter(filter);
					bm.iterate(finder, true);
					List r = finder.getResult();

					List lines = buildMatchLines((IFile) resource, r, pattern);
					for (int i = 0; i < lines.size(); i++) {
						if (query != null) {
							ISearchResult searchResult = query
									.getSearchResult();
							if (searchResult instanceof AbstractSearchResult) {
								((AbstractSearchResult) searchResult)
										.addMatch((Match) lines.get(i));
							}
						} else {
							result.add(lines.get(i));
						}
					}
					monitor.worked(1);
				} catch (ApplicationException e) {
				}
				return false;
			}
			return false;
		}

		private boolean checkExtension(IResource resource) {
			IFile file = (IFile) resource;
			String fileExtension = file.getFileExtension();
			return "bm".equals(fileExtension) || "screen".equals(fileExtension)
					|| "svc".equals(fileExtension);
		}

	}

	private ISearchQuery query;

	public AbstractSearchService() {
	}

	public AbstractSearchService(ISearchQuery query) {
		this.query = query;
	}

	abstract protected CompositeMapIteator createIterationHandle();

	protected IDocument createDocument(IFile file) throws CoreException {
		FileEditorInput element = new FileEditorInput(file);
		XMLDocumentProvider provider = new XMLDocumentProvider();
		provider.connect(element);
		IDocument document = provider.getDocument(element);
		documentMap.put(file, document);
		return document;

	}

	private List buildMatchLines(IFile file, List r, String pattern)
			throws CoreException {
		List lines = new ArrayList();
		for (int i = 0; i < r.size(); i++) {
			MapFinderResult result = (MapFinderResult) r.get(i);
			CompositeMap map = result.getMap();
			Location location = map.getLocation();
			IDocument document = (IDocument) documentMap.get(file);
			int lineNo = location.getStartLine();

			LineElement l = null;

			try {
				IRegion lineInformation = document
						.getLineInformation(lineNo - 1);
				String lineContent = document.get(lineInformation.getOffset(),
						lineInformation.getLength());
				l = new LineElement(file, lineNo, lineInformation.getOffset(),
						lineContent);
				lines.addAll(createLineMatches(result, l, file, pattern));

			} catch (BadLocationException e1) {
				continue;
			}
		}

		return lines;
	}

	private List createLineMatches(MapFinderResult r, LineElement l,
			IFile file, String pattern) {
		FindReplaceDocumentAdapter dd = new FindReplaceDocumentAdapter(
				(IDocument) this.documentMap.get(file));

		int startOffset = l.getOffset();
		List matches = new ArrayList();
		List attributes = r.getAttributes();

		for (int i = 0; i < attributes.size(); i++) {
			try {
				Attribute att = (Attribute) attributes.get(i);
				String name = att.getName();
				IRegion nameRegion;
				nameRegion = dd.find(startOffset, name, true, true, false,
						false);
				if (nameRegion == null) {
					continue;
				}
				startOffset = nameRegion.getOffset();
				IRegion valueRegion = dd.find(startOffset, pattern, true, true,
						false, false);
				if (valueRegion == null) {
					continue;
				}
				startOffset = valueRegion.getOffset();
				ReferenceMatch match = new ReferenceMatch(file,
						valueRegion.getOffset(), valueRegion.getLength(), l);
				match.setMatchs(r);
				matches.add(match);
			} catch (BadLocationException e) {
				continue;
			}
		}
		return matches;
	}

	public List service(IContainer scope, Object source,
			IProgressMonitor monitor) {
		// searchPattern
		String pattern = getSearchPattern(scope, source);
		if (pattern == null)
			return null;
		monitor.beginTask("searching for " + pattern, 500);
		Visitor visitor = new Visitor(monitor, scope, source);
		try {
			scope.accept(visitor);
			monitor.done();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		LinkedList result = visitor.getResult();

		return result;
	}

	protected abstract IDataFilter getDataFilter(IContainer scope, Object source);

	public String getSearchPattern(IContainer scope, Object source) {
		return this.pattern == null ? createPattern(scope, source) : pattern;

	}

	protected abstract String createPattern(IContainer scope, Object source);

}
