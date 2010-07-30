package uncertain.ide.eclipse.editor.widgets;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
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
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.action.AddElementAction;
import uncertain.ide.eclipse.action.AddPropertyAction;
import uncertain.ide.eclipse.action.RefreshAction;
import uncertain.ide.eclipse.action.RemoveElementAction;
import uncertain.ide.eclipse.action.RemovePropertyAction;
import uncertain.ide.eclipse.celleditor.CellEditorFactory;
import uncertain.ide.eclipse.celleditor.ICellEditor;
import uncertain.ide.eclipse.editor.CompositeMapViewer;
import uncertain.ide.eclipse.editor.ITableViewer;
import uncertain.ide.eclipse.editor.IViewer;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.IType;

public class PropertyGridViewer extends CompositeMapViewer implements
		ITableViewer {

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

	private boolean isPacked = true;
	public PropertyGridViewer(IViewer parent) {
		super();
		mParent = parent;
	}
	public PropertyGridViewer(IViewer parent,boolean isInitPacked) {
		super();
		mParent = parent;
		isPacked = isInitPacked;
	}

	public void setData(CompositeMap data) {
		mData = data;
		if (mTable != null) {
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
		Element elm = LoadSchemaManager.getSchemaManager().getElement(mData);
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
				.toArray(), cellModifiers);
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
			// editors[id++] = new TextCellEditor(mTable);
			ICellEditor cellEditor = CellEditorFactory.getInstance()
					.createCellEditor(this, attrib, null, null);
			if (cellEditor != null) {
				editors[id++] = cellEditor.getCellEditor();
				cellModifiers.addEditor(attrib.getLocalName(), cellEditor);
			} else {
				editors[id++] = new TextCellEditor(mTable);
			}
		}
		return editors;
	}

	private void createTableColumn(List attrib_list) {
		String seq_imagePath = LocaleMessage.getString("property.icon");
		Image idp = Activator.getImageDescriptor(seq_imagePath).createImage();
		TableColumn seq_column = new TableColumn(mTable, SWT.LEFT);
		seq_column.setText(LocaleMessage.getString("sequence"));
		seq_column.setImage(idp);
		// column.setWidth(80);
		seq_column.pack();

		for (Iterator it = attrib_list.iterator(); it.hasNext();) {
			Attribute attrib = (Attribute) it.next();
			TableColumn column = new TableColumn(mTable, SWT.LEFT);
			column.setText(attrib.getLocalName());
			column.setImage(idp);
			if(!isPacked)
				column.setWidth(40);
			else
				column.pack();
		}
	}

	private String[] createColumnProperties(List attrib_list) {
		String[] column_index = new String[attrib_list.size() + 1];
		// CellEditor[] editors = new CellEditor[attrib_list.size() + 1];
		int id = 0;
		column_index[0] = "sequence";
		for (Iterator it = attrib_list.iterator(); it.hasNext();) {
			Attribute attrib = (Attribute) it.next();
			// editors[++id] = new TextCellEditor(mTable);
			column_index[++id] = attrib.getLocalName();
		}
		return column_index;
	}

	public void createEditor(Composite parent) {
		viewForm = new ViewForm(parent, SWT.NONE);
		viewForm.setSize(200, 200);
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
		toolBarManager = new ToolBarManager(toolBar);
		if (mData != null)
			createDefaultActions();
		fillKeyListener();
		viewForm.setContent(mPropertyViewer.getControl());
		viewForm.setTopLeft(toolBar);
	}

	public TableViewer getTableViewer() {
		return mPropertyViewer;
	}

	public Table getTable() {
		return mTable;
	}

	public TableViewer getViewer() {
		return mPropertyViewer;
	}

	public void addActions(IAction[] actions) {
		if (actions == null)
			return;
		for (int i = 0; i < actions.length; i++) {
			toolBarManager.add(createActionContributionItem(actions[i]));
		}
		toolBarManager.update(true);
	}

	public void setActions(IAction[] actions) {
		toolBarManager.removeAll();
		if (actions == null)
			return;
		for (int i = 0; i < actions.length; i++) {
			toolBarManager.add(createActionContributionItem(actions[i]));
		}
		toolBarManager.update(true);
	}

	public void createDefaultActions() {
		Element element = LoadSchemaManager.getSchemaManager()
				.getElement(mData);
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
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);// ��ʾͼ��+����
		return aci;
	}

	public void refresh(boolean dirty) {
		if (dirty) {
			mParent.refresh(dirty);
		} else {
			if (mPropertyViewer != null
					&& !mPropertyViewer.getTable().isDisposed()) {
				if (mData != null && mData.getChilds() != null) {
					mPropertyViewer.setInput(mData);
					propertyArrayLabelProvider.refresh();
				}
			}
		}
	}

	public void packColumns() {
		if(mTable == null || isPacked)
			return;
		for (int i = 0; i < mTable.getColumnCount(); i++) {
			TableColumn column = mTable.getColumn(i);
			column.pack();
		}
		isPacked = true;
	}

	public CompositeMap getInput() {
		return (CompositeMap) mPropertyViewer.getInput();

	}

	public Control getControl() {
		return viewForm;
	}

	public void fillKeyListener() {
		mTable.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					removeElement();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

	}
}