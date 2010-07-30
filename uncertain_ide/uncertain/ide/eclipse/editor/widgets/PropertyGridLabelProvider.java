/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.celleditor.ICellEditor;
import uncertain.schema.Attribute;

public class PropertyGridLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

    /**
     * @param attribArray
     */
	int nodeIndex;
	int nodeCount;
	Object[]     mAttribArray;
	private PropertyGridCellModifier mCellModifier;
	
	public PropertyGridLabelProvider(Object[] attribArray,PropertyGridCellModifier cellModifier) {
		super();
		this.mAttribArray = attribArray;
		this.mCellModifier = cellModifier;
	}

    

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
    	
    	if(columnIndex == 0){
    		return null;
    	}
        if(mAttribArray==null || mAttribArray.length==0)
        	return null;
        Attribute attrib = (Attribute)mAttribArray[columnIndex-1];
        
        Object cellEditor_object = mCellModifier.getCellEditor(attrib.getLocalName());
        
		if(cellEditor_object != null && cellEditor_object instanceof CheckboxCellEditor){
			CompositeMap data = (CompositeMap)element;
			String returnValue = data.getString(attrib.getLocalName());
//			ICellEditor cellEditor= (ICellEditor)cellEditor_object;
//			String value = cellEditor.getSelection();
//			if(value != null && value.equals("true"))
			if(returnValue != null && returnValue.equals("true"))	
				return Activator.getImageDescriptor("icons/checked.gif").createImage();
			return Activator.getImageDescriptor("icons/unchecked.gif").createImage();
		}

        return null;
    }
    
    public String getColumnText(Object element, int columnIndex){
        CompositeMap data = (CompositeMap)element;
    	int nowCount = data.getParent().getChildsNotNull().size();
    	//if nodes has changed,reset the nodeIndex;
    	if(nowCount!=nodeCount){
    		nodeCount = nowCount;
    		nodeIndex = 0;
    	}
    	//the first column is sequence.
    	if(columnIndex == 0){
    		return String.valueOf(++nodeIndex);
    	}
    	
        if(mAttribArray==null || mAttribArray.length==0)
        	return null;
        Attribute attrib = (Attribute)mAttribArray[columnIndex-1];
        
        Object cellEditor_object = mCellModifier.getCellEditor(attrib.getLocalName());
        String returnValue = data.getString(attrib.getLocalName());
        
		if(cellEditor_object != null){
			ICellEditor cellEditor= (ICellEditor)cellEditor_object;
			if(returnValue !=null)
				cellEditor.SetSelection(returnValue);
		}
        return returnValue;
    }
    public void refresh(){
    	nodeIndex = 0;
    }
    

}
