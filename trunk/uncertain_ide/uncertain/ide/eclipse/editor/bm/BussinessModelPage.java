package uncertain.ide.eclipse.editor.bm;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.composite.XMLOutputter;
import uncertain.ide.Common;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.eclipse.action.AddFieldAction;
import uncertain.ide.eclipse.action.AddPropertyAction;
import uncertain.ide.eclipse.action.RefreshAction;
import uncertain.ide.eclipse.action.RemoveElementAction;
import uncertain.ide.eclipse.action.RemovePropertyAction;
import uncertain.ide.eclipse.editor.BaseCompositeMapViewer;
import uncertain.ide.eclipse.editor.CompositeMapPage;
import uncertain.ide.eclipse.editor.IViewer;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.PropertyGridViewer;
import uncertain.ide.eclipse.editor.widgets.PropertyHashViewer;
import uncertain.schema.Array;
import uncertain.schema.Element;
import uncertain.schema.IType;

public class BussinessModelPage extends CompositeMapPage {
	private static final String PageId = "BussinessModelPage";
	private static final String PageTitle = "Bussiness Model";
	private CTabFolder mTabFolder;
	private CompositeMap data;
	private SashForm sashForm;
	private boolean modify = false;
	Composite shell;
	private ArrayList childViews;
	private QualifiedName bm_model = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "model");

	public BussinessModelPage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		shell = form.getBody();
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);
		Element schemaElement = LoadSchemaManager.getSchemaManager()
				.getElement(bm_model);
		if (schemaElement == null) {
			throw new RuntimeException("Please add the bm schema file first!");
		}
		try {
			CompositeLoader loader = new CompositeLoader();
			loader.setSaveNamespaceMapping(true);
			data = loader.loadByFile(getFile().getAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage(), e.getCause());
		}
		if (!data.getQName().equals(bm_model))
			throw new RuntimeException("This root element is not " + bm_model
					+ " !");
		createContent(shell);
	}

	protected void createContent(Composite shell) {
		Control[] childs = shell.getChildren();
		for (int i = 0; i < childs.length; i++) {
			Control temp = childs[i];
			if (!temp.isDisposed()) {
				temp.dispose();
			}
		}
		sashForm = new SashForm(shell, SWT.VERTICAL);
		createMasterContent(sashForm);
		createDetailContent(sashForm);
		sashForm.setWeights(new int[] { 30, 70 });
		shell.layout(true);
	}

	protected void createMasterContent(Composite parent) {
		if (childViews != null)
			childViews.clear();
		else
			childViews = new ArrayList();
		PropertyHashViewer mPropertyEditor = new PropertyHashViewer(this,
				parent);
		childViews.add(mPropertyEditor);
		mPropertyEditor.createEditor();
		try {
			mPropertyEditor.clear();
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		mPropertyEditor.setData(data);
	}

	private void createDetailContent(Composite parent) {
		mTabFolder = createTabFolder(parent);
		Element model_em = LoadSchemaManager.getSchemaManager().getElement(
				bm_model);
		Iterator arrays = model_em.getAllArrays().iterator();
		String TabHeighGrab = "     ";
		for (int i = 0; arrays.hasNext(); i++) {
			Array array = (Array) arrays.next();
			CompositeMap array_data = data.getChild(array.getLocalName());

			if (array_data == null) {
				String name = array.getLocalName();
				array_data = new CompositeMap(data.getPrefix(), data
						.getNamespaceURI(), name);
				array_data.setParent(data);
			}

			IType type = array.getElementType();
			if (!(type instanceof Element)) {
				childViews.add(createBaseViewer(TabHeighGrab, i, array,
						array_data));
				continue;
			}
			Element arrayType = LoadSchemaManager.getSchemaManager()
					.getElement(type.getQName());
			if (arrayType.getAllElements().size() > 0) {
				childViews.add(createBaseViewer(TabHeighGrab, i, array,
						array_data));
				continue;
			} else {
				final PropertyGridViewer propertyArrayEditor = new PropertyGridViewer(
						this,false);
				propertyArrayEditor.createEditor(mTabFolder, array_data);

				if (array.getLocalName().equals("primary-key")
						|| array.getLocalName().equals("order-by")) {
					createCustomerActions(propertyArrayEditor);
				}

				mTabFolder.getItem(i).setText(
						TabHeighGrab + array.getLocalName().toUpperCase()
								+ TabHeighGrab);
				mTabFolder.getItem(i).setControl(
						propertyArrayEditor.getControl());
				childViews.add(propertyArrayEditor);
				final int  itemIndex = i;
				mTabFolder.addSelectionListener(new SelectionListener() {

					public void widgetSelected(SelectionEvent e) {
						if(mTabFolder.getSelectionIndex()==itemIndex)
							propertyArrayEditor.packColumns();
					}

					public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
					}
				});
			}
		}
//		mTabFolder.setSelection(0);
		mTabFolder.layout(true);

	}

	private BaseCompositeMapViewer createBaseViewer(String TabHeighGrab, int i,
			Array array, CompositeMap array_data) {
		BaseCompositeMapViewer baseViewer = new BaseCompositeMapViewer(this,
				array_data);
		baseViewer.createFormContent(mTabFolder);
		mTabFolder.getItem(i).setText(
				TabHeighGrab + array.getLocalName().toUpperCase()
						+ TabHeighGrab);
		mTabFolder.getItem(i).setControl(baseViewer.getControl());
		return baseViewer;
	}

	public void createCustomerActions(PropertyGridViewer pae) {
		Element element = LoadSchemaManager.getSchemaManager().getElement(
				pae.getData());
		if (element == null) {
			return;
		}
		if (element.isArray()) {
			Action addAction = new AddFieldAction(pae, data.getChild("fields"),
					pae.getData(), AddPropertyAction
							.getDefaultImageDescriptor(), null);
			Action removeAction = new RemoveElementAction(pae,
					RemovePropertyAction.getDefaultImageDescriptor(), null);
			Action refreshAction = new RefreshAction(pae, RefreshAction
					.getDefaultImageDescriptor(), null);
			pae.setActions(new Action[] { addAction, removeAction,
					refreshAction });
		}
	}

	private CTabFolder createTabFolder(final Composite parent) {
		final CTabFolder tabFolder = new CTabFolder(parent, SWT.NONE
				| SWT.BORDER);
		tabFolder.setMaximizeVisible(true);
		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void minimize(CTabFolderEvent event) {
				tabFolder.setMinimized(true);
				tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						false));
				parent.layout(true);
			}

			public void maximize(CTabFolderEvent event) {
				tabFolder.setMaximized(true);
				sashForm.setMaximizedControl(tabFolder);
				parent.layout(true);
			}

			public void restore(CTabFolderEvent event) {
				tabFolder.setMaximized(false);
				sashForm.setMaximizedControl(null);
				parent.layout(true);
			}
		});
		tabFolder.setSimple(false);
		tabFolder.setTabHeight(23);

		Element model_em = LoadSchemaManager.getSchemaManager().getElement(
				bm_model);
		for (int i = 0; i < model_em.getAllArrays().size(); i++) {
			new CTabItem(tabFolder, SWT.None | SWT.MULTI | SWT.V_SCROLL);
		}
		return tabFolder;
	}

	protected File getFile() {
		IFile ifile = ((IFileEditorInput) getEditor().getEditorInput())
				.getFile();
		String fileName = Common.getIfileLocalPath(ifile);
		return new File(fileName);
	}

	public void doSave(IProgressMonitor monitor) {
		try {
			File file = getFile();
			XMLOutputter.saveToFile(file, data);
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
	}

	public void refresh(boolean dirty) {
		if (dirty) {
			setModify(true);
		}
		for (Iterator iterator = childViews.iterator(); iterator.hasNext();) {
			Object childViewer = iterator.next();
			if (childViewer instanceof IViewer) {
				IViewer iViewer = (IViewer) childViewer;
				iViewer.refresh(false);
			}
		}
		getEditor().editorDirtyStateChanged();

	}

	public void refresh(CompositeMap data) {
		this.data = data;
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

	public CompositeMap getContent() {
		return data;
	}

	public String getFullContent() {
		String encoding = "UTF-8";
		String xml_decl = "<?xml version=\"1.0\" encoding=\"" + encoding
				+ "\"?>\n";
		return xml_decl + XMLOutputter.defaultInstance().toXML(data, true);
	}

	public void setContent(CompositeMap content) {
		this.data = content;
		createContent(shell);

	}
}