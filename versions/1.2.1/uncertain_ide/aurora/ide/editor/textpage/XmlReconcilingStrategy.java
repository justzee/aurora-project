package aurora.ide.editor.textpage;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;

import aurora.ide.builder.IntimeBuilder;
import aurora.ide.editor.outline.OutlineReconcile;
import aurora.ide.helpers.DialogUtil;

public class XmlReconcilingStrategy implements IReconcilingStrategy,
		IReconcilingStrategyExtension {

	private ProjectionViewer mSourceViewer;
	private List listeners;

	public XmlReconcilingStrategy(ISourceViewer sourceViewer) {
		this.mSourceViewer = (ProjectionViewer) sourceViewer;
		listeners = new LinkedList();
		listeners.add(new XmlErrorReconcile(mSourceViewer));
		listeners.add(new ProjectionReconcile(mSourceViewer));
		listeners.add(new IntimeBuilder(mSourceViewer));
		listeners.add(new OutlineReconcile(mSourceViewer));
	}

	public void reconcile(IRegion partition) {
		noticeListener();
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		noticeListener();
	}

	public void setDocument(IDocument document) {
		// this.document = document;
		noticeListener();

	}

	private void noticeListener() {
		if (!listeners.isEmpty()) {
			for (Iterator it = listeners.listIterator(); it.hasNext();) {
				try {
					((IReconcileListener) it.next()).reconcile();
				} catch (Throwable e) {
					DialogUtil.showExceptionMessageBox(e);
				}
			}
		}
	}

	public void initialReconcile() {
		noticeListener();
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
	}
}
