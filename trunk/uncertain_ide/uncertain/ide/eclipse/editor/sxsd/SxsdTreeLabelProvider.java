/*
 * Created on 2009-7-3
 */
package uncertain.ide.eclipse.editor.sxsd;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import aurora_ide.Activator;

import uncertain.composite.CompositeMap;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import uncertain.schema.SchemaConstant;

public class SxsdTreeLabelProvider extends BaseLabelProvider implements
		ILabelProvider {

	/**
	 * @param schemaManager
	 */
	public SxsdTreeLabelProvider() {
		super();
	}

	public Image getImage(Object element) {
    	String imagePath ="icons/element_obj.gif";
    	
		CompositeMap elemenntCm = (CompositeMap) element;
		elemenntCm.setNameSpaceURI(SchemaConstant.SCHEMA_NAMESPACE);

		Element ele = Activator.getSchemaManager().getElement(elemenntCm);
		if (ele != null) {
			if (ele.isArray()){
				return Activator.getImageDescriptor("icons/array.gif"
				).createImage();
//				return null;
			}
		}
        return Activator.getImageDescriptor(imagePath).createImage();
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

		CompositeMap elemenntCm = (CompositeMap) obj;
		elemenntCm.setNameSpaceURI(SxsdPage.namespaceUrl);
		String raw_name = elemenntCm.getRawName();
		Element element = Activator.getSchemaManager().getElement(elemenntCm);
		if (element != null) {
			if (element.isArray())
				return "[]" + raw_name;
		}

		return raw_name;
	}
}
