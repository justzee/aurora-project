package uncertain.ide.eclipse.action;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.MenuManager;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.schema.Element;

public class ContextAddElementMenuManager extends MenuManager {
	private IViewerDirty viewer;
	String prefix;
	String uri;

	public ContextAddElementMenuManager(IViewerDirty viewer, String prefix, String uri, String label) {
		super(label);
		this.viewer = viewer;
		this.prefix = prefix;
		this.uri = uri;
		createActions();
	}

	private void createActions() {
//		ISelection selection = mColumnViewer.getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		final CompositeMap comp = (CompositeMap) obj;
		
		final CompositeMap comp = viewer.getFocusData();

		Element element = Activator.getSchemaManager().getElement(comp);
		if (element == null) {
			return;
		}

		if (element.isArray()) {
			final QualifiedName qName = element.getElementType().getQName();
			final String elementType = element.getElementType().getQName()
					.getLocalName();
//			AddElementAction addAtion = new AddElementAction(viewer, comp,
//					prefix, uri, elementType,AddElementAction.getDefaultImageDescriptor(),elementType);
			AddElementAction addAtion = new AddElementAction(viewer, comp,
					qName,AddElementAction.getDefaultImageDescriptor(),elementType);
			this.add(addAtion);

		} else if (element != null) {
			List arrays = element.getAllElements();
			if (arrays != null) {
				Iterator ite = arrays.iterator();
				while (ite.hasNext()) {
					final Element ele = (Element) ite.next();
					final QualifiedName qName = element.getQName();
//					this.add(new AddElementAction(viewer,
//							comp, prefix, uri, ele.getLocalName(),AddElementAction.getDefaultImageDescriptor(),ele.getLocalName()));
					this.add(new AddElementAction(viewer,
							comp, qName,AddElementAction.getDefaultImageDescriptor(),ele.getLocalName()));

				}
			}
		}
	}
}
