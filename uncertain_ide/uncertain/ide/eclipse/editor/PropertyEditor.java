package uncertain.ide.eclipse.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;

import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.action.AddPropertyAction;
import uncertain.ide.eclipse.action.CategroyAction;
import uncertain.ide.eclipse.action.CharSortAction;
import uncertain.ide.eclipse.action.CompositeMapAction;
import uncertain.ide.eclipse.action.IPropertyCategory;
import uncertain.ide.eclipse.action.IViewerDirty;
import uncertain.ide.eclipse.action.RefreshAction;
import uncertain.ide.eclipse.action.RemovePropertyAction;
import uncertain.schema.Element;
import uncertain.schema.editor.AttributeValue;

public class PropertyEditor  implements IPropertyCategory{

	public static final String COLUMN_PROPERTY = "PROPERTY";
	public static final String COLUMN_VALUE = "VALUE";
	public static final String[] TABLE_COLUMN_PROPERTIES = {COLUMN_PROPERTY,COLUMN_VALUE};
	private boolean isCategory;
	TableViewer mPropertyViewer;
	Table mTable;
	CompositeMap mData;
	public ViewForm viewForm;
	protected IViewerDirty mDirtyAction;

	public PropertyEditor(IViewerDirty DirtyAction) {
		mDirtyAction = DirtyAction;
	}
	
	public void setData(CompositeMap data) {
	        mData = data;
	        mPropertyViewer.setInput(data);
//	        System.out.println(viewForm.getParent().getBounds().width);
	        int xWidth = viewForm.getParent().getBounds().width;
	        int columnWidth  = xWidth/mTable.getColumnCount();
	        for (int i = 0, n = mTable.getColumnCount(); i < n; i++) {
//	        	mTable.getColumn(i).setWidth(150);
	        	mTable.getColumn(i).setWidth(columnWidth);
	        	
//	            mTable.getColumn(i).pack();

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
	        mPropertyViewer.setCellModifier( new PropertyCellModifier(this));
	        mPropertyViewer.setColumnProperties(TABLE_COLUMN_PROPERTIES);
	        mPropertyViewer.setCellEditors(new CellEditor[] { null, new TextCellEditor(mTable) });
	        mTable.setLinesVisible(true);
	        mTable.setHeaderVisible(true);
	        
	//        mTable.setLayout(layout);
	//        mTable.setLayoutData(rowData);
	        
	        TableColumn propertycolumn = new TableColumn(mTable, SWT.LEFT);
	        propertycolumn.setText("属性");
	        new TableColumn(mTable, SWT.LEFT).setText("值");
	        
			ToolBar toolBar = new ToolBar(viewForm, SWT.RIGHT|SWT.FLAT);
			// 创建一个toolBar的管理器
			ToolBarManager toolBarManager = new ToolBarManager(toolBar);
			// 调用fillActionToolBars方法将Action注入ToolBar中
			fillActionToolBars(toolBarManager,this);
			fillKeyListener(this);
	        
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

	public PropertyEditor() {
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
		if(mPropertyViewer!= null&&!mPropertyViewer.getTable().isDisposed())
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
	public void fillActionToolBars(ToolBarManager actionBarManager,IPropertyCategory mCategoryObject) {
		// 生成按钮，按钮就是一个个的Action
		Action addAction = new AddPropertyAction(mCategoryObject,AddPropertyAction.getDefaultImageDescriptor(),null);

		Action removeAction = new RemovePropertyAction(mCategoryObject,RemovePropertyAction.getDefaultImageDescriptor(),null);
		Action refreshAction = new RefreshAction(mCategoryObject,RefreshAction.getDefaultImageDescriptor(),null);

		CategroyAction categroyAction = new CategroyAction(mCategoryObject,CategroyAction.getDefaultImageDescriptor(),null);
		CharSortAction charSortAction = new CharSortAction(mCategoryObject,CharSortAction.getDefaultImageDescriptor(),null);
		/*
		 * 将按钮通过工具栏管理器ToolBarManager填充进工具栏,如果用add(action)
		 * 也是可以的，只不过只有文字没有图像。要显示图像需要将Action包装成
		 * ActionContributionItem，在这里我们将包装的处理过程写成了一个方法。
		 * 
		 */
		actionBarManager.add(createActionContributionItem(addAction));
		actionBarManager.add(createActionContributionItem(refreshAction));
		actionBarManager.add(createActionContributionItem(removeAction));
		actionBarManager.add(createActionContributionItem(categroyAction));
		actionBarManager.add(createActionContributionItem(charSortAction));
		// 更新工具栏。没有这一句，工具栏上会没有任何显示
		actionBarManager.update(true);
	}
	ActionContributionItem createActionContributionItem(IAction action) {
		ActionContributionItem aci = new ActionContributionItem(action);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);// 显示图像+文字
		return aci;
	}
	public void fillKeyListener(final IPropertyCategory viewer) {
		mTable.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(e.keyCode==SWT.DEL){
					CompositeMapAction.removePropertyAction(viewer);
				}
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

}

}