package uncertain.ide.eclipse.editor.service;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.action.ElementSelectionListener;
import uncertain.ide.eclipse.action.IDirty;
import uncertain.schema.SchemaManager;
import aurora_ide.Activator;

public class ServiceShell implements IDirty {

	IDirty mDirtyAction;

	public ServiceShell(IDirty DirtyAction, CompositeMap data) {
		this.data = data;
	}

	// Composite mContent;
	ServiceTreeEditor mServiceTreeEditor;
	TabFolder mTabFolder;
	ServicePropertyEditor mPropertyEditor;
	ServicePropertyArrayEditor mPropertyArrayEditor;
	Text mInnerText;
	Shell shell;
	CompositeMap data;
	CompositeMap mSlectDataCm;

	public void createFormContent(Shell shell) {

		try {
			this.shell = shell;
			createContent(shell);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createContent(Composite shell) {

		SashForm sashForm = new SashForm(shell, SWT.HORIZONTAL);

		createElementContent(sashForm);
		createProperyContent(sashForm);
		ElementSelectionListener listener =  new ElementSelectionListener(
				mTabFolder, mPropertyEditor, mPropertyArrayEditor,
				mServiceTreeEditor, mInnerText);
		mServiceTreeEditor.addSelectionChangedListener(listener);
	}

	private void createElementContent(Composite mContent) {

		ViewForm viewForm = new ViewForm(mContent, SWT.NONE);
		viewForm.setLayout(new FillLayout());

		Tree tree = new Tree(viewForm, SWT.NONE);
		mServiceTreeEditor = new ServiceTreeEditor(tree, this, data);


		viewForm.setContent(mServiceTreeEditor.getControl()); // 主体：表格

	}

	private void createProperyContent(Composite parent) {

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
				String oldText = mSlectDataCm.getText();
				if (oldText == null) {
					oldText = "";
				}
				oldText = oldText.trim();
				if (!newText.equals(oldText)) {
					mSlectDataCm.setText(newText);
					// makeDirty();
				}

			}

		});
		TabItem tabItem1 = new TabItem(mTabFolder, SWT.NULL);
		tabItem1.setText("属性");
		icon = Activator.getImageDescriptor("icons/property.gif").createImage();
		tabItem1.setImage(icon);
		// tabItem1.setControl(mPropertyEditor.viewForm);

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

	class ElementDoubleClickListener implements IDoubleClickListener {

		public void doubleClick(DoubleClickEvent event) {
			TreeSelection selection = (TreeSelection) event.getSelection();
			CompositeMap data = (CompositeMap) selection.getFirstElement();

		}

	}

	public static void main(String[] args) throws Exception {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		SchemaManager sm = Activator.getSchemaManager();
		sm.loadSchemaFromClassPath("aurora.testcase.ui.config.components",
				"sxsd");
		sm.loadSchemaFromClassPath("aurora.testcase.ui.config.service", "sxsd");

		CompositeLoader loader = new CompositeLoader();
		CompositeMap data = loader
				.loadFromClassPath("uncertain.testcase.schema.screen_test");
		// data = loader.loadByFile(getFile().getAbsolutePath());
		System.out.println(data.toXML());

		ServiceShell editor = new ServiceShell(null, data);
		// editor.createFormContent(shell,data);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}


	public void makeDirty() {
		String text = shell.getText();
		text = "*" + text;
		shell.setText(text);
	}

	public void setDirty(boolean dirty) {
		// TODO Auto-generated method stub
		
	}

}
