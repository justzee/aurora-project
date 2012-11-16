package aurora.ide.bm.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;

import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;
import aurora.bm.BusinessModel;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.bm.ExtendModelFactory;
import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.ProjectionReconcile;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.XMLConfiguration;
import aurora.ide.editor.textpage.XmlErrorReconcile;
import aurora.ide.editor.textpage.XmlReconcilingStrategy;
import aurora.ide.search.cache.CacheManager;

public class ViewSource extends TextPage {

	public ViewSource(FormEditor editor) {
		super(editor);
		
		setSourceViewerConfiguration(new XMLConfiguration(new ColorManager()){
			public IReconciler getReconciler(ISourceViewer sourceViewer) {
				XmlReconcilingStrategy strategy = new XmlReconcilingStrategy(
						sourceViewer);
				strategy.addListener(new XmlErrorReconcile(sourceViewer));
				strategy.addListener(new ProjectionReconcile((ProjectionViewer) sourceViewer));
				MonoReconciler reconciler = new MonoReconciler(strategy, false);
				return reconciler;
			}
		});
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		this.getSourceViewer().getTextWidget()
				.addFocusListener(new FocusListener() {
					public void focusGained(FocusEvent arg0) {
						refresh();
					}

					public void focusLost(FocusEvent arg0) {
					}
				});
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void refresh(boolean bool) {
		setModify(false);
	}

	public void refresh() {
		this.getSourceViewer().getTextWidget().setText(getExtendContent());
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean isEditorInputReadOnly() {
		return true;
	}

	public String getExtendContent() {
		IFile file = (IFile) getEditorInput().getAdapter(IFile.class);
		try {

//			CompositeMap bm = AuroraResourceUtil.loadFromResource(file);
//			BusinessModel r = createResult(bm, file);
//			CompositeMap wholeBM2 = r.getObjectContext();
//			String xml2 = XMLOutputter.defaultInstance().toXML(
//					wholeBM2, true);
			
			CompositeMap wholeBMCompositeMap = CacheManager
					.getWholeBMCompositeMap(file);
			String xml1 = CommentXMLOutputter.defaultInstance().toXML(
					wholeBMCompositeMap, true);
			return xml1;
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	private BusinessModel createResult(CompositeMap config, IFile file) {
		ExtendModelFactory factory = new ExtendModelFactory(
				OCManager.getInstance(), file);
		return factory.getModel(config);
	}
}
