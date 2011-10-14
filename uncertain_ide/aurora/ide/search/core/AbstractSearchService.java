package aurora.ide.search.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.util.resource.Location;
import a.d;
import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.reference.IDataFilter;
import aurora.ide.search.reference.MapFinderResult;
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
	public final static QualifiedName urlReference = new QualifiedName(
			"http://www.aurora-framework.org/application", "screenBm");

	private Map compositeMap = new HashMap();
	private Map exceptionMap = new HashMap();

	private boolean runInUI = false;
	private Object pattern;

	public boolean isPostException() {
		return isPostException;
	}

	public void setPostException(boolean isPostException) {
		this.isPostException = isPostException;
	}

	private boolean isPostException = true;

	private class ScopeVisitor implements IResourceVisitor {
		private List result = new ArrayList();

		public boolean visit(IResource resource) throws CoreException {
			if (resource.getType() == IResource.FILE) {
				boolean checkExtension = checkExtension(resource);
				if (checkExtension) {
					result.add(resource);
				}
				return false;
			}
			return true;
		}

		public List getResult() {
			return result;
		}

		private boolean checkExtension(IResource resource) {
			IFile file = (IFile) resource;
			String fileExtension = file.getFileExtension();
			return "bm".equalsIgnoreCase(fileExtension)
					|| "screen".equalsIgnoreCase(fileExtension)
					|| "svc".equalsIgnoreCase(fileExtension);
		}
	}

	private ISearchQuery query;
	private Object source;
	private IResource[] roots;
	private IFile fCurrentFile;
	private int fNumberOfScannedFiles;
	private int fNumberOfFilesToScan;

	public AbstractSearchService(IResource[] roots, Object source) {
		this.roots = roots;
		this.source = source;
	}

	public AbstractSearchService(IResource[] roots, Object source,
			ISearchQuery query) {
		this(roots, source);
		this.query = query;
	}

	abstract protected CompositeMapIteator createIterationHandle(IFile resource);

	protected List buildMatchLines(IFile file, List r, Object pattern)
			throws CoreException {

		List lines = new ArrayList();
		for (int i = 0; i < r.size(); i++) {
			MapFinderResult result = (MapFinderResult) r.get(i);
			CompositeMap map = result.getMap();
			Location location = map.getLocation();
			IDocument document = getDocument(file);
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
		finder.setFilter(getDataFilter(roots, source));
		bm = getCompositeMap((IFile) resource);
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

	protected List createLineMatches(MapFinderResult r, LineElement l,
			IFile file, Object pattern) throws CoreException {

		IDocument document = (IDocument) getDocument(file);
		FindReplaceDocumentAdapter dd = new FindReplaceDocumentAdapter(
				(IDocument) getDocument(file));

		List matches = new ArrayList();
		List attributes = r.getAttributes();

		for (int i = 0; i < attributes.size(); i++) {
			try {
				int startOffset = l.getOffset();
				Attribute att = (Attribute) attributes.get(i);
				String name = att.getName();
				IRegion nameRegion = getAttributeRegion(startOffset,
						l.getLength(), name, document);

				if (nameRegion == null) {
					continue;
				}
				startOffset = nameRegion.getOffset();
				IRegion valueRegion = dd.find(startOffset, pattern.toString(),
						true, true, true, false);
				if (valueRegion == null) {
					continue;
				}
				startOffset = valueRegion.getOffset();
				AuroraMatch match = new AuroraMatch(file,
						valueRegion.getOffset(), valueRegion.getLength(), l);
				match.setMatchs(r);
				matches.add(match);
			} catch (BadLocationException e) {
				continue;
			}
		}

		return matches;
	}

	protected IRegion getAttributeRegion(int offset, int length, String name,
			IDocument document) throws BadLocationException {

		IRegion attributeRegion = Util.getAttributeRegion(offset, length, name,
				document);

		return attributeRegion;
	}

	// protected List createLineMatches(MapFinderResult r, LineElement l,
	// IFile file, Object pattern) throws CoreException {
	// FindReplaceDocumentAdapter dd = new FindReplaceDocumentAdapter(
	// (IDocument) getDocument(file));
	//
	// int startOffset = l.getOffset();
	// List matches = new ArrayList();
	// List attributes = r.getAttributes();
	//
	// for (int i = 0; i < attributes.size(); i++) {
	// try {
	// Attribute att = (Attribute) attributes.get(i);
	// String name = att.getName();
	// IRegion nameRegion;
	// nameRegion = dd
	// .find(startOffset, name, true, true, true, false);
	// if (nameRegion == null) {
	// continue;
	// }
	// startOffset = nameRegion.getOffset();
	// IRegion valueRegion = dd.find(startOffset, pattern.toString(),
	// true, true, true, false);
	// if (valueRegion == null) {
	// continue;
	// }
	// startOffset = valueRegion.getOffset();
	// ReferenceMatch match = new ReferenceMatch(file, valueRegion
	// .getOffset(), valueRegion.getLength(), l);
	// match.setMatchs(r);
	// matches.add(match);
	// } catch (BadLocationException e) {
	// continue;
	// }
	// }
	// return matches;
	// }

	public List service(final IProgressMonitor monitor) {

		List files = findFilesInScopes(roots);
		fNumberOfFilesToScan = files.size();
		Job monitorUpdateJob = new Job("Aurora Search progress") {
			private int fLastNumberOfScannedFiles = 0;

			public IStatus run(final IProgressMonitor inner) {
				while (!inner.isCanceled()) {
					final IFile file = fCurrentFile;
					if (file != null) {
						if (isRunInUI()) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									updateMonitor(monitor, file);
								}
							});
						} else {
							updateMonitor(monitor, file);
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						return Status.OK_STATUS;
					}
				}
				return Status.OK_STATUS;

			}

			private void updateMonitor(final IProgressMonitor monitor,
					final IFile file) {
				String fileName = file.getName();
				final Object[] args = { fileName,
						new Integer(fNumberOfScannedFiles),
						new Integer(fNumberOfFilesToScan) };
				monitor.subTask(MessageFormater.format(Message._scanning, args));
				int steps = fNumberOfScannedFiles - fLastNumberOfScannedFiles;
				monitor.worked(steps);
				fLastNumberOfScannedFiles += steps;
			}

		};

		// searchPattern
		pattern = getSearchPattern(roots, source);

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
					try {
						result.addAll(processFile(fCurrentFile));
					} catch (CoreException e) {
					} catch (ApplicationException e) {
					} catch (Exception e) {
						if (!(e instanceof IllegalArgumentException)) {
//							e.printStackTrace();
						}

						handleException(fCurrentFile, e);
					}
				}
			}
		} finally {
			monitorUpdateJob.cancel();
			monitor.done();
			if (isPostException)
				postException();
		}

		return result;
	}

	private static Shell getShell() {
		// index : 0 must the active window.
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchWindow windowToParentOn = activeWindow == null ? (workbench
				.getWorkbenchWindowCount() > 0 ? workbench
				.getWorkbenchWindows()[0] : null) : activeWindow;
		return windowToParentOn == null ? null : activeWindow.getShell();

	}

	private static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}

	private void postException() {
		if (exceptionMap.size() != 0) {
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					MultiStatus status = new MultiStatus(
							AuroraPlugin.PLUGIN_ID, IStatus.ERROR,
							getStatusChildren(), "文件解析异常", null);
					ErrorDialog.openError(getShell(), null, null, status);
				}
			});

		}

	}

	public IStatus[] getStatusChildren() {
		IStatus[] children = new IStatus[exceptionMap.size()];
		Set keySet = exceptionMap.keySet();
		int i = 0;
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
			IFile o = (IFile) iterator.next();
			children[i] = new Status(IStatus.ERROR, AuroraPlugin.PLUGIN_ID, o
					.getFullPath().toString(), (Throwable) exceptionMap.get(o));
			i++;
		}
		return children;
	}

	private void handleException(IFile file, Exception e) {
		exceptionMap.put(file, e);
	}

	private List findFilesInScopes(IResource[] roots) {
		List result = new ArrayList();
		if (roots != null) {
			for (int i = 0; i < roots.length; i++) {
				List _result = findFilesInScope(roots[i]);
				merge(result, _result);
			}
		}

		return result;
	}

	private void merge(List to, List from) {
		if (from == null)
			return;
		for (int i = 0; i < from.size(); i++) {
			if (to.contains(from.get(i))) {
				continue;
			}
			to.add(from.get(i));
		}
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

	protected abstract IDataFilter getDataFilter(IResource[] roots,
			Object source);

	public Object getSearchPattern(IResource[] roots, Object source) {
		return this.pattern == null ? createPattern(roots, source) : pattern;

	}

	public ISearchQuery getQuery() {
		return query;
	}

	public Object getSource() {
		return source;
	}

	public IResource[] getRoots() {
		return roots;
	}

	public CompositeMap getCompositeMap(IFile file) throws CoreException,
			ApplicationException {
		return CacheManager.getCompositeMapCacher().getCompositeMap(file);
	}

	public IFile getFile(CompositeMap map) {
		return (IFile) this.compositeMap.get(map);
	}

	public IDocument getDocument(IFile file) throws CoreException {
		return CacheManager.getDocumentCacher().getDocument(file);
	}

	protected abstract Object createPattern(IResource[] roots, Object source);

	public boolean isRunInUI() {
		return runInUI;
	}

	public void setRunInUI(boolean runInUI) {
		this.runInUI = runInUI;
	}

}
