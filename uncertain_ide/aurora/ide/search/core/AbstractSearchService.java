package aurora.ide.search.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import aurora.ide.editor.textpage.XMLDocumentProvider;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.search.reference.IDataFilter;
import aurora.ide.search.reference.MapFinderResult;
import aurora.ide.search.reference.ReferenceMatch;
import aurora.ide.search.ui.LineElement;
import aurora.ide.search.ui.MessageFormater;


abstract public class AbstractSearchService implements ISearchService {
	public final static QualifiedName bmReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "model");
	public final static QualifiedName screenReference = new QualifiedName(
			"http://www.aurora-framework.org/application", "screen");
	public final static QualifiedName localFieldReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "localField");
	public final static QualifiedName foreignFieldReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "foreignField");
	public final static QualifiedName datasetReference = new QualifiedName(
			"http://www.aurora-framework.org/application", "dataset");
	private Map documentMap = new HashMap();
	private Map compositeMap = new HashMap();

	private Object pattern;

	private class ScopeVisitor implements IResourceVisitor {
		private List result = new ArrayList();

		public boolean visit(IResource resource) throws CoreException {
			if (resource.getType() == IResource.PROJECT) {
				return true;
			}

			if (resource.getType() == IResource.FOLDER) {
				return true;
			}

			if (resource.getType() == IResource.FILE) {
				boolean checkExtension = checkExtension(resource);
				if (checkExtension) {
					result.add(resource);
				}
				return false;
			}
			return false;
		}

		public List getResult() {
			return result;
		}

		private boolean checkExtension(IResource resource) {
			IFile file = (IFile) resource;
			String fileExtension = file.getFileExtension();
			return "bm".equals(fileExtension) || "screen".equals(fileExtension)
					|| "svc".equals(fileExtension);
		}
	}

	private ISearchQuery query;
	private Object source;
	private IResource scope;
	private IFile fCurrentFile;
	private int fNumberOfScannedFiles;
	private int fNumberOfFilesToScan;

	public AbstractSearchService(IResource scope, Object source) {
		this.scope = scope;
		this.source = source;
	}

	public AbstractSearchService(IResource scope, Object source,
			ISearchQuery query) {
		this(scope, source);
		this.query = query;
	}

	abstract protected CompositeMapIteator createIterationHandle(IFile resource);

	protected IDocument createDocument(IFile file) throws CoreException {
		FileEditorInput element = new FileEditorInput(file);
		XMLDocumentProvider provider = new XMLDocumentProvider();
		provider.connect(element);
		IDocument document = provider.getDocument(element);
		documentMap.put(file, document);
		return document;

	}

	private List buildMatchLines(IFile file, List r, Object pattern)
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

	private List processFile(IResource resource) throws CoreException,
			ApplicationException {

		List result = new ArrayList();
		CompositeMap bm;
		fNumberOfScannedFiles++;
		CompositeMapIteator finder = createIterationHandle((IFile) resource);
		finder.setFilter(getDataFilter(scope, source));

		IDocument document = createDocument((IFile) resource);
		bm = CompositeMapUtil.loaderFromString(document.get());
		compositeMap.put(bm, resource);
		bm.iterate(finder, true);

		List r = finder.getResult();
		List lines = buildMatchLines((IFile) resource, r, pattern);
		for (int i = 0; i < lines.size(); i++) {
			if (query != null) {
				ISearchResult searchResult = query.getSearchResult();
				if (searchResult instanceof AbstractSearchResult) {
					((AbstractSearchResult) searchResult)
							.addMatch((Match) lines.get(i));
				}
			} else {
				result.add(lines.get(i));
			}
		}

		return result;
	}

	private List createLineMatches(MapFinderResult r, LineElement l,
			IFile file, Object pattern) {
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
				IRegion valueRegion = dd.find(startOffset, pattern.toString(),
						true, true, false, false);
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

	public List service(final IProgressMonitor monitor) {
		List files = findFilesInScope(scope);
		fNumberOfFilesToScan = files.size();
		Job monitorUpdateJob = new Job("Aurora Search progress") {
			private int fLastNumberOfScannedFiles = 0;

			public IStatus run(IProgressMonitor inner) {
				while (!inner.isCanceled()) {
					IFile file = fCurrentFile;
					if (file != null) {
						String fileName = file.getName();
						Object[] args = { fileName,
								new Integer(fNumberOfScannedFiles),
								new Integer(fNumberOfFilesToScan) };
						monitor.subTask(MessageFormater.format(
								Message._scanning, args));
						int steps = fNumberOfScannedFiles
								- fLastNumberOfScannedFiles;
						monitor.worked(steps);
						fLastNumberOfScannedFiles += steps;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						return Status.OK_STATUS;
					}
				}
				return Status.OK_STATUS;
			}
		};

		// searchPattern
		pattern = getSearchPattern(scope, source);

		monitor.beginTask("Searching for " + pattern.toString(), files.size());
		monitorUpdateJob.setSystem(true);
		monitorUpdateJob.schedule();
		List result = new ArrayList();
		try {
			if (files != null) {
				for (int i = 0; i < files.size(); i++) {
					if (monitor.isCanceled())
						return result;
					fCurrentFile = (IFile) files.get(i);
					result.addAll(processFile(fCurrentFile));
				}
			}
		} catch (CoreException e) {

		} catch (ApplicationException e) {

		} finally {
			monitorUpdateJob.cancel();
			monitor.done();
		}

		return result;
	}

	private List findFilesInScope(IResource scope) {
		ScopeVisitor visitor = new ScopeVisitor();
		try {
			scope.accept(visitor);
			return visitor.getResult();
		} catch (CoreException e) {
		}
		return null;
	}

	protected abstract IDataFilter getDataFilter(IResource scope, Object source);

	public Object getSearchPattern(IResource scope, Object source) {
		return this.pattern == null ? createPattern(scope, source) : pattern;

	}

	public ISearchQuery getQuery() {
		return query;
	}

	public Object getSource() {
		return source;
	}

	public IResource getScope() {
		return scope;
	}

	public IFile getFile(CompositeMap map) {
		return (IFile) this.compositeMap.get(map);
	}

	public IDocument getDocument(IFile file) {
		return (IDocument) this.documentMap.get(file);
	}

	protected abstract Object createPattern(IResource scope, Object source);

}
