/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor.sxsd;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.views.IViewCategory;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.action.IDirty;
import uncertain.ide.eclipse.action.IPropertyCategory;
import uncertain.ide.eclipse.action.IViewerDirty;
import uncertain.ide.eclipse.action.PropertyActions;
import uncertain.ide.eclipse.editor.PropertySheetContentProvider;
import uncertain.ide.eclipse.editor.PropertySheetLabelProvider;
import uncertain.ide.eclipse.editor.PropertySorter;
import uncertain.schema.ISchemaManager;
import uncertain.schema.SchemaManager;

public class SxsdPropertyEditor implements IPropertyCategory,IViewerDirty{
    
    public static final String COLUMN_PROPERTY = "PROPERTY";
    public static final String COLUMN_VALUE = "VALUE";
    public static final String[] TABLE_COLUMN_PROPERTIES = {COLUMN_PROPERTY,COLUMN_VALUE};
    
    public static int isCategory = 0;
    ISchemaManager  mSchemaManager;
    TableViewer     mPropertyViewer;
    Table           mTable;
    CompositeMap    mData;
    FormPage        mformPage;
    ViewForm  viewForm;
    IDirty dirtyObject;
    public SxsdPropertyEditor(ISchemaManager schemaManager) {
        mSchemaManager = schemaManager;
    }
    public SxsdPropertyEditor(ISchemaManager schemaManager,FormPage  formPage,IDirty dirtyObject) {
        mSchemaManager = schemaManager;
        mformPage = formPage;
    } 

    public void setData( CompositeMap data ){
        mData = data;
        mPropertyViewer.setInput(data);
        for (int i = 0, n = mTable.getColumnCount(); i < n; i++) {
        	mTable.getColumn(i).setWidth(100);
//            mTable.getColumn(i).pack();
          }        
    }

    public CompositeMap getData(){
        return mData;
    }
    public void clearAll(){
    	if(mPropertyViewer != null){
	    	mPropertyViewer.getTable().dispose();
	    	viewForm.dispose();
    	}
    }
    
    
    
    public void createEditor( Composite parent ){
    	viewForm = new ViewForm(parent, SWT.RESIZE);
		viewForm.setLayout(new FillLayout());

        mPropertyViewer = new TableViewer(viewForm,SWT.BORDER|SWT.FULL_SELECTION);
        mTable = mPropertyViewer.getTable();
        mPropertyViewer.setLabelProvider(new PropertySheetLabelProvider());
        mPropertyViewer.setContentProvider(new PropertySheetContentProvider(mSchemaManager,this) );
        mPropertyViewer.setCellModifier( new SxsdPropertyCellModifier(mPropertyViewer,mformPage));
        mPropertyViewer.setColumnProperties(TABLE_COLUMN_PROPERTIES);
        mPropertyViewer.setCellEditors(new CellEditor[] { null, new TextCellEditor(mTable) });
        mTable.setLinesVisible(true);
        mTable.setHeaderVisible(true);
        
        
        TableColumn propertyColumn = new TableColumn(mTable, SWT.LEFT);
        propertyColumn.setText("属性");

        
        TableColumn valueColumn = new TableColumn(mTable, SWT.LEFT);
        valueColumn.setText("值");

        
        PropertyActions treeActionGroup = new PropertyActions(this,this);
		ToolBar toolBar = new ToolBar(viewForm, SWT.RIGHT|SWT.FLAT);
		// 创建一个toolBar的管理器
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		// 调用fillActionToolBars方法将Action注入ToolBar中
		treeActionGroup.fillActionToolBars(toolBarManager);
        
        
        viewForm.setContent(mPropertyViewer.getControl()); // 主体：表格
		viewForm.setTopLeft(toolBar); // 顶端边缘：工具栏
        
        
        mPropertyViewer.setSorter(new PropertySorter(this));
        propertyColumn.addSelectionListener(new SelectionAdapter(){
        	public void widgetSelected(SelectionEvent e) {
        		((PropertySorter)mPropertyViewer.getSorter()).doSort(0);
        		mPropertyViewer.refresh();
        	}
        });
        mPropertyViewer.refresh();
        
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
//        sm.loadSchemaByClassPath("uncertain.testcase.schema.extension_test");
        SxsdPropertyEditor editor = new SxsdPropertyEditor(sm);
        
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
	public boolean IsCategory() {
		// TODO Auto-generated method stub
		return false;
	}
	public void setIsCategory(boolean isCategory) {
		// TODO Auto-generated method stub
		
	}
	public ColumnViewer getObject() {
		// TODO Auto-generated method stub
		return null;
	}
	public CompositeMap getSelectedData() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setSelectedData(CompositeMap data) {
		// TODO Auto-generated method stub
		
	}
	public void makeDirty() {
		// TODO Auto-generated method stub
		
	}
	public void setFocusData(CompositeMap data) {
		// TODO Auto-generated method stub
		
	}
	public void refresh() {
		mPropertyViewer.refresh();
		
	}
	public Control getControl() {
		// TODO Auto-generated method stub
		return null;
	}
	public CompositeMap getFocusData() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setDirty(boolean dirty) {
		// TODO Auto-generated method stub
		
	}    

}
