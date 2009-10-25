/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import aurora_ide.Activator;

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
    	String imagePath="icons/attribute_obj.gif";
    	if(columnIndex == 0){
    		if(element instanceof CategoryLabel){
    			return Activator.getImageDescriptor("icons/category.gif").createImage();
    		}
    		return Activator.getImageDescriptor(imagePath).createImage();
    	}
    	return null;
    }
    
    public String getColumnText(Object element, int columnIndex){

    	if(! (element instanceof AttributeValue)){
    		return element.toString();
    	}


        AttributeValue av = (AttributeValue)element;
    	if(element instanceof CategoryLabel){
    		if( columnIndex == 0 )
                return av.getValueString();
            else if( columnIndex == 1 ){
                return "";
            }
//    		return av.getValueString();
    	}
        if(av.getAttribute()==null)
        	 return av.getValueString();
        

        
        if( columnIndex == 0 ){
            Attribute attr = av.getAttribute();
            String text = attr.getName();
            if(attr.getUse()!=null&&attr.getUse().equals("required"))
            	text = " * "+text;
            return text;
        }
        else if( columnIndex == 1 ){
            return av.getValueString();
        }
        else
            return "";
    }


}
