/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import uncertain.composite.CompositeMap;
import uncertain.schema.ISchemaManager;
import uncertain.schema.SchemaManager;

public class PropertySheetEditor {
    
    public static final String COLUMN_PROPERTY = "PROPERTY";
    public static final String COLUMN_VALUE = "VALUE";
    public static final String[] TABLE_COLUMN_PROPERTIES = {COLUMN_PROPERTY,COLUMN_VALUE};
    
    ISchemaManager  mSchemaManager;
    TableViewer     mPropertyViewer;
    Table           mTable;
    CompositeMap    mData;
    
    public PropertySheetEditor(ISchemaManager schemaManager) {
        mSchemaManager = schemaManager;
    }    
    
    public void setData( CompositeMap data ){
        mData = data;
        mPropertyViewer.setInput(data);
        for (int i = 0, n = mTable.getColumnCount(); i < n; i++) {
            mTable.getColumn(i).pack();
          }        
    }
    
    public CompositeMap getData(){
        return mData;
    }
    
    
    
    public void createEditor( Composite parent ){
        mPropertyViewer = new TableViewer(parent,SWT.BORDER|SWT.FULL_SELECTION);
        mTable = mPropertyViewer.getTable();
        mPropertyViewer.setLabelProvider(new PropertySheetLabelProvider());
        mPropertyViewer.setContentProvider(new PropertySheetContentProvider(mSchemaManager) );
        mPropertyViewer.setCellModifier( new PropertySheetCellModifier(mPropertyViewer));
        mPropertyViewer.setColumnProperties(TABLE_COLUMN_PROPERTIES);
        mPropertyViewer.setCellEditors(new CellEditor[] { null, new TextCellEditor(mTable) });
        mTable.setLinesVisible(true);
        mTable.setHeaderVisible(true);
        new TableColumn(mTable, SWT.LEFT).setText("Property");
        new TableColumn(mTable, SWT.LEFT).setText("Value");
        
    }
    
    public void createEditor( Composite parent, CompositeMap data ){
        createEditor( parent );
        setData( data );
    }
    
    public TableViewer getTableViewer(){
        return mPropertyViewer;
    }
    
    public Table getTable(){
        return mTable;
    }
    
    public static void main(String[] args) 
        throws Exception
    {
        Display display = new Display ();
        Shell shell = new Shell(display);
        shell.setText("CompositeMap Editor");
        shell.setSize(400, 400);        
        
        shell.setLayout(new FillLayout());
        SchemaManager sm = new SchemaManager();
        sm.loadSchemaByClassPath("uncertain.testcase.schema.extension_test");
        PropertySheetEditor editor = new PropertySheetEditor(sm);
        
        CompositeMap element = new CompositeMap("my", "http://myobjects.com/schema", "Button");
        element.put("name", "button1");
        element.put("Width", "80");
        element.put("Text", "Click me");
        element.put("controlID", "UID001");
        
        editor.createEditor(shell,element);
        shell.open ();        
        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        
        display.dispose ();

    }    

}
