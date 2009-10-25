package uncertain.ide.eclipse.action;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import uncertain.composite.CompositeMap;
import uncertain.schema.Element;
import aurora_ide.Activator;

public class ContextAddElementMenuManager extends MenuManager {
	private IViewerDirty viewerDirty;
	String _prefix;
	String _uri;

	public ContextAddElementMenuManager(IViewerDirty dirtyObject, String prefix, String uri, String label) {
		super(label);
		viewerDirty = dirtyObject;
		_prefix = prefix;
		_uri = uri;
		createActions();
	}

	private void createActions() {
//		ISelection selection = mColumnViewer.getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		final CompositeMap comp = (CompositeMap) obj;
		
		final CompositeMap comp = viewerDirty.getFocusData();

		Element element = Activator.getSchemaManager().getElement(comp);
		if (element == null) {
			return;
		}

		if (element.isArray()) {
			final String elementType = element.getElementType().getQName()
					.getLocalName();

			this.add(new AddElementAction(viewerDirty, comp,
					_prefix, _uri, elementType));

		} else if (element != null) {
			List arrays = element.getAllElements();
			if (arrays != null) {
				Iterator ite = arrays.iterator();
				while (ite.hasNext()) {
					final Element ele = (Element) ite.next();
					this.add(new AddElementAction(viewerDirty,
							comp, _prefix, _uri, ele.getLocalName()));
				}
			}
		}
	}
}
