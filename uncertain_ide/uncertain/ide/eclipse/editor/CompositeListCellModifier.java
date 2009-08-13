/*
 * Created on 2009-8-13
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import uncertain.composite.CompositeMap;
import uncertain.schema.editor.AttributeValue;

public class CompositeListCellModifier implements ICellModifier {
    
    /**
     * @param viewer
     */
    public CompositeListCellModifier(TableViewer viewer) {
        super();
        mViewer = viewer;
    }

    TableViewer mViewer;

    public boolean canModify(Object element, String property) {
        return true;
    }

    public Object getValue(Object element, String property) {        
        Object value =  ((CompositeMap)element).get(property);
        return value;
    }

    public void modify(Object element, String property, Object value) {
        TableItem item = (TableItem)element;        
        CompositeMap data = (CompositeMap)item.getData();        
        data.put(property, value);
        mViewer.refresh();
    }

}
