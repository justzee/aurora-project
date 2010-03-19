/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;

public class PropertyArrayLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

    /**
     * @param attribArray
     */
	int nodeIndex;
	int nodeCount;
    public PropertyArrayLabelProvider(Object[] attribArray) {
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
    	int nowCount = data.getParent().getChildsNotNull().size();
    	//if nodes has changed,reset the nodeIndex;
    	if(nowCount!=nodeCount){
//    		System.out.println("nodeCount:"+nodeCount+"nowCount:"+nowCount);
    		nodeCount = nowCount;
    		nodeIndex = 0;
    	}
    	//the first column is sequence.
    	if(columnIndex == 0){
//    		System.out.println("nodeIndex:"+nodeIndex);
    		return String.valueOf(++nodeIndex);
    	}
    	
        if(mAttribArray==null || mAttribArray.length==0)
        	return null;
        Attribute attrib = (Attribute)mAttribArray[columnIndex-1];
        return data.getString(attrib.getName());
    }
    public void refresh(){
    	nodeIndex = 0;
    }
    

}
