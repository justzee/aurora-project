package uncertain.ide.eclipse.editor.bm;

import java.io.File;
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
import uncertain.ide.Common;
import uncertain.ide.eclipse.action.AddFieldAction;
import uncertain.ide.eclipse.action.AddPropertyAction;
import uncertain.ide.eclipse.action.RefreshAction;
import uncertain.ide.eclipse.action.RemoveElementAction;
import uncertain.ide.eclipse.action.RemovePropertyAction;
import uncertain.ide.eclipse.editor.IViewer;
import uncertain.ide.eclipse.editor.widgets.PropertyGridViewer;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.editor.AttributeValue;
import uncertain.schema.editor.CompositeMapEditor;

public class BussinessModelPage extends FormPage implements IViewer {
	private static final String PageId = "BussinessModelPage";
	private static final String PageTitle = "Bussiness Model";
	private CTabFolder mTabFolder;
	private CompositeMap data;
	private SashForm sashForm;
	private boolean modify = false;
	
	private QualifiedName bm_model = new QualifiedName("http://www.aurora-framework.org/schema/bm", "model");
	
	public BussinessModelPage(FormEditor editor) {
		super(editor,PageId, PageTitle);
	}
	
	public BussinessModelPage(String id, String title) {
		super(id, title);
	}

	public BussinessModelPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite shell = form.getBody();
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);
		Element schemaElement = Common.getSchemaManager().getElement(bm_model);
		if(schemaElement == null){
			throw new RuntimeException("Please add the bm schema file first!");
		}
		try {
			CompositeLoader loader = new CompositeLoader();
			data = loader.loadByFile(getFile().getAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage(),e.getCause());
		}	
		if(!data.getQName().equals(bm_model))
			throw new RuntimeException("this root element is not "+bm_model+" !");
		createContent(shell, toolkit);
	}

	protected void createContent(Composite shell, FormToolkit toolkit) {
		sashForm = new SashForm(shell, SWT.VERTICAL);
		createMasterContent(sashForm, toolkit);
		createDetailContent(sashForm);
		sashForm.setWeights(new int[] { 30, 70 });
	}
	protected void createMasterContent(Composite parent, FormToolkit toolkit) {
		Group textGroup = new Group (parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout (3,false);
		textGroup.setLayout (gridLayout);
		GridData gd = new GridData (SWT.FILL, SWT.FILL, true, true);
		textGroup.setLayoutData (gd);
		textGroup.setText ("Table");
		
        CompositeMapEditor editor = new CompositeMapEditor(Common.getSchemaManager(), data);
        AttributeValue[] avs =  editor.getAttributeList();
		for(int i=0;i<avs.length;i++){
			AttributeValue av =  avs[i];
			Attribute attr = av.getAttribute();
			String attrName = attr.getLocalName();
			String attrValue = av.getValueString();
			String attrDocument = attr.getDocument();
			
			Label attrNamLabel = new Label(textGroup,SWT.NONE);
			attrNamLabel.setText(attrName);
			Text attrValueText = new Text(textGroup,SWT.NONE);
			attrValueText.setText(attrValue);
			GridData attrValueText_gd = new GridData (200, 20);
			attrValueText.setLayoutData(attrValueText_gd);
			Label attrDocumentLabel = new Label(textGroup,SWT.NONE);
			attrDocumentLabel.setText(attrDocument);
		}

	}

	private void createDetailContent(Composite parent) {
		mTabFolder = createTabFolder(parent);
		Element model_em = Common.getSchemaManager().getElement(bm_model);
		Iterator arrays = model_em.getAllArrays().iterator();
		String TabHeighGrab = "           ";
		for(int i=0;arrays.hasNext();i++){
			Array array = (Array)arrays.next();
			PropertyGridViewer propertyArrayEditor = new PropertyGridViewer(this);
			CompositeMap array_data = data.getChild(array.getLocalName());
			
			if(array_data == null){
				String name = array.getLocalName();
				array_data = new CompositeMap(data.getPrefix(),
						data.getNamespaceURI(), name);
				array_data.setParent(data);
			}
			
			
			propertyArrayEditor.createEditor(mTabFolder,array_data);
			
			if(array.getLocalName().equals("primary-key")||array.getLocalName().equals("order-by")){
				createCustomerActions(propertyArrayEditor);
			}
			
			mTabFolder.getItem(i).setText(TabHeighGrab+array.getLocalName().toUpperCase()+TabHeighGrab);
			mTabFolder.getItem(i).setControl(propertyArrayEditor.getControl());
		}
		mTabFolder.setSelection(0);
		mTabFolder.layout(true);	

	}
	public void createCustomerActions(PropertyGridViewer pae) {
		Element element = Common.getSchemaManager().getElement(pae.getData());
		if (element == null) {
			return;
		}
		if (element.isArray()) {
			Action addAction = new AddFieldAction(pae, data.getChild("fields"), pae.getData(),
					AddPropertyAction.getDefaultImageDescriptor(), null);
			Action removeAction = new RemoveElementAction(pae,
					RemovePropertyAction.getDefaultImageDescriptor(), null);
			Action refreshAction = new RefreshAction(pae, RefreshAction
					.getDefaultImageDescriptor(), null);
			pae.setActions(new Action[]{addAction,removeAction,refreshAction});
		}
	}
	
	
	private CTabFolder createTabFolder(final Composite parent) {
		final CTabFolder tabFolder = new CTabFolder(parent, SWT.NONE|SWT.BORDER);
		tabFolder.setMaximizeVisible(true);
		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void minimize(CTabFolderEvent event) {
				tabFolder.setMinimized(true);
				tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						false));
				parent.layout(true);// Ë¢ÐÂ²¼¾Ö
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

		Element model_em = Common.getSchemaManager().getElement(bm_model);
		for(int i=0;i<model_em.getAllArrays().size();i++){
			 new CTabItem(tabFolder, SWT.None | SWT.MULTI
					| SWT.V_SCROLL);
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
			Common.showExceptionMessageBox(null, e);
		}
	}

	public void refresh(boolean dirty) {
		if (dirty) {
			setModify(true);
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
}