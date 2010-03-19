/*
 * Created on 2009-7-3
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ide.Activator;
import uncertain.ide.Common;
import uncertain.schema.Element;

public class TreeLabelProvider extends BaseLabelProvider implements
		ILabelProvider {

	/**
	 * @param schemaManager
	 */

	public TreeLabelProvider() {
		super();
	}

	public Image getImage(Object element) {
		String defaultPath = "icons/element_obj.gif";
		
		CompositeMap elemenntCm = (CompositeMap) element;
		Element ele = Common.getSchemaManager().getElement(elemenntCm);
		if (ele != null) {
			if (ele.isArray()) {
				return Activator.getImageDescriptor("icons/array.gif")
						.createImage();
				// return null;
			}
		}
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
		// elemenntCm.setNameSpaceURI(SchemaConstant.SCHEMA_NAMESPACE);

		String tagName = elemenntCm.getRawName();
		String elementName = getElementName(elemenntCm);
		if (elementName != null && !elementName.equals(""))
			elementText = elementName;
		else
			elementText = tagName;
		Element element = Common.getSchemaManager().getElement(elemenntCm);
		if (element != null) {
			if (element.isArray()){
				int nodes = elemenntCm.getChildsNotNull().size();
//				if(nodes>0){
					return "["+nodes+"]" + elementText;
//				}
//				return "[]" + elementText;
			}
				
		}

		return elementText;
	}

	private String getElementName(CompositeMap element) {

		String tagName = element.getRawName();
		Element elm = Common.getSchemaManager().getElement(
				element);
		String elemName = null;
		if(elm != null && !elm.isArray()){
			if(elm.getDisplayMask()==null){
				if(element.get("name") != null)
					elemName = element.get("name").toString();
				else{
					if(element.get("Name") != null)
						elemName = element.get("Name").toString();
				}
					
				if(elemName != null)
					tagName = tagName+" ("+elemName+")";
			}
			else{
//				System.out.println(element.toXML());
				tagName = tagName+" "+TextParser.parse(elm.getDisplayMask(), element);
			}
		}
//		System.out.println("tagName:"+tagName);
		
		Object elementNameObject = null;

//		if (tagNames.get(tagName) != null) {
//			String[] names = (String[]) tagNames.get(tagName);
//			for (int i = 0; i < names.length; i++) {
//				String name = names[i];
//				if (element.get(name) != null) {
//					elementNameObject = element.get(name);
//					break;
//				}
//			}
//
//		} else {
//			elementNameObject = element.get("Name");
//		}
		if (elementNameObject == null || elementNameObject.equals(""))
			return tagName;
		return elementNameObject.toString();
	}
}
