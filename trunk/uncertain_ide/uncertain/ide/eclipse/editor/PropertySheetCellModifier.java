/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import uncertain.schema.editor.AttributeValue;

public class PropertySheetCellModifier implements ICellModifier {
    
    /**
     * @param viewer
     */
    public PropertySheetCellModifier(TableViewer viewer) {
        super();
        mViewer = viewer;
    }

    public static final String[] PROPERTY_TO_UPDATE = {PropertySheetEditor.COLUMN_VALUE};
    
    TableViewer     mViewer;

    public boolean canModify(Object element, String property) {
        return PropertySheetEditor.COLUMN_VALUE.equals(property);
    }

    public Object getValue(Object element, String property) {
        AttributeValue  av = (AttributeValue)element;
        if( PropertySheetEditor.COLUMN_VALUE.equals(property) )
            return av.getValue();
        else
            return av.getAttribute().getName();
    }

    public void modify(Object element, String property, Object value) {
        TableItem item = (TableItem)element;        
        AttributeValue  av = (AttributeValue)item.getData();
        av.getContainer().put(av.getAttribute().getName(), value);  
        //mViewer.update( item.getData(), null);
        mViewer.refresh();
    }

}
