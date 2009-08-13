/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor;

import java.util.Iterator;
import java.util.List;

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

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import uncertain.schema.IType;
import uncertain.schema.SchemaManager;

public class CompositeListEditor {
    
    public static final String COLUMN_PROPERTY = "PROPERTY";
    public static final String COLUMN_VALUE = "VALUE";
    public static final String[] TABLE_COLUMN_PROPERTIES = {COLUMN_PROPERTY,COLUMN_VALUE};
    
    ISchemaManager  mSchemaManager;
    TableViewer     mPropertyViewer;
    Table           mTable;
    CompositeMap    mData;
    
    public CompositeListEditor(ISchemaManager schemaManager) {
        mSchemaManager = schemaManager;
    }    
    
    public void setData( CompositeMap data ){
        mData = data;
        //mPropertyViewer.setInput(data);
    }
    
    public CompositeMap getData(){
        return mData;
    }
    
    protected void createTableColumns(){
        Element elm = mSchemaManager.getElement(mData);
        if(elm == null)
            throw new IllegalArgumentException("Can't get element schema from "+mData.toXML());
        if(elm instanceof Array ){
            Array array = (Array)elm;
            IType type = array.getElementType();
            if(type==null)
                throw new IllegalArgumentException("Can't get array type from "+array.getQName());
            if(type instanceof Element){
                Element type_element = (Element)type;
                List attrib_list = type_element.getAllAttributes();
                if( attrib_list==null ) return;
                String[] column_index = new String[attrib_list.size()];
                CellEditor[] editors = new CellEditor[attrib_list.size()];
                int id=0;
                for(Iterator it = attrib_list.iterator(); it.hasNext(); ){                    
                    Attribute attrib = (Attribute)it.next();
                    editors[id] = new TextCellEditor(mTable);
                    column_index[id++] = attrib.getName(); 
                }
                mPropertyViewer.setColumnProperties(column_index);
                mPropertyViewer.setCellEditors(editors);
                mPropertyViewer.setLabelProvider(new CompositeListLabelProvider(attrib_list.toArray()));
                for( Iterator it = attrib_list.iterator(); it.hasNext(); ){
                    Attribute attrib = (Attribute)it.next();
                    TableColumn column = new TableColumn(mTable, SWT.LEFT);
                    column.setText(attrib.getName());                    
                }
            }else{
                throw new IllegalArgumentException("Type "+type.getQName()+" is not element");
            }
        }else
            throw new IllegalArgumentException("Type "+elm.getQName()+" is not array");
    }
    
    
    public void createEditor( Composite parent ){
        mPropertyViewer = new TableViewer(parent,SWT.BORDER|SWT.FULL_SELECTION);
        mTable = mPropertyViewer.getTable();
        mPropertyViewer.setContentProvider(new CompositeListContentProvider() );
        mPropertyViewer.setCellModifier( new CompositeListCellModifier(mPropertyViewer));
        mPropertyViewer.setInput(mData);
        createTableColumns();
        for (int i = 0, n = mTable.getColumnCount(); i < n; i++) {
            mTable.getColumn(i).pack();
          }     
        mTable.setLinesVisible(true);
        mTable.setHeaderVisible(true);        
    }
    
    public void createEditor( Composite parent, CompositeMap data ){
        setData( data );
        createEditor( parent );
        
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
        shell.setLayout(new FillLayout());
        
        SchemaManager sm = new SchemaManager();
        sm.loadSchemaByClassPath("aurora.testcase.ui.config.components", "sxsd");
        
        CompositeLoader loader = new CompositeLoader();
        CompositeMap data = loader.loadFromClassPath("uncertain.testcase.schema.screen_test");
        CompositeMap options = (CompositeMap)data.getObject("template/select/options");
        
        CompositeListEditor editor = new CompositeListEditor(sm);
        editor.createEditor(shell, options);
        
        shell.open ();
        
        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        
        display.dispose ();

    }    

}
