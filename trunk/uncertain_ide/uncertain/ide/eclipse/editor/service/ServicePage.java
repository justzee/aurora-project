package uncertain.ide.eclipse.editor.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.action.CompositeMapAction;
import uncertain.ide.eclipse.action.ElementSelectionListener;
import uncertain.ide.eclipse.action.IDirty;
import uncertain.ide.eclipse.action.IViewerDirty;
import uncertain.ide.eclipse.action.ToolBarAddElementListener;
import aurora_ide.Activator;
import aurora_ide.Common;

public class ServicePage extends FormPage implements IDirty {
	private static final String PageId = "ServicePage";
	private static final String PageTitle = "SERVICE File";

	public ServicePage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}

	ServiceTreeEditor mServiceTreeEditor;
	TabFolder mTabFolder;
	ServicePropertyEditor mPropertyEditor;
	ServicePropertyArrayEditor mPropertyArrayEditor;
	Text mInnerText;
	CompositeMap data;

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite shell = form.getBody();
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);

		try {

			CompositeLoader loader = new CompositeLoader();
			data = loader.loadByFile(getFile().getAbsolutePath());
			// System.out.println(data.toXML());
			autoLoadProjectSxsdFile();
			createContent(shell, toolkit);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void autoLoadProjectSxsdFile() {

		File project = ((IFileEditorInput) getEditorInput()).getFile()
				.getProject().getLocation().toFile();
		if (project != null && project.isDirectory()) {
			File[] files = project.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.getName().toLowerCase().endsWith(".sxsd")) {
					try {
						Activator.getSchemaManager().loadSchemaByFile(
								file.getAbsolutePath());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		// System.out.println("rootFile:"+rootFile );
	}

	private void createContent(Composite shell, FormToolkit toolkit) {

		SashForm sashForm = new SashForm(shell, SWT.HORIZONTAL);

		createElementContent(sashForm, toolkit);
		createPropertyContent(sashForm);
		mServiceTreeEditor
		.addSelectionChangedListener(new ElementSelectionListener(
				mTabFolder, mPropertyEditor, mPropertyArrayEditor,
				mServiceTreeEditor, mInnerText));


	}

	private void createElementContent(Composite mContent, FormToolkit toolkit) {

		ViewForm viewForm = new ViewForm(mContent, SWT.NONE);
		viewForm.setLayout(new FillLayout());

		Tree tree = toolkit.createTree(viewForm, SWT.NONE);
		mServiceTreeEditor = new ServiceTreeEditor(tree, this, data);


		viewForm.setContent(mServiceTreeEditor.getControl()); // 主体：表格
		
		fillElementToolBar(viewForm,mServiceTreeEditor);

	}

	private void createPropertyContent(Composite parent) {

		createTabFolder(parent);
		mPropertyEditor = new ServicePropertyEditor(Activator
				.getSchemaManager(), mServiceTreeEditor);
		mPropertyEditor.createEditor(mTabFolder);
		mTabFolder.getItem(0).setControl(mPropertyEditor.viewForm);

		mPropertyArrayEditor = new ServicePropertyArrayEditor(Activator
				.getSchemaManager(),mServiceTreeEditor);
		mPropertyArrayEditor.createEditor(mTabFolder);
		// mTabFolder.getItem(1).setControl(
		// mPropertyArrayEditor.getTableViewer().getControl());

	}

	private void createTabFolder(Composite parent) {
		Image icon = null;
		mTabFolder = new TabFolder(parent, SWT.TOP);
		mInnerText = new Text(mTabFolder, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
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
				String oldText = mServiceTreeEditor.getFocusData().getText();
				if (oldText == null) {
					oldText = "";
				}
				oldText = oldText.trim();
				if (!newText.equals(oldText)) {
					mServiceTreeEditor.getFocusData().setText(newText);
					makeDirty();
				}

			}

		});
		TabItem tabItem1 = new TabItem(mTabFolder, SWT.NULL);
		tabItem1.setText("属性");
		icon = Activator.getImageDescriptor("icons/property.gif").createImage();
//		tabItem1.setImage(icon);
		// tabItem1.setControl(mPropertyEditor.viewForm);

		TabItem tabItem2 = new TabItem(mTabFolder, SWT.NULL);
		tabItem2.setText("子项");
//		icon = Activator.getImageDescriptor("icons/items.gif").createImage();
//		tabItem2.setImage(icon);

		TabItem tabItem3 = new TabItem(mTabFolder, SWT.NULL);
		tabItem3.setText("值");
//		icon = Activator.getImageDescriptor("icons/document.gif").createImage();
//		tabItem3.setImage(icon);
		tabItem3.setControl(mInnerText);

		TabItem tabItem4 = new TabItem(mTabFolder, SWT.NULL);
		tabItem4.setText("编辑器");
		icon = Activator.getImageDescriptor("icons/editor.gif").createImage();
//		tabItem4.setImage(icon);

		mTabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}



	private File getFile() {
		IFile ifile = ((IFileEditorInput)getEditor().getEditorInput()).getFile();
		String fileName = Common.getIfileLocalPath(ifile);
		return new File(fileName);
	}

	public void changeSelection(CompositeMap newSelection) {
		mPropertyEditor.setData(newSelection);
	}

	public void doSave(IProgressMonitor monitor) {
		try {
			File file = getFile();
			PrintStream out = new PrintStream(new FileOutputStream(file));
			out.println("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
			String content = data.toXML();// new
			// String(data.toXML().getBytes("GBK"),
			// "UTF-8");
			// System.out.println(content);
			out.println(content);
			out.close();
			setDirty(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDirty(boolean dirty) {
//		System.out.println("is dirty:"+getEditor().isDirty());
		if (!dirty && getEditor().isDirty()) {
			getEditor().editorDirtyStateChanged();
		} else if (dirty && !(getEditor().isDirty())) {
			getEditor().editorDirtyStateChanged();
		}
//		getEditor().editorDirtyStateChanged();
//		((ServiceEditor)getEditor()).makeDirty();
//		System.out.println("is dirty:"+getEditor().isDirty());
	}

	public void makeDirty() {
		setDirty(true);
	}
	public void fillElementToolBar(Composite shell,final IViewerDirty columnViewerDirtyObject) {
	
			ToolBar toolBar = new ToolBar(shell, SWT.RIGHT | SWT.FLAT);
			// 创建一个toolBar的管理器
			Menu menu = new Menu(shell);
	
			ToolItem addItem = new ToolItem(toolBar, SWT.DROP_DOWN);
			setToolItemShowProperty(addItem, "添加子节点", "icons/add_obj.gif");
			addItem.addListener(SWT.Selection, new ToolBarAddElementListener(
					toolBar, menu, addItem, columnViewerDirtyObject.getObject(), columnViewerDirtyObject));
	
			final ToolItem removeItem = new ToolItem(toolBar, SWT.PUSH);
			setToolItemShowProperty(removeItem, "删除", "icons/delete_obj.gif");
			removeItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					CompositeMapAction.removeElement(columnViewerDirtyObject);
	
				}
			});
	
			final ToolItem cutItem = new ToolItem(toolBar, SWT.PUSH);
			setToolItemShowProperty(cutItem, "剪切", "icons/cut.gif");
			cutItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					CompositeMapAction.cutElement(columnViewerDirtyObject);
				}
			});
	
			final ToolItem copyItem = new ToolItem(toolBar, SWT.PUSH);
			setToolItemShowProperty(copyItem, "复制", "icons/copy.gif");
			copyItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					CompositeMapAction.copyElement(columnViewerDirtyObject);
				}
			});
	
			final ToolItem pasteItem = new ToolItem(toolBar, SWT.PUSH);
			setToolItemShowProperty(pasteItem, "粘贴", "icons/paste.gif");
			pasteItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					CompositeMapAction.pasteElement(columnViewerDirtyObject);
				}
			});
	
			toolBar.pack();
			((ViewForm) shell).setTopLeft(toolBar); // 顶端边缘：工具栏
		}
	
	private void setToolItemShowProperty(ToolItem toolItem, String text,
			String iconPath) {
		if (text != null && !text.equals(""))
			toolItem.setText(text);
		if (iconPath != null && !iconPath.equals("")) {
			Image icon = Activator.getImageDescriptor(iconPath).createImage();
			toolItem.setImage(icon);
		}

	}
}
