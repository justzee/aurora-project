/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.celleditor.ICellEditor;
import uncertain.ide.eclipse.editor.widgets.core.IGridLabelProvider;
import uncertain.ide.eclipse.editor.widgets.core.IGridViewer;
import uncertain.ide.help.LocaleMessage;

public class PropertyGridLabelProvider extends BaseLabelProvider implements IGridLabelProvider {

    /**
     * @param attribArray
     */
	int nodeIndex;
	int nodeCount;
	String[]     gridPropties;
	private GridViewer viewer;
	
	public PropertyGridLabelProvider(String[] gridPropties,GridViewer viewer) {
		super();
		this.gridPropties = gridPropties;
		this.viewer = viewer;
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
        if(gridPropties==null || gridPropties.length==0)
        	return null;
        
        ICellEditor cellEditor = viewer.getCellEditor(gridPropties[columnIndex-1]);
        
		if(cellEditor != null && cellEditor instanceof CheckboxCellEditor){
			CompositeMap data = (CompositeMap)element;
			String returnValue = data.getString(gridPropties[columnIndex-1]);
			if(returnValue != null && returnValue.equals("true"))	
				return Activator.getImageDescriptor(LocaleMessage.getString("checked.icon")).createImage();
			return Activator.getImageDescriptor(LocaleMessage.getString("unchecked.icon")).createImage();
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
    		if((viewer.getGridStyle()&IGridViewer.NoSeqColumn)!=0){
    			return ""; 
    		}
    		return String.valueOf(++nodeIndex);
    	}
    	
        if(gridPropties==null || gridPropties.length==0)
        	return null;
        String  attrName = gridPropties[columnIndex-1];
        
        ICellEditor cellEditor = viewer.getCellEditor(attrName);
        String returnValue = data.getString(attrName);
        
		if(cellEditor != null){
			if(returnValue !=null)
				cellEditor.SetSelection(returnValue);
		}
        return returnValue;
    }
    public void refresh(){
    	nodeIndex = 0;
    }
    

}
