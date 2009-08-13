/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;

public class CompositeListLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

    /**
     * @param attribArray
     */
    public CompositeListLabelProvider(Object[] attribArray) {
        super();
        this.mAttribArray = attribArray;
    }

    Object[]     mAttribArray;

    public Image getImage(Object element) {
        return null;
    }

    public String getText(Object element) {
         return element.toString();
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
    
    public Image getColumnImage(Object element, int columnIndex){
        return null;
    }
    
    public String getColumnText(Object element, int columnIndex){
        CompositeMap data = (CompositeMap)element;
        Attribute attrib = (Attribute)mAttribArray[columnIndex];
        return data.getString(attrib.getName());
    }


}
