package uncertain.ide.eclipse.editor;

import java.util.Iterator;
import java.util.List;

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
import uncertain.ide.eclipse.action.IDirty;
import uncertain.ide.eclipse.action.IViewerDirty;
import uncertain.ide.eclipse.action.RefreshAction;
import uncertain.ide.eclipse.action.RemoveElementAction;
import uncertain.ide.eclipse.action.RemovePropertyAction;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.IType;

public class PropertyArrayEditor implements IViewerDirty {

	public static final String COLUMN_PROPERTY = "PROPERTY";
	public static final String COLUMN_VALUE = "VALUE";
	public static final String[] TABLE_COLUMN_PROPERTIES = { COLUMN_PROPERTY,
			COLUMN_VALUE };
	TableViewer mPropertyViewer;
	Table mTable;
	CompositeMap mData;
	IDirty mDirtyObject;
	public ViewForm viewForm;

	private Composite parent;
	private PropertyArrayLabelProvider propertyArrayLabelProvider;

	public PropertyArrayEditor(IDirty dirtyObject) {
		super();
		mDirtyObject = dirtyObject;
	}

	public void setData(CompositeMap data) {
		mData = data;
		if (mTable != null) {
			// createEditor( mTable.getParent() );
			createTableColumns();
			if (mData.getChilds() != null)
				mPropertyViewer.setInput(mData);
		}
	}

	public CompositeMap getData() {
		return mData;
	}

	public void clearAll() {
		if (mPropertyViewer != null) {
			mPropertyViewer.getTable().dispose();
			viewForm.dispose();
		}
	}

	protected void createTableColumns() {
		Element elm = Common.getSchemaManager().getElement(mData);
		if (elm == null)
			throw new IllegalArgumentException("Can't get element schema from "
					+ mData.toXML());
		if (elm instanceof Array) {
			Array array = (Array) elm;
			IType type = array.getElementType();
			if (type == null)
				throw new IllegalArgumentException("Can't get array type from "
						+ array.getQName());
			if (type instanceof ComplexType) {
				ComplexType type_element = (ComplexType) type;
				List attrib_list = type_element.getAllAttributes();
				if (attrib_list == null)
					return;
				String[] column_index = new String[attrib_list.size() + 1];
				CellEditor[] editors = new CellEditor[attrib_list.size() + 1];
				int id = 0;
				column_index[0] = "Seq";
				for (Iterator it = attrib_list.iterator(); it.hasNext();) {
					Attribute attrib = (Attribute) it.next();
					editors[++id] = new TextCellEditor(mTable);
					column_index[id] = attrib.getName();
				}
				mPropertyViewer.setColumnProperties(column_index);
				propertyArrayLabelProvider = new PropertyArrayLabelProvider(
						attrib_list.toArray());
				mPropertyViewer.setLabelProvider(propertyArrayLabelProvider);

				TableColumn seq_column = new TableColumn(mTable, SWT.LEFT);
				seq_column.setText("序号");
				String seq_imagePath = "icons/attribute_obj.gif";
				seq_column.setImage(Activator.getImageDescriptor(seq_imagePath)
						.createImage());
				// column.setWidth(80);
				seq_column.pack();

				for (Iterator it = attrib_list.iterator(); it.hasNext();) {
					Attribute attrib = (Attribute) it.next();
					TableColumn column = new TableColumn(mTable, SWT.LEFT);
					column.setText(attrib.getName());
					String imagePath = "icons/attribute_obj.gif";
					column.setImage(Activator.getImageDescriptor(imagePath)
							.createImage());
					// column.setWidth(80);
					column.pack();
				}
				mPropertyViewer.setCellEditors(editors);
			} else {
				throw new IllegalArgumentException("Type " + type.getQName()
						+ " is not element");
			}
		} else
			throw new IllegalArgumentException("Type " + elm.getQName()
					+ " is not array");
	}

	public void createEditor(Composite parent) {
		viewForm = new ViewForm(parent, SWT.NONE);
		viewForm.setLayout(new FillLayout());

		mPropertyViewer = new TableViewer(viewForm, SWT.BORDER
				| SWT.FULL_SELECTION);
		// mPropertyViewer = new
		// TableViewer(parent,SWT.BORDER|SWT.FULL_SELECTION);
		mTable = mPropertyViewer.getTable();
		mPropertyViewer.setContentProvider(new PropertyArrayContentProvider());
		mPropertyViewer.setCellModifier(new PropertyArrayCellModifier(this));
		mTable.setLinesVisible(true);
		mTable.setHeaderVisible(true);

	}

	public void createEditor(Composite parent, CompositeMap data) {
		this.parent = parent;
		createEditor(parent);
		setData(data);
		ToolBar toolBar = new ToolBar(viewForm, SWT.RIGHT | SWT.FLAT);
		// 创建一个toolBar的管理器
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		// 调用fillActionToolBars方法将Action注入ToolBar中
		if (mData != null)
			fillActionToolBars(toolBarManager);
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

	public void fillActionToolBars(ToolBarManager actionBarManager) {
		// 生成按钮，按钮就是一个个的Action
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
			/*
			 * 将按钮通过工具栏管理器ToolBarManager填充进工具栏,如果用add(action)
			 * 也是可以的，只不过只有文字没有图像。要显示图像需要将Action包装成
			 * ActionContributionItem，在这里我们将包装的处理过程写成了一个方法。
			 */
			actionBarManager.add(createActionContributionItem(addAction));
			actionBarManager.add(createActionContributionItem(refreshAction));
			actionBarManager.add(createActionContributionItem(removeAction));
			// 更新工具栏。没有这一句，工具栏上会没有任何显示
			actionBarManager.update(true);
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
		if (dirty) {
			mDirtyObject.setDirty(dirty);
		}
//		System.out.println("refresh  array  ......");
	}

	public CompositeMap getInput() {
		return (CompositeMap) mPropertyViewer.getInput();

	}

	public CompositeMap getFocusData() {
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

	public void fillKeyListener(final IViewerDirty viewer) {
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