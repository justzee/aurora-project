package uncertain.ide.eclipse.editor.widgets;

import java.util.HashMap;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;

import uncertain.composite.CompositeMap;
import uncertain.ide.Common;
import uncertain.ide.eclipse.action.AddPropertyAction;
import uncertain.ide.eclipse.action.CategroyAction;
import uncertain.ide.eclipse.action.CharSortAction;
import uncertain.ide.eclipse.action.CompositeMapAction;
import uncertain.ide.eclipse.action.RefreshAction;
import uncertain.ide.eclipse.action.RemovePropertyAction;
import uncertain.ide.eclipse.celleditor.ICellEditor;
import uncertain.ide.eclipse.editor.ICategoryContainer;
import uncertain.ide.eclipse.editor.IViewer;
import uncertain.schema.editor.AttributeValue;

public class PropertyHashViewer implements ICategoryContainer {
	private HashMap property_editors = new HashMap();
	public static final String COLUMN_PROPERTY = "PROPERTY";
	public static final String COLUMN_VALUE = "VALUE";
	public static final String COLUMN_DOCUMENT = "DOCUMENT";
	public static final String[] TABLE_COLUMN_PROPERTIES = { COLUMN_PROPERTY,
			COLUMN_VALUE, COLUMN_DOCUMENT };

	private Composite parent;
	private TableViewer mPropertyViewer;
	protected IViewer mViewer;
	private boolean isCategory;

	private AttributeValue focus;

	public PropertyHashViewer(IViewer viewer, Composite parent) {
		mViewer = viewer;
		this.parent = parent;
	}

	public void setData(CompositeMap data) {
		mPropertyViewer.setInput(data);
		int xWidth = parent.getBounds().width;
		Table table = mPropertyViewer.getTable();
		int columnWidth = xWidth / table.getColumnCount();
		for (int i = 0, n = table.getColumnCount(); i < n; i++) {
			table.getColumn(i).setWidth(columnWidth);

		}

	}

	public void clearAll() throws Exception {
		if (mPropertyViewer != null) {
			clear();
			mPropertyViewer.getTable().removeAll();// clearAll();
		}
	}

	public void createEditor() {
		if (mPropertyViewer != null
				&& (!mPropertyViewer.getTable().isDisposed()))
			return;
		ViewForm viewForm = new ViewForm(parent, SWT.NONE);
		viewForm.setLayout(new FillLayout());
		createToolbar(viewForm);
		createMainContent(viewForm);

	}

	private void createMainContent(ViewForm viewForm) {
		mPropertyViewer = new TableViewer(viewForm, SWT.BORDER
				| SWT.FULL_SELECTION);
		Table mTable = mPropertyViewer.getTable();
		mPropertyViewer.setLabelProvider(new PropertyHashLabelProvider(this));
		mPropertyViewer.setContentProvider(new PropertyHashContentProvider(
				this));
		mPropertyViewer.setCellModifier(new PropertyHashCellModifier(this));
		mPropertyViewer.setColumnProperties(TABLE_COLUMN_PROPERTIES);
		mPropertyViewer.setCellEditors(new CellEditor[] { null,
				new TextCellEditor(mTable) });
		// mPropertyViewer.setCellEditors(new CellEditor[] { null});
		mTable.setLinesVisible(true);
		mTable.setHeaderVisible(true);

		TableColumn propertycolumn = new TableColumn(mTable, SWT.LEFT);
		propertycolumn.setText(Common.getString("property.name"));
		new TableColumn(mTable, SWT.LEFT).setText(Common.getString("value"));
		new TableColumn(mTable, SWT.LEFT).setText(Common
				.getString("description"));
		viewForm.setContent(mPropertyViewer.getControl()); // 主体：表格
		fillKeyListener(this);
		mPropertyViewer.setSorter(new PropertyHashSorter(this));
		propertycolumn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				((PropertyHashSorter) mPropertyViewer.getSorter()).doSort(0);
//				CellEditorFactory.getInstance().setTableChanged(true);
				refresh();
			}
		});
		refresh();
	}

	private void createToolbar(ViewForm viewForm) {
		ToolBar toolBar = new ToolBar(viewForm, SWT.RIGHT | SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		fillActionToolBars(toolBarManager, this);
		viewForm.setTopLeft(toolBar); // 顶端边缘：工具栏
	}

	public void createEditor(CompositeMap data) {
		createEditor();
		setData(data);
	}

	public TableViewer getTableViewer() {
		return mPropertyViewer;
	}

	public Table getTable() {
		return mPropertyViewer.getTable();
	}

	public boolean isCategory() {
		return isCategory;
	}

	public void setCategory(boolean isCategory) {
//		if (isCategory != this.isCategory) {
//			CellEditorFactory.getInstance().setTableChanged(true);
//		}
		this.isCategory = isCategory;
	}

	public ColumnViewer getObject() {
		return mPropertyViewer;
	}

	public AttributeValue getFocusData() {
		ISelection selection = mPropertyViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		AttributeValue av = (AttributeValue) obj;
		return av;
	}

	public void refresh() {
		if (mPropertyViewer != null && !mPropertyViewer.getTable().isDisposed()) {
			mPropertyViewer.refresh();
		}
	}

	public void refresh(boolean dirty) {
		if (dirty) {
			mViewer.refresh(dirty);
		} else
			refresh();
	}

	public CompositeMap getInput() {
		return (CompositeMap) mPropertyViewer.getInput();

	}

	private void fillActionToolBars(ToolBarManager actionBarManager,
			ICategoryContainer mCategoryObject) {
		// 生成按钮，按钮就是一个个的Action
		Action addAction = new AddPropertyAction(mCategoryObject,
				AddPropertyAction.getDefaultImageDescriptor(), null);

		Action removeAction = new RemovePropertyAction(mCategoryObject,
				RemovePropertyAction.getDefaultImageDescriptor(), null);
		Action refreshAction = new RefreshAction(mCategoryObject, RefreshAction
				.getDefaultImageDescriptor(), null);

		CategroyAction categroyAction = new CategroyAction(mCategoryObject,
				CategroyAction.getDefaultImageDescriptor(), null);
		CharSortAction charSortAction = new CharSortAction(mCategoryObject,
				CharSortAction.getDefaultImageDescriptor(), null);
		/*
		 * 将按钮通过工具栏管理器ToolBarManager填充进工具栏,如果用add(action)
		 * 也是可以的，只不过只有文字没有图像。要显示图像需要将Action包装成
		 * ActionContributionItem，在这里我们将包装的处理过程写成了一个方法。
		 */
		actionBarManager.add(createActionContributionItem(addAction));
		actionBarManager.add(createActionContributionItem(refreshAction));
		actionBarManager.add(createActionContributionItem(removeAction));
		actionBarManager.add(createActionContributionItem(categroyAction));
		actionBarManager.add(createActionContributionItem(charSortAction));
		// 更新工具栏。没有这一句，工具栏上会没有任何显示
		actionBarManager.update(true);
	}

	private ActionContributionItem createActionContributionItem(IAction action) {
		ActionContributionItem aci = new ActionContributionItem(action);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);// 显示图像+文字
		return aci;
	}

	public void fillKeyListener(final ICategoryContainer viewer) {
		mPropertyViewer.getTable().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					CompositeMapAction.removePropertyAction(viewer);
				}
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	public Composite getParent() {
		return parent;
	}

	public void setParent(Composite parent) {
		this.parent = parent;
	}

	public Control getControl() {
		return null;
	}

	public Object getFocus() {
		return focus;
	}

	public Object getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getViewer() {
		return mPropertyViewer;
	}

	public void setFocus(Object data) {
		this.focus = (AttributeValue) data;

	}

	public void setSelection(Object data) {
		// TODO Auto-generated method stub

	}
	public void addEditor(String property,ICellEditor cellEditor){
		property_editors.put(property, cellEditor);
	}
	public void clear() throws Exception{
		Object[] editors = property_editors.values().toArray();
		for(int i=0;i<editors.length;i++){
			ICellEditor ed = (ICellEditor) editors[i];
			ed.validValue(ed.getSelection());
		}
		for(int i=0;i<editors.length;i++){
			ICellEditor ed = (ICellEditor) editors[i];
			ed.dispose();
		}
		property_editors.clear();
	
	}
}