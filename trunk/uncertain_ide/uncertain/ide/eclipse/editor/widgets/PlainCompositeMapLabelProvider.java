package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;

public class PlainCompositeMapLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	int nodeIndex;
	public String[] columnProperties;

	public PlainCompositeMapLabelProvider(String[] columnProperties) {
		this.columnProperties = columnProperties;
	}

	public String getColumnText(Object element, int columnIndex) {
		CompositeMap record = (CompositeMap) element;
		
    	//the first column is sequence.
    	if(columnIndex == 0){
//    		System.out.println("nodeIndex:"+nodeIndex);
    		int returnInt = ++nodeIndex;
/*    		int count = record.getParent().getChildsNotNull().size();
    		System.out.println("count:"+count+"nodeIndex:"+nodeIndex);
    		if(count == nodeIndex){
    			nodeIndex = 0;
    		}*/
    		return String.valueOf(returnInt);
    		
    	}
		String propertyName = columnProperties[columnIndex];
		return record.getString(propertyName);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}
	public void refresh(){
		nodeIndex = 0;
	}
}