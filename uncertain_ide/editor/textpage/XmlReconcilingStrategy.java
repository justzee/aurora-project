package editor.textpage;

import helpers.ApplicationException;
import helpers.CompositeMapUtil;
import helpers.SystemException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;

import uncertain.composite.CompositeMap;

public class XmlReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

	private IDocument document;
	/** holds the calculated positions */
	private Annotation[] oldAnnotations;
	private ProjectionAnnotationModel annotationModel;
	private ProjectionViewer sourceViewer;
	private List oldPositions;
	public XmlReconcilingStrategy(ISourceViewer sourceViewer) {
		this.sourceViewer = (ProjectionViewer) sourceViewer;
	}

	public void reconcile(IRegion partition) {
		updateAnnotations();
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		updateAnnotations();
	}

	public void setDocument(IDocument document) {
		this.document = document;
		updateAnnotations();

	}
	private void updateAnnotations() {
		annotationModel = sourceViewer.getProjectionAnnotationModel();
		if (annotationModel == null)
			return;
		List positions = new LinkedList();
		try {
			positions = calculatePositions();
		} catch (ApplicationException e) {
			updateFoldingStructure(null);
		}
		if(!isListEqual(positions,oldPositions)){
			updateFoldingStructure(positions);
			oldPositions = positions;
		}
	}
	private boolean isListEqual(List one,List two){
		if(one == null && two == null)
			return true;
		if(one != null && one.equals(two))
			return true;
		return false;
	}

	protected List calculatePositions() throws ApplicationException {
		List positions = new LinkedList();
		String content = document.get();
		CompositeMap root = CompositeMapUtil.loaderFromString(content);
		if (root == null) {
			return null;
		}
		try {
			if (root.getLocation().getEndLine()> root.getLocation().getStartLine()) {
				positions.add(createPosition(root.getLocation().getRange()));
			}
			iteratorNodes(root, positions);
		} catch (BadLocationException e) {
			throw new SystemException(e);
		}
		return positions;
	}
	private void iteratorNodes(CompositeMap node, List positions) throws BadLocationException {
		if (node == null || node.getChildIterator() == null)
			return;
		for (Iterator it = node.getChildIterator(); it.hasNext();) {
			CompositeMap child = (CompositeMap) it.next();
			if (child.getLocation().getEndLine() - child.getLocation().getStartLine()>4) {
				positions.add(createPosition(child.getLocation().getRange()));
			}
			iteratorNodes(child, positions);
		}
	}
	private Position createPosition(int[] location) throws BadLocationException {
		int startLine = location[0] - 1;
		int startColumn = location[1];
		int endLine = location[2] - 1;
		int endColumn = location[3];
		int beginOffset = document.getLineOffset(startLine) + startColumn;
		int endOffset = document.getLineOffset(endLine) + endColumn;
		return new Position(beginOffset, endOffset - beginOffset);
	}
	public void updateFoldingStructure(List positions) {
		if(annotationModel == null)
			return;
		if (positions == null) {
			return;
		}
		Annotation[] annotations = new Annotation[positions.size()];
		// this will hold the new annotations along
		// with their corresponding positions
		HashMap newAnnotations = new HashMap();
		for (int i = 0; i < positions.size(); i++) {
			ProjectionAnnotation annotation = new ProjectionAnnotation();
			newAnnotations.put(annotation, positions.get(i));
			annotations[i] = annotation;
		}
		annotationModel.modifyAnnotations(oldAnnotations, newAnnotations, null);
		oldAnnotations = annotations;
	}

	public void initialReconcile() {
		updateAnnotations();
	}
	public void setProgressMonitor(IProgressMonitor monitor) {
	}

}
