/*
 * Created on 2009-7-3
 */
package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ide.Activator;
import uncertain.ide.Common;
import uncertain.schema.Element;

public class CompositeMapTreeLabelProvider extends BaseLabelProvider implements
		ILabelProvider {

	public CompositeMapTreeLabelProvider() {
		super();
	}

	public Image getImage(Object element) {
		CompositeMap elemenntCm = (CompositeMap) element;
		Element ele = Common.getSchemaManager().getElement(elemenntCm);
		if (ele != null) {
			if (ele.isArray()) {
				return Activator.getImageDescriptor(
						Common.getString("array.icon")).createImage();
			}
		}
		String defaultPath = Common.getString("element.icon");
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
		Element element = Common.getSchemaManager().getElement(elemenntCm);
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
		Element elm = Common.getSchemaManager().getElement(element);
		String elemName = null;
		if (elm != null && !elm.isArray()) {
			if (elm.getDisplayMask() == null) {
				if (element.get("name") != null)
					elemName = element.get("name").toString();
				else {
					if (element.get("Name") != null)
						elemName = element.get("Name").toString();
				}

				if (elemName != null)
					tagName = tagName + " (" + elemName + ")";
			} else {
				tagName = tagName + " "
						+ TextParser.parse(elm.getDisplayMask(), element);
			}
		}

		Object elementNameObject = null;
		if (elementNameObject == null || elementNameObject.equals(""))
			return tagName;
		return elementNameObject.toString();
	}
}
