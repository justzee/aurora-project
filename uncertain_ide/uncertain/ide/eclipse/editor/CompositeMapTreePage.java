package uncertain.ide.eclipse.editor;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.composite.XMLOutputter;
import uncertain.ide.Activator;
import uncertain.ide.Common;
import uncertain.ide.eclipse.action.CompositeMapAction;
import uncertain.ide.eclipse.action.ElementSelectionListener;
import uncertain.ide.eclipse.action.ToolBarAddElementListener;
import uncertain.ide.eclipse.celleditor.CellEditorFactory;
import uncertain.ide.eclipse.celleditor.ICellEditor;
import uncertain.ide.eclipse.editor.textpage.JavaScriptLineStyler;
import uncertain.ide.eclipse.editor.widgets.PropertyGridViewer;
import uncertain.ide.eclipse.editor.widgets.PropertyHashViewer;
import uncertain.ide.eclipse.editor.widgets.CompositeMapTreeViewer;
import uncertain.schema.editor.AttributeValue;

public class CompositeMapTreePage extends FormPage implements IViewer {

	protected CompositeMapTreeViewer mTreeEditor;
	private CTabFolder mTabFolder;
	private PropertyHashViewer mPropertyEditor;
	private PropertyGridViewer mPropertyArrayEditor;
	private StyledText mInnerText;
	private CompositeMap data;

	private JavaScriptLineStyler lineStyler = new JavaScriptLineStyler();
	private SashForm sashForm;
	private boolean modify = false;
	private Label elementDocument;
	
	Composite shell ;
	public CompositeMapTreePage(String id, String title) {
		super(id, title);
	}

	public CompositeMapTreePage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		shell = form.getBody();
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);

		try {
			CompositeLoader loader = new CompositeLoader();
			data = loader.loadByFile(getFile().getAbsolutePath());
			createContent(shell, toolkit);
		} catch (Exception e) {
			Common.showExceptionMessageBox(null, e);
		}
	}

	protected void createContent(Composite shell, FormToolkit toolkit) {

		sashForm = new SashForm(shell, SWT.NONE);

		createElementContent(sashForm, toolkit);
		createPropertyContent(sashForm);

		final ElementSelectionListener listener =  new ElementSelectionListener(
				mTabFolder, mPropertyEditor, mPropertyArrayEditor,
				mTreeEditor, mInnerText,lineStyler,elementDocument);
		mTreeEditor.addSelectionChangedListener(listener);
		((ColumnViewer)mTreeEditor.getViewer()).addPostSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				if(listener.postChangeDiabled())
					return;
				initEditors(mPropertyEditor);
				
			}
		});
		
		sashForm.setWeights(new int[] { 40, 60 });
	}

	protected void createElementContent(Composite mContent, FormToolkit toolkit) {

		ViewForm viewForm = new ViewForm(mContent, SWT.NONE);
		viewForm.setLayout(new FillLayout());

		Tree tree = toolkit.createTree(viewForm, SWT.NONE);
//		mServiceTreeEditor = new TreeEditor(mContent, this, data);
		mTreeEditor = new CompositeMapTreeViewer(tree, this, data);

		viewForm.setContent(mTreeEditor.getControl()); // 主体：表格
		fillElementToolBar(viewForm, mTreeEditor);

	}

	private void createPropertyContent(Composite parent) {
	
		createTabFolder(parent);
		createItem0Page(mTabFolder);
		
		mPropertyArrayEditor = new PropertyGridViewer(this);
		mPropertyArrayEditor.createEditor(mTabFolder);
		mTabFolder.getItem(1).setControl(mPropertyArrayEditor.viewForm);

	}

	private void createItem0Page(Composite parent) {

		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		mPropertyEditor = new PropertyHashViewer(this,sashForm);

		mPropertyEditor.createEditor();
	    elementDocument = new Label(sashForm, SWT.LEFT);
		mTabFolder.getItem(0).setControl(sashForm);
		sashForm.setWeights(new int[] {92, 8});

	}
	
	private void createTabFolder(final Composite parent) {
		mTabFolder = new CTabFolder(parent, SWT.TOP);
		mTabFolder.setMaximizeVisible(true);
		// mTabFolder.setMinimizeVisible(true);
		mTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void minimize(CTabFolderEvent event) {
				mTabFolder.setMinimized(true);
				mTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						false));
				parent.layout(true);// 刷新布局
			}

			public void maximize(CTabFolderEvent event) {
				mTabFolder.setMaximized(true);
				// mTabFolder.setLayoutData(new
				// GridData(SWT.FILL,SWT.FILL,true,true));
				sashForm.setMaximizedControl(mTabFolder);
				parent.layout(true);
			}

			public void restore(CTabFolderEvent event) {
				// mTabFolder.setMinimized(false);
				mTabFolder.setMaximized(false);
				// mTabFolder.setLayoutData(new
				// GridData(SWT.FILL,SWT.FILL,false,false));
				sashForm.setMaximizedControl(null);
				parent.layout(true);
			}
		});
		// mInnerText = new StyledText(mTabFolder, SWT.MULTI | SWT.V_SCROLL |
		// SWT.WRAP);
		createStyledText();
		mInnerText.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}

			public void focusLost(FocusEvent e) {

				String newText = mInnerText.getText();
				if (newText == null) {
					newText = "";
				}
				newText = newText.trim();
				String oldText = ((CompositeMap)mTreeEditor.getFocus()).getText();
				if (oldText == null) {
					oldText = "";
				}
				oldText = oldText.trim();
				if (!newText.equals(oldText)) {
					((CompositeMap)mTreeEditor.getFocus()).setText(newText);
					refresh(true);
				}

			}

		});
		mTabFolder.setBorderVisible(true);
		mTabFolder.setSimple(false);
		mTabFolder.setTabHeight(23);

		CTabItem tabItem1 = new CTabItem(mTabFolder, SWT.None | SWT.MULTI
				| SWT.V_SCROLL);
		String tab = "         ";
		tabItem1.setText(tab+Common.getString("property.name")+tab);

		CTabItem tabItem2 = new CTabItem(mTabFolder, SWT.None | SWT.MULTI
				| SWT.V_SCROLL);
		tabItem2.setText(tab+Common.getString("child.list")+tab);

		CTabItem tabItem3 = new CTabItem(mTabFolder, SWT.None | SWT.MULTI
				| SWT.V_SCROLL);
		tabItem3.setText(tab+Common.getString("value")+tab);
		tabItem3.setControl(mInnerText);

		CTabItem tabItem4 = new CTabItem(mTabFolder, SWT.None | SWT.MULTI
				| SWT.V_SCROLL);
		tabItem4.setText(tab+Common.getString("editor")+tab);

		mTabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		// mTabFolder.(new CTabFolder2Adapter(){});

	}

	private void createStyledText() {
		mInnerText = new StyledText(mTabFolder, SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL);
		GridData spec = new GridData();
		spec.horizontalAlignment = GridData.FILL;
		spec.grabExcessHorizontalSpace = true;
		spec.verticalAlignment = GridData.FILL;
		spec.grabExcessVerticalSpace = true;
		mInnerText.setLayoutData(spec);
		mInnerText.addLineStyleListener(lineStyler);
		mInnerText.setFont(new Font(mTabFolder.getDisplay(), "Courier New", 10, SWT.NORMAL));

	}

	protected File getFile() {
		IFile ifile = ((IFileEditorInput) getEditor().getEditorInput())
				.getFile();
		String fileName = Common.getIfileLocalPath(ifile);
		return new File(fileName);
	}

	public void changeSelection(CompositeMap newSelection) {
		mPropertyEditor.setData(newSelection);
	}

	public void doSave(IProgressMonitor monitor) {
		try {
			File file = getFile();
			XMLOutputter.saveToFile(file, data);
			// setDirty(false);
		} catch (Exception e) {
			Common.showExceptionMessageBox(null, e);
		}
	}

	public void refresh(boolean dirty) {
		if (dirty) {
//			System.out.println("mainform..............");
			mTreeEditor.refresh();
			mPropertyEditor.refresh();
			mPropertyArrayEditor.refresh();
			setModify(true);
		}		
		getEditor().editorDirtyStateChanged();

	}

	public void fillElementToolBar(Composite shell,
			final IContainer container) {

		ToolBar toolBar = new ToolBar(shell, SWT.RIGHT | SWT.FLAT);
		// 创建一个toolBar的管理器
		Menu menu = new Menu(shell);

		ToolItem addItem = new ToolItem(toolBar, SWT.DROP_DOWN);
		setToolItemShowProperty(addItem, Common.getString("add.element.label"), Common.getString("add.icon"));
		addItem.addListener(SWT.Selection, new ToolBarAddElementListener(
				toolBar, menu, addItem, container));

		final ToolItem cutItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(cutItem, Common.getString("cut"), Common.getString("cut.icon"));
		cutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				CompositeMapAction.cutElement(container);
			}
		});

		final ToolItem copyItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(copyItem, Common.getString("copy"), Common.getString("copy.icon"));
		copyItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				CompositeMapAction.copyElement(container);
			}
		});

		final ToolItem pasteItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(pasteItem, Common.getString("paste"), Common.getString("paste.icon"));
		pasteItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				CompositeMapAction.pasteElement(container);
			}
		});
		final ToolItem refreshItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(refreshItem, Common.getString("refresh"), Common.getString("refresh.icon"));
		refreshItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				((ColumnViewer)mTreeEditor.getViewer()).refresh();
				Common.refeshSchemaManager();
			}
		});
		final ToolItem removeItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(removeItem, Common.getString("delete"), Common.getString("delete.icon"));
		removeItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				CompositeMapAction.removeElement(container);

			}
		});
		toolBar.pack();
		((ViewForm) shell).setTopLeft(toolBar); // 顶端边缘：工具栏
	}

	private void setToolItemShowProperty(ToolItem toolItem, String text,
			String iconPath) {
		// if (text != null && !text.equals(""))
		// toolItem.setText(text);
		if (text != null && !text.equals(""))
			toolItem.setToolTipText(text);
		if (iconPath != null && !iconPath.equals("")) {
			Image icon = Activator.getImageDescriptor(iconPath).createImage();
			toolItem.setImage(icon);
		}

	}

	public void refresh(CompositeMap data) {
		this.data = data;
		mTreeEditor.setInput(data);
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}
	public CompositeMap getData() {
		return data;
	}

	public void setData(CompositeMap data) {
		this.data = data;
	}
	public void initEditors(ICategoryContainer container) {
//		if (CellEditorFactory.getInstance().isTableChanged()){
//			clear();
//		}
		Table table = ((TableViewer) container.getViewer()).getTable();
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			AttributeValue av = (AttributeValue) item.getData();
			if (av instanceof CategoryLabel) {
				continue;
			}
			ICellEditor cellEditor = CellEditorFactory.getInstance().createCellEditor(container, av.getAttribute(),item);
			if(cellEditor != null){
				mPropertyEditor.addEditor(av.getAttribute().getLocalName(), cellEditor);
				fillTableCellEditor(table, item, av.getAttribute().getQName(),
						cellEditor);
			}
		}
//		CellEditorFactory.getInstance().setTableChanged(false);

	}
	private void fillTableCellEditor(Table table, TableItem item,
			QualifiedName attributeQName, ICellEditor cellEditor) {
		TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.setEditor(cellEditor.getCellControl(), item, 1);
//		addCellEditors(attributeQName, cellEditor);
	}
}