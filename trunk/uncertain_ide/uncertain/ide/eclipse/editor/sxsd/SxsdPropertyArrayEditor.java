/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor.sxsd;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.editor.FormPage;

import explorer_12.FileSorter;

import aurora_ide.Activator;


import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.PropertyArrayContentProvider;
import uncertain.ide.eclipse.editor.PropertyArrayLabelProvider;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import uncertain.schema.IType;
import uncertain.schema.SchemaManager;

public class SxsdPropertyArrayEditor {
    
  
    public static int isCategory = 0;
    ISchemaManager  mSchemaManager;
    TableViewer     mPropertyViewer;
    Table           mTable;
    CompositeMap    mData;
    FormPage        mformPage;
    ViewForm  viewForm;
    public SxsdPropertyArrayEditor(ISchemaManager schemaManager) {
        mSchemaManager = schemaManager;
    }
    public SxsdPropertyArrayEditor(ISchemaManager schemaManager,FormPage  formPage) {
        mSchemaManager = schemaManager;
        mformPage = formPage;
    } 


    public CompositeMap getData(){
        return mData;
    }
    public void clearAll(){
    	if(mPropertyViewer != null)
    		mPropertyViewer.getTable().dispose();
    }
    
    
    
 
    
    public void createEditor( Composite parent, CompositeMap data ){
    	createEditor( parent );
   		setData( data );
    
    }
    public void createEditor( Composite parent ){
        mPropertyViewer = new TableViewer(parent,SWT.BORDER|SWT.FULL_SELECTION);
        mTable = mPropertyViewer.getTable();
        mPropertyViewer.setContentProvider(new PropertyArrayContentProvider() );
        mPropertyViewer.setCellModifier( new SxsdPropertyArrayCellModifier(mPropertyViewer,mformPage));
        mTable.setLinesVisible(true);
        mTable.setHeaderVisible(true);
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
                mPropertyViewer.setLabelProvider(new PropertyArrayLabelProvider(attrib_list.toArray()));
                for( Iterator it = attrib_list.iterator(); it.hasNext(); ){
                    Attribute attrib = (Attribute)it.next();
                    TableColumn column = new TableColumn(mTable, SWT.LEFT);
                    column.setText(attrib.getName());
                	String imagePath="icons/attribute_obj.gif";
                    column.setImage(Activator.getImageDescriptor(imagePath).createImage());
                    column.setWidth(80);
                }
                mPropertyViewer.setCellEditors(editors);
            }else{
                throw new IllegalArgumentException("Type "+type.getQName()+" is not element");
            }
        }else
            throw new IllegalArgumentException("Type "+elm.getQName()+" is not array");
    }
    
    
    public void setData( CompositeMap data ){
        mData = data;
        if(mTable != null){
	        createEditor( mTable.getParent() );
		   	createTableColumns();
		   	if(mData.getChilds() != null)
		   		mPropertyViewer.setInput(mData);
        }
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
//        sm.loadSchemaByClassPath("uncertain.testcase.schema.extension_test");
        SxsdPropertyArrayEditor editor = new SxsdPropertyArrayEditor(sm);
        
        CompositeMap element = new CompositeMap("my", "http://myobjects.com/schema", "Button");
        element.put("name", "button1");
        element.put("Width", "80");
        element.put("Text", "Click me");
        element.put("controlID", "UID001");
        
//        editor.createEditor(shell,element);
        shell.open ();        
        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        
        display.dispose ();

    }    

}
