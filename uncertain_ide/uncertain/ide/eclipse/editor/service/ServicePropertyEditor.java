/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor.service;

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

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.action.ICategory;
import uncertain.ide.eclipse.action.IPropertyCategory;
import uncertain.ide.eclipse.action.IViewerDirty;
import uncertain.ide.eclipse.action.PropertyActions;
import uncertain.ide.eclipse.editor.PropertySheetContentProvider;
import uncertain.ide.eclipse.editor.PropertySheetLabelProvider;
import uncertain.ide.eclipse.editor.PropertySorter;
import uncertain.schema.ISchemaManager;
import uncertain.schema.SchemaManager;

public class ServicePropertyEditor implements IPropertyCategory{
    
    public static final String COLUMN_PROPERTY = "PROPERTY";
    public static final String COLUMN_VALUE = "VALUE";
    public static final String[] TABLE_COLUMN_PROPERTIES = {COLUMN_PROPERTY,COLUMN_VALUE};
    private boolean isCategory;
    ISchemaManager  mSchemaManager;
    TableViewer     mPropertyViewer;
    Table           mTable;
    CompositeMap    mData;
//    FormPage        mformPage;
    ViewForm  viewForm;
    
    IViewerDirty mDirtyAction;

    public ServicePropertyEditor(ISchemaManager schemaManager,IViewerDirty dirtyAction) {
        mSchemaManager = schemaManager;
        this.mDirtyAction=dirtyAction;
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
    	viewForm = new ViewForm(parent, SWT.NONE);
		viewForm.setLayout(new FillLayout());

        mPropertyViewer = new TableViewer(viewForm,SWT.BORDER|SWT.FULL_SELECTION);
        mTable = mPropertyViewer.getTable();
        mPropertyViewer.setLabelProvider(new PropertySheetLabelProvider());
        mPropertyViewer.setContentProvider(new PropertySheetContentProvider(mSchemaManager,this));
        mPropertyViewer.setCellModifier( new ServicePropertyCellModifier(mDirtyAction));
        mPropertyViewer.setColumnProperties(TABLE_COLUMN_PROPERTIES);
        mPropertyViewer.setCellEditors(new CellEditor[] { null, new TextCellEditor(mTable) });
        mTable.setLinesVisible(true);
        mTable.setHeaderVisible(true);
        
//        mTable.setLayout(layout);
//        mTable.setLayoutData(rowData);
        
        TableColumn propertycolumn = new TableColumn(mTable, SWT.LEFT);
        propertycolumn.setText("属性");
        new TableColumn(mTable, SWT.LEFT).setText("值");
        
        PropertyActions treeActionGroup = new PropertyActions(mDirtyAction,this);
		ToolBar toolBar = new ToolBar(viewForm, SWT.RIGHT|SWT.FLAT);
		// 创建一个toolBar的管理器
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		// 调用fillActionToolBars方法将Action注入ToolBar中
		treeActionGroup.fillActionToolBars(toolBarManager);
        
        
        viewForm.setContent(mPropertyViewer.getControl()); // 主体：表格
		viewForm.setTopLeft(toolBar); // 顶端边缘：工具栏
        
        
        mPropertyViewer.setSorter(new PropertySorter(this));
        propertycolumn.addSelectionListener(new SelectionAdapter(){
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
        ServicePropertyEditor editor = new ServicePropertyEditor(sm,null);
        
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
	public  boolean IsCategory() {
		return isCategory;
	}
	public void setIsCategory(boolean isCategory) {
		this.isCategory = isCategory;
	}
	public ColumnViewer getObject() {
		return mPropertyViewer;
	}
	public void makeDirty() {
		mDirtyAction.setDirty(true);
		
	}
	public Control getControl(){
		return viewForm;
	}
	public void setSelectedData(CompositeMap data) {
		// TODO Auto-generated method stub
		
	}
	public CompositeMap getSelectedData() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFocusData(CompositeMap data) {
		// TODO Auto-generated method stub
		
	}

	public CompositeMap getFocusData() {
		// TODO Auto-generated method stub
		return null;
	}

	public void refresh() {
		mPropertyViewer.refresh();
		
	}



}
