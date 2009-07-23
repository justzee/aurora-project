/*
 * Created on 2009-7-3
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;

public class CompositeMapLabelProvider extends BaseLabelProvider implements ILabelProvider {

    public Image getImage(Object element){
        return null;
    }

    /**
     * Returns the text for the label of the given element.
     *
     * @param element the element for which to provide the label text
     * @return the text string used to label the element, or <code>null</code>
     *   if there is no text label for the given object
     */
    public String getText(Object element){
        CompositeMap map = (CompositeMap)element;
        return map.getRawName();
    }
}
