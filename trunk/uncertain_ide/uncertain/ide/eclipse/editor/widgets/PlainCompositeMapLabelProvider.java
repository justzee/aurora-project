package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;

public class PlainCompositeMapLabelProvider extends LabelProvider implements IGridLabelProvider  {

	int nodeIndex;
	public String[] columnProperties;

	public PlainCompositeMapLabelProvider(String[] columnProperties) {
		this.columnProperties = columnProperties;
	}

	public String getColumnText(Object element, int columnIndex) {
		CompositeMap record = (CompositeMap) element;
		
    	//the first column is sequence.
    	if(columnIndex == 0){
    		int returnInt = ++nodeIndex;
    		return String.valueOf(returnInt);
    	}
		String propertyName = columnProperties[columnIndex-1];
		return record.getString(propertyName);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}
	public void refresh(){
		nodeIndex = 0;
	}
}