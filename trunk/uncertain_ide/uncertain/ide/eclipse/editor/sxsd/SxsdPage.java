package uncertain.ide.eclipse.editor.sxsd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.action.IDirty;
import uncertain.schema.Element;
import uncertain.schema.SchemaManager;
import aurora_ide.Activator;

public class SxsdPage extends FormPage implements IDirty{
	private static final String PageId = "SxsdTreePage";
	private static final String PageTitle = "Simple XML Schema";
	public static String namespacePrefix;
	public static String namespaceUrl;

	public SxsdPage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}

	TabFolder mTabFolder;
	SxsdPropertyEditor mPropertyEditor;
	SxsdPropertyArrayEditor mPropertyArrayEditor;
	CompositeMap data;
	Text mInnerText;

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite shell = form.getBody();
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);

		try {

			CompositeLoader loader = new CompositeLoader();
			data = loader.loadByFile(getFile().getAbsolutePath());
			namespacePrefix = data.getPrefix();
			namespaceUrl = data.getNamespaceURI().toString();
			
			createContent(shell, toolkit);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createContent(Composite shell, FormToolkit toolkit) {

		SashForm sashForm = new SashForm(shell, SWT.HORIZONTAL);

		createElementContent(sashForm, toolkit);
		createProperyContent(sashForm);

	}

	private void createElementContent(Composite mContent, FormToolkit toolkit) {

		ViewForm viewForm = new ViewForm(mContent, SWT.NONE);
		viewForm.setLayout(new FillLayout());

		Tree tree = toolkit.createTree(viewForm, SWT.NONE);
		TreeViewer mTreeViewer = new TreeViewer(tree);
		mTreeViewer.setLabelProvider(new SxsdTreeLabelProvider());
		mTreeViewer.setContentProvider(new SxsdTreeContentProvider());
		mTreeViewer.addSelectionChangedListener(new ElementSelectionListener());
		mTreeViewer.setInput(data);
		viewForm.setContent(mTreeViewer.getControl()); // 主体：表格

		// 响应事件
		SxsdActionGroup treeActionGroup = new SxsdActionGroup(mTreeViewer, this);

		treeActionGroup.fillElementToolBar(viewForm);
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		treeActionGroup.fillContextMenu(menuMgr);
		treeActionGroup.fillKeyListener();
		treeActionGroup.fillDNDListener();

	}

	private void createTabFolder(Composite parent) {
		Image icon = null;
		mTabFolder = new TabFolder(parent, SWT.TOP);
		mInnerText = new Text(mTabFolder, SWT.NULL);

		TabItem tabItem1 = new TabItem(mTabFolder, SWT.NULL);
		tabItem1.setText("属性");
		icon = Activator.getImageDescriptor("icons/property.gif").createImage();
		tabItem1.setImage(icon);
//		tabItem1.setControl(mPropertyEditor.viewForm);

		TabItem tabItem2 = new TabItem(mTabFolder, SWT.NULL);
		tabItem2.setText("子项");
		icon = Activator.getImageDescriptor("icons/items.gif").createImage();
		tabItem2.setImage(icon);

		TabItem tabItem3 = new TabItem(mTabFolder, SWT.NULL);
		tabItem3.setText("值");
		icon = Activator.getImageDescriptor("icons/document.gif").createImage();
		tabItem3.setImage(icon);
		tabItem3.setControl(mInnerText);

		TabItem tabItem4 = new TabItem(mTabFolder, SWT.NULL);
		tabItem4.setText("编辑器");
		icon = Activator.getImageDescriptor("icons/editor.gif").createImage();
		tabItem4.setImage(icon);

		mTabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	private void createProperyContent(Composite parent) {

		createTabFolder(parent);
		mPropertyEditor = new SxsdPropertyEditor(Activator.getSchemaManager(),
				this,this);
		mPropertyEditor.createEditor(mTabFolder);
		mTabFolder.getItem(0).setControl(mPropertyEditor.viewForm);
		
		mPropertyArrayEditor = new SxsdPropertyArrayEditor(Activator.getSchemaManager(),
				this);
		mPropertyArrayEditor.createEditor(mTabFolder);
//		mTabFolder.getItem(1).setControl(
//				mPropertyArrayEditor.getTableViewer().getControl());

	}

	public class ElementSelectionListener implements ISelectionChangedListener {

		public void selectionChanged(SelectionChangedEvent event) {
			TreeSelection selection = (TreeSelection) event.getSelection();
			CompositeMap data = (CompositeMap) selection.getFirstElement();

			// System.out.println(data.toXML());

			if (data == null)
				return;
			Element em = Activator.getSchemaManager().getElement(data);
			if (em != null && em.isArray()) {
				mPropertyEditor.clearAll();
				mPropertyArrayEditor.createEditor(mTabFolder, data);
				mTabFolder.getItem(1).setControl(
						mPropertyArrayEditor.getTableViewer().getControl());
				mTabFolder.setSelection(1);
				mTabFolder.layout(true);

			} else {
				mPropertyArrayEditor.clearAll();
				mPropertyEditor.createEditor(mTabFolder,data);
				mTabFolder.getItem(0).setControl(mPropertyEditor.viewForm);
				mTabFolder.setSelection(0);
				mTabFolder.layout(true);

			}
			String a = data.getText();
			if (a != null && !a.trim().equals("")) {
				mInnerText.setText(data.getText());
				mTabFolder.setSelection(2);
				mTabFolder.layout(true);
			} else {
				mInnerText.setText("");
			}
		}

	}

	private File getFile() {
		return ((SxsdEditor) getEditor()).getFile();
	}


	public SxsdEditor getPageEditor() {
		return (SxsdEditor) getEditor();
	}

	public void doSave(IProgressMonitor monitor) {
		try {

			File file = ((SxsdEditor) getEditor()).getFile();
			PrintStream out = new PrintStream(new FileOutputStream(file));
			out.println(data.toXML());
			out.close();
			setDirty(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDirty(boolean dirty) {
		getPageEditor().setDirty(dirty);
	}

	public void makeDirty() {
		setDirty(true);
	}

}
