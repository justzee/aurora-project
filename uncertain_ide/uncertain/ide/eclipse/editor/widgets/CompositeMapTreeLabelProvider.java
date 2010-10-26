package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ide.Activator;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.LocaleMessage;
import uncertain.schema.Element;

public class CompositeMapTreeLabelProvider extends BaseLabelProvider implements
		ILabelProvider {

	public CompositeMapTreeLabelProvider() {
		super();
	}

	public Image getImage(Object element) {
		CompositeMap elemenntCm = (CompositeMap) element;
		Element ele = LoadSchemaManager.getSchemaManager().getElement(
				elemenntCm);
		if (ele != null) {
			if (ele.isArray()) {
				return Activator.getImageDescriptor(
						LocaleMessage.getString("array.icon")).createImage();
			}
		}
		String defaultPath = LocaleMessage.getString("element.icon");
		return Activator.getImageDescriptor(defaultPath).createImage();
	}

	/**
	 * Returns the text for the label of the given element.
	 * 
	 * @param obj
	 *            the element for which to provide the label text
	 * @return the text string used to label the element, or <code>null</code>
	 *         if there is no text label for the given object
	 */
	public String getText(Object obj) {
		String elementText = null;
		CompositeMap elemenntCm = (CompositeMap) obj;

		String tagName = elemenntCm.getRawName();
		String elementName = getElementName(elemenntCm);
		if (elementName != null && !elementName.equals(""))
			elementText = elementName;
		else
			elementText = tagName;
		Element element = LoadSchemaManager.getSchemaManager().getElement(
				elemenntCm);
		if (element != null) {
			if (element.isArray()) {
				int nodes = elemenntCm.getChildsNotNull().size();
				return "[" + nodes + "]" + elementText;
			}

		}

		return elementText;
	}

	private String getElementName(CompositeMap element) {

		String tagName = element.getRawName();
		Element elm = LoadSchemaManager.getSchemaManager().getElement(element);
		String elemDesc = null;
		if (elm != null && !elm.isArray()) {
			if (elm.getDisplayMask() == null) {
				elemDesc = TextParser.parse(elm.getDisplayMask(), element);
			}
			if (elemDesc != null)
				tagName = tagName + " " + elemDesc;
		}
		if (elemDesc == null) {
			if (element.get("id") != null) {
				elemDesc = element.getString("id");
			} else if (element.get("name") != null)
				elemDesc = element.get("name").toString();
			else if (element.get("Name") != null)
					elemDesc = element.get("Name").toString();
			if (elemDesc != null)
				tagName = tagName + " (" + elemDesc + ")";
		}

		return tagName;
	}
}
