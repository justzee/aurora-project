/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import uncertain.composite.CompositeMap;
import uncertain.schema.ISchemaManager;
import uncertain.schema.SchemaManager;

public class PropertySheetEditor {
    
    public static final String COLUMN_PROPERTY = "PROPERTY";
    public static final String COLUMN_VALUE = "Value";
    public static final String[] TABLE_COLUMN_PROPERTIES = {COLUMN_PROPERTY,COLUMN_VALUE};
    
    ISchemaManager  mSchemaManager;
    
    public PropertySheetEditor(ISchemaManager schemaManager) {
        mSchemaManager = schemaManager;
    }    
    
    public void createEditor( Shell shell, CompositeMap data ){
        final TableViewer v = new TableViewer(shell,SWT.BORDER|SWT.FULL_SELECTION);
        Table table = v.getTable();
        v.setLabelProvider(new PropertySheetLabelProvider());
        v.setContentProvider(new PropertySheetContentProvider(mSchemaManager) );
        v.setCellModifier( new PropertySheetCellModifier(v));
        v.setColumnProperties(TABLE_COLUMN_PROPERTIES);
        v.setCellEditors(new CellEditor[] { null, new TextCellEditor(table) });
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        new TableColumn(table, SWT.LEFT).setText("Property");
        new TableColumn(table, SWT.LEFT).setText("Value");
        v.setInput(data);
        for (int i = 0, n = table.getColumnCount(); i < n; i++) {
            table.getColumn(i).pack();
          }
    }
    
    public static void main(String[] args) {
        Display display = new Display ();
        Shell shell = new Shell(display);
        shell.setText("CompositeMap Editor");
        shell.setSize(400, 400);        
        
        shell.setLayout(new FillLayout());
        SchemaManager sm = new SchemaManager();
        PropertySheetEditor editor = new PropertySheetEditor(sm);
        
        CompositeMap element = new CompositeMap("element");
        element.put("name", "query");
        element.put("minOccur", "0");
        element.put("maxOccur", "unbounded");
        
        editor.createEditor(shell, element);
        shell.open ();        
        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        
        display.dispose ();

    }    

}
