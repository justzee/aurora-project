package uncertain.ide.eclipse.editor.widgets;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.ide.Common;
import uncertain.ide.eclipse.action.AddElementAction;
import uncertain.ide.eclipse.action.AddPropertyAction;
import uncertain.ide.eclipse.action.CompositeMapAction;
import uncertain.ide.eclipse.action.RefreshAction;
import uncertain.ide.eclipse.action.RemoveElementAction;
import uncertain.ide.eclipse.action.RemovePropertyAction;
import uncertain.ide.eclipse.celleditor.CellEditorFactory;
import uncertain.ide.eclipse.celleditor.ICellEditor;
import uncertain.ide.eclipse.editor.IContainer;
import uncertain.ide.eclipse.editor.IViewer;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.IType;

public class PropertyGridViewer implements IContainer {

	public static final String COLUMN_PROPERTY = "PROPERTY";
	public static final String COLUMN_VALUE = "VALUE";
	public static final String[] TABLE_COLUMN_PROPERTIES = { COLUMN_PROPERTY,
			COLUMN_VALUE };
	TableViewer mPropertyViewer;
	Table mTable;
	CompositeMap mData;
	IViewer mParent;
	public ViewForm viewForm;

	PropertyGridCellModifier cellModifiers;
	private PropertyGridLabelProvider propertyArrayLabelProvider;
	ToolBarManager toolBarManager;
	public PropertyGridViewer(IViewer parent) {
		super();
		mParent = parent;
	}

	public void setData(CompositeMap data) {
		mData = data;
		if (mTable != null) {
			// createEditor( mTable.getParent() );
//			CellEditorFactory.getInstance().setTableChanged(true);
			createTableColumns();
			if (mData.getChilds() != null)
				mPropertyViewer.setInput(mData);
		}
	}

	public CompositeMap getData() {
		return mData;
	}

	public void clearAll() throws Exception {
		if (mPropertyViewer != null) {
			cellModifiers.clear();
			mPropertyViewer.getTable().dispose();
			viewForm.dispose();
			
		}
		
	}
	protected void createTableColumns() {
		Element elm = Common.getSchemaManager().getElement(mData);
		if (elm == null)
			throw new IllegalArgumentException("Can't get element schema from "
					+ mData.toXML());
		if (!(elm instanceof Array))
			throw new IllegalArgumentException("Type " + elm.getQName()
					+ " is not array");
		Array array = (Array) elm;
		IType type = array.getElementType();
		if (type == null)
			throw new IllegalArgumentException("Can't get array type from "
					+ array.getQName());
		if (!(type instanceof ComplexType))
			throw new IllegalArgumentException("Type " + type.getQName()
					+ " is not element");
		ComplexType type_element = (ComplexType) type;
		List attrib_list = type_element.getAllAttributes();
		if (attrib_list == null)
			return;
		String[] column_index = createColumnProperties(attrib_list);
		mPropertyViewer.setColumnProperties(column_index);
		
		propertyArrayLabelProvider = new PropertyGridLabelProvider(attrib_list
				.toArray(),cellModifiers);
		mPropertyViewer.setLabelProvider(propertyArrayLabelProvider);
		createTableColumn(attrib_list);
		
		CellEditor[] editors = createCellEditors(attrib_list);
		mPropertyViewer.setCellEditors(editors);

	}

	private CellEditor[] createCellEditors(List attrib_list) {
		CellEditor[] editors = new CellEditor[attrib_list.size() + 1];
		int id = 1;
		for (Iterator it = attrib_list.iterator(); it.hasNext();) {
			Attribute attrib = (Attribute) it.next();
//			editors[id++] = new TextCellEditor(mTable);
			ICellEditor cellEditor = CellEditorFactory.getInstance().createCellEditor(this,attrib,null);
			if(cellEditor !=null){
				editors[id++] = cellEditor.getCellEditor();
				cellModifiers.addEditor(attrib.getLocalName(), cellEditor);}
			else{
				editors[id++] = new TextCellEditor(mTable);
			}
		}
//		String[] items = {"true","false","aa"}; 
//		editors[1] = new ComboBoxCellEditor(mTable,items);
		return editors;
	}

	private void createTableColumn(List attrib_list) {
		String seq_imagePath = Common.getString("property.icon");
		Image idp=Activator.getImageDescriptor(seq_imagePath).createImage();
		TableColumn seq_column = new TableColumn(mTable, SWT.LEFT);
		seq_column.setText(Common.getString("sequence"));
		seq_column.setImage(idp);
		// column.setWidth(80);
		seq_column.pack();

		for (Iterator it = attrib_list.iterator(); it.hasNext();) {
			Attribute attrib = (Attribute) it.next();
			TableColumn column = new TableColumn(mTable, SWT.LEFT);
			column.setText(attrib.getLocalName());
			column.setImage(idp);
			// column.setWidth(80);
			column.pack();
		}
	}

	private String[] createColumnProperties(List attrib_list) {
		String[] column_index = new String[attrib_list.size() + 1];
//		CellEditor[] editors = new CellEditor[attrib_list.size() + 1];
		int id = 0;
		column_index[0] = "sequence";
		for (Iterator it = attrib_list.iterator(); it.hasNext();) {
			Attribute attrib = (Attribute) it.next();
//			editors[++id] = new TextCellEditor(mTable);
			column_index[++id] = attrib.getLocalName();
		}
		return column_index;
	}
	public void createEditor(Composite parent) {
		viewForm = new ViewForm(parent, SWT.NONE);
		viewForm.setLayout(new FillLayout());

		mPropertyViewer = new TableViewer(viewForm, SWT.BORDER
				| SWT.FULL_SELECTION);
		// mPropertyViewer = new
		// TableViewer(parent,SWT.BORDER|SWT.FULL_SELECTION);
		mTable = mPropertyViewer.getTable();
		mPropertyViewer.setContentProvider(new PropertyGridContentProvider());
		cellModifiers = new PropertyGridCellModifier(this);
		mPropertyViewer.setCellModifier(cellModifiers);
		mTable.setLinesVisible(true);
		mTable.setHeaderVisible(true);

	}

	public void createEditor(Composite parent, CompositeMap data) {
		createEditor(parent);
		setData(data);
		ToolBar toolBar = new ToolBar(viewForm, SWT.RIGHT | SWT.FLAT);
		// 创建一个toolBar的管理器
		toolBarManager = new ToolBarManager(toolBar);
		// 调用fillActionToolBars方法将Action注入ToolBar中
		if (mData != null)
			createDefaultActions();
		fillKeyListener(this);
		viewForm.setContent(mPropertyViewer.getControl()); // 主体：表格
		viewForm.setTopLeft(toolBar); // 顶端边缘：工具栏
	}

	public TableViewer getTableViewer() {
		return mPropertyViewer;
	}

	public Table getTable() {
		return mTable;
	}

	public boolean IsCategory() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setIsCategory(boolean isCategory) {
		// TODO Auto-generated method stub

	}

	public Object getViewer() {
		// TODO Auto-generated method stub
		return mPropertyViewer;
	}

	public Object getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSelection(Object data) {
		// TODO Auto-generated method stub

	}

	public void setFocus(Object data) {
		// TODO Auto-generated method stub

	}

	public void addActions(IAction[] actions){
		if(actions ==null)
			return;
		for(int i=0;i<actions.length;i++){
			toolBarManager.add(createActionContributionItem(actions[i]));
		}
		toolBarManager.update(true);
	}
	public void setActions(IAction[] actions){
		toolBarManager.removeAll();
		if(actions ==null)
			return;
		for(int i=0;i<actions.length;i++){
			toolBarManager.add(createActionContributionItem(actions[i]));
		}
		toolBarManager.update(true);
	}

	public void createDefaultActions() {
		Element element = Common.getSchemaManager().getElement(mData);
		if (element == null) {
			return;
		}
		if (element.isArray()) {
			final QualifiedName qName = element.getElementType().getQName();
			Action addAction = new AddElementAction(this, mData, qName,
					AddPropertyAction.getDefaultImageDescriptor(), null);

			Action removeAction = new RemoveElementAction(this,
					RemovePropertyAction.getDefaultImageDescriptor(), null);
			Action refreshAction = new RefreshAction(this, RefreshAction
					.getDefaultImageDescriptor(), null);
			toolBarManager.add(createActionContributionItem(addAction));
			toolBarManager.add(createActionContributionItem(refreshAction));
			toolBarManager.add(createActionContributionItem(removeAction));
			toolBarManager.update(true);
		}
	}


	ActionContributionItem createActionContributionItem(IAction action) {
		ActionContributionItem aci = new ActionContributionItem(action);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);// 显示图像+文字
		return aci;
	}

	public void refresh() {
//		System.out.println("refresh..");
		if (mPropertyViewer != null && !mPropertyViewer.getTable().isDisposed()) {
			// mPropertyViewer.refresh();
			mPropertyViewer.setInput(mData);
			propertyArrayLabelProvider.refresh();
		}

	}

	public void refresh(boolean dirty) {
		refresh();
		if (dirty) {
			mParent.refresh(dirty);
		}
	}

	public CompositeMap getInput() {
		return (CompositeMap) mPropertyViewer.getInput();

	}

	public Object getFocus() {
		// TODO Auto-generated method stub
		ISelection selection = mPropertyViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		CompositeMap av = (CompositeMap) obj;
		return av;
	}

	public Control getControl() {
		// TODO Auto-generated method stub
		return viewForm;
	}

	public void fillKeyListener(final IContainer viewer) {
		mTable.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					CompositeMapAction.removeElement(viewer);
				}
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}
}