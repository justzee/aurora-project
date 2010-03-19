package uncertain.ide.eclipse.action;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.MenuManager;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Common;
import uncertain.schema.Element;

public class ContextAddElementMenuManager extends MenuManager {
	private IViewerDirty viewer;
	String prefix;
	String uri;

	public ContextAddElementMenuManager(IViewerDirty viewer, String prefix,
			String uri, String label) {
		super(label);
		this.viewer = viewer;
		this.prefix = prefix;
		this.uri = uri;
		createActions();
	}

	private void createActions() {
		final CompositeMap comp = viewer.getFocusData();
		Element element = Common.getSchemaManager().getElement(comp);
		if (element == null) {
			return;
		}
		List sonElements = CompositeMapAction.getAvailableSonElements(element, comp);
		if (sonElements != null) {
			Iterator ite = sonElements.iterator();
			while (ite.hasNext()) {
				final Element ele = (Element) ite.next();
				final QualifiedName qName = ele.getQName();
				String text = Common.getElementFullName(comp, qName);
				this.add(new AddElementAction(viewer, comp, ele.getQName(),
						AddElementAction.getDefaultImageDescriptor(), text));

			}
		}
	}
}
