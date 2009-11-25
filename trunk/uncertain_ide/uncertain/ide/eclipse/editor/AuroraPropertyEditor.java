package uncertain.ide.eclipse.editor;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.action.IPropertyCategory;
import uncertain.ide.eclipse.action.IViewerDirty;
import uncertain.ide.eclipse.action.PropertyActions;
import uncertain.schema.editor.AttributeValue;

public class AuroraPropertyEditor  implements IPropertyCategory{

	public static final String COLUMN_PROPERTY = "PROPERTY";
	public static final String COLUMN_VALUE = "VALUE";
	public static final String[] TABLE_COLUMN_PROPERTIES = {COLUMN_PROPERTY,COLUMN_VALUE};
	private boolean isCategory;
	TableViewer mPropertyViewer;
	Table mTable;
	CompositeMap mData;
	public ViewForm viewForm;
	protected IViewerDirty mDirtyAction;

	public AuroraPropertyEditor(IViewerDirty DirtyAction) {
		mDirtyAction = DirtyAction;
	}
	
	public void setData(CompositeMap data) {
	        mData = data;
	        mPropertyViewer.setInput(data);
	        for (int i = 0, n = mTable.getColumnCount(); i < n; i++) {
	        	mTable.getColumn(i).setWidth(100);
	//            mTable.getColumn(i).pack();
	          }        
	    }

	public CompositeMap getData() {
	    return mData;
	}

	public void clearAll() {
		if(mPropertyViewer != null){
	    	mPropertyViewer.getTable().dispose();
	    	viewForm.dispose();
		}
	}

	public void createEditor(Composite parent) {
	    	viewForm = new ViewForm(parent, SWT.NONE);
			viewForm.setLayout(new FillLayout());
	
	        mPropertyViewer = new TableViewer(viewForm,SWT.BORDER|SWT.FULL_SELECTION);
	        mTable = mPropertyViewer.getTable();
	        mPropertyViewer.setLabelProvider(new PropertySheetLabelProvider());
	        mPropertyViewer.setContentProvider(new PropertySheetContentProvider(this));
	        mPropertyViewer.setCellModifier( new AuroraPropertyCellModifier(this));
	        mPropertyViewer.setColumnProperties(TABLE_COLUMN_PROPERTIES);
	        mPropertyViewer.setCellEditors(new CellEditor[] { null, new TextCellEditor(mTable) });
	        mTable.setLinesVisible(true);
	        mTable.setHeaderVisible(true);
	        
	//        mTable.setLayout(layout);
	//        mTable.setLayoutData(rowData);
	        
	        TableColumn propertycolumn = new TableColumn(mTable, SWT.LEFT);
	        propertycolumn.setText("属性");
	        new TableColumn(mTable, SWT.LEFT).setText("值");
	        
	        PropertyActions treeActionGroup = new PropertyActions(this);
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

	public void createEditor(Composite parent, CompositeMap data) {
	    createEditor( parent );
	    setData( data );
	}

	public TableViewer getTableViewer() {
	    return mPropertyViewer;
	}

	public Table getTable() {
	    return mTable;
	}

	public AuroraPropertyEditor() {
		super();
	}

	public boolean IsCategory() {
		return isCategory;
	}

	public void setIsCategory(boolean isCategory) {
		this.isCategory = isCategory;
	}

	public ColumnViewer getObject() {
		return mPropertyViewer;
	}

	public Control getControl() {
		return viewForm;
	}

	public AttributeValue getFocusData() {
		// TODO Auto-generated method stub
		ISelection selection = mPropertyViewer.getSelection();
		Object obj = ((IStructuredSelection) selection)
				.getFirstElement();
		AttributeValue av = (AttributeValue) obj;
		return av;
	}

	public void refresh() {
		mPropertyViewer.refresh();
		
	}

	public void refresh(boolean dirty) {
		if(dirty){
			mDirtyAction.refresh(dirty);
		}
		mPropertyViewer.refresh();
	}

	public CompositeMap getInput() {
		return (CompositeMap) mPropertyViewer.getInput();
		
	}

}