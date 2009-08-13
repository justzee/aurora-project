/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.schema.Attribute;
import uncertain.schema.editor.AttributeValue;

public class PropertySheetLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

    /*    
    public static final int TYPE_ATTRIBUTE = 0;
    public static final int TYPE_VALUE = 1;
    
    int     mType;

    public PropertySheetLabelProvider( int type ){
        super();
        mType = type;
    }
 */ 

    public Image getImage(Object element) {
        return null;
    }

    public String getText(Object element) {
        if( element instanceof Attribute )
            return ((Attribute)element).getLocalName();
        else
            return element.toString();
    }

    public boolean isLabelProperty(Object element, String property) {
        //return PropertySheetEditor.COLUMN_VALUE.equals(property);
        return false;
    }
    
    public Image getColumnImage(Object element, int columnIndex){
        return null;
    }
    
    public String getColumnText(Object element, int columnIndex){
        AttributeValue av = (AttributeValue)element;
        if( columnIndex == 0 )
            return av.getAttribute().getName();
        else if( columnIndex == 1 ){
            return av.getValueString();
        }
        else
            return "";
    }


}
