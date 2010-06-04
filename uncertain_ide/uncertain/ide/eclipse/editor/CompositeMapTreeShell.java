package uncertain.ide.eclipse.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import uncertain.composite.CompositeMap;
import uncertain.ide.Common;
import uncertain.ide.eclipse.action.ElementSelectionListener;
import uncertain.ide.eclipse.editor.textpage.JavaScriptLineStyler;
import uncertain.ide.eclipse.editor.widgets.PropertyGridViewer;
import uncertain.ide.eclipse.editor.widgets.PropertyHashViewer;
import uncertain.ide.eclipse.editor.widgets.CompositeMapTreeViewer;
import uncertain.schema.SchemaManager;

public class CompositeMapTreeShell implements IViewer {

	protected IViewer mParentViewer;
	protected CompositeMapTreeViewer mServiceTreeEditor;
	CTabFolder mTabFolder;
	PropertyHashViewer mPropertyEditor;
	PropertyGridViewer mPropertyArrayEditor;
	StyledText mInnerText;
	Shell shell;
	protected CompositeMap data;
	CompositeMap mSlectDataCm;
	JavaScriptLineStyler lineStyler = new JavaScriptLineStyler();
	SashForm sashForm ;
	private Label elementDocument;
	public CompositeMapTreeShell(IViewer parent, CompositeMap data){
		mParentViewer = parent;
		this.data = data;
	}
	
	public static void main(String[] args) throws Exception {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
	
		SchemaManager sm = Common.getSchemaManager();
		sm.loadSchemaFromClassPath("aurora.testcase.ui.config.components",
				"sxsd");
		sm.loadSchemaFromClassPath("aurora.testcase.ui.config.service", "sxsd");
		shell.open();
	
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	
		display.dispose();
	}

	public void createFormContent(Shell shell) {
	
		try {
			this.shell = shell;
			createContent(shell);
	
		} catch (Exception e) {
			Common.showExceptionMessageBox(null, e);
		}
	}

	private void createContent(Composite shell) {
	
		sashForm = new SashForm(shell, SWT.HORIZONTAL);
	
		createElementContent(sashForm);
		createProperyContent(sashForm);
		ElementSelectionListener listener =  new ElementSelectionListener(
				mTabFolder, mPropertyEditor, mPropertyArrayEditor,
				mServiceTreeEditor, mInnerText,lineStyler,elementDocument);
		mServiceTreeEditor.addSelectionChangedListener(listener);
		
		sashForm.setWeights(new int[] {40, 60});
	}
	

	protected void createElementContent(Composite mContent) {
		
		ViewForm viewForm = new ViewForm(mContent, SWT.NONE);
		viewForm.setLayout(new FillLayout());
	
		Tree tree = new Tree(viewForm, SWT.NONE);
		mServiceTreeEditor = new CompositeMapTreeViewer(tree, this, data);
	
	
		viewForm.setContent(mServiceTreeEditor.getControl()); // 主体：表格
	
	}

	private void createProperyContent(Composite parent) {
	
		createTabFolder(parent);
		createItem0Page(mTabFolder);
	
		mPropertyArrayEditor = new PropertyGridViewer(this);
		mPropertyArrayEditor.createEditor(mTabFolder);
		// mTabFolder.getItem(1).setControl(
		// mPropertyArrayEditor.getTableViewer().getControl());
	
	}

	private void createItem0Page(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		mPropertyEditor = new PropertyHashViewer(mServiceTreeEditor,sashForm);

		mPropertyEditor.createEditor();
	    elementDocument = new Label(sashForm, SWT.LEFT);
		mTabFolder.getItem(0).setControl(sashForm);
		sashForm.setWeights(new int[] {92, 8});
	}

	private void createTabFolder(final Composite parent) {
			mTabFolder = new CTabFolder(parent, SWT.TOP);
			mTabFolder.setMaximizeVisible(true);
			mTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {    
	            public void minimize(CTabFolderEvent event) {    
	            	mTabFolder.setMinimized(true);    
	            	mTabFolder.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));    
	            	parent.layout(true);//刷新布局    
	            }    
	            public void maximize(CTabFolderEvent event) {    
	            	mTabFolder.setMaximized(true);    
	            	sashForm.setMaximizedControl(mTabFolder);
	            	parent.layout(true);                
	            }    
	            public void restore(CTabFolderEvent event) {    
	            	mTabFolder.setMaximized(false);    
	            	sashForm.setMaximizedControl(null);
	            	parent.layout(true);
	            }      
	        });
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
	//				String oldText = mSlectDataCm.getText();
					String oldText = ((CompositeMap)mServiceTreeEditor.getFocus()).getText();
					if (oldText == null) {
						oldText = "";
					}
					oldText = oldText.trim();
					if (!newText.equals(oldText)) {
	//					mSlectDataCm.setText(newText);
						// makeDirty();
						((CompositeMap)mServiceTreeEditor.getFocus()).setText(newText);
						refresh(true);
					}
	
				}
	
			});
			mTabFolder.setBorderVisible(true);
			mTabFolder.setSimple(false);
			mTabFolder.setTabHeight(20);

			
			CTabItem tabItem1 = new CTabItem(mTabFolder, SWT.None|SWT.MULTI|SWT.V_SCROLL);
			String tab = "  ";
			tabItem1.setText(tab+Common.getString("property.name")+tab);

			CTabItem tabItem2 = new CTabItem(mTabFolder, SWT.None|SWT.MULTI|SWT.V_SCROLL);
			tabItem2.setText(tab+Common.getString("child.list")+tab);
			// icon = Activator.getImageDescriptor("icons/items.gif").createImage();
			// tabItem2.setImage(icon);

			CTabItem tabItem3 = new CTabItem(mTabFolder, SWT.None|SWT.MULTI|SWT.V_SCROLL);
			tabItem3.setText(tab+Common.getString("value")+tab);
			
			// icon =
			// Activator.getImageDescriptor("icons/document.gif").createImage();
			// tabItem3.setImage(icon);
			tabItem3.setControl(mInnerText);

			CTabItem tabItem4 = new CTabItem(mTabFolder, SWT.None|SWT.MULTI|SWT.V_SCROLL);
			tabItem4.setText(tab+Common.getString("editor")+tab);
	
			mTabFolder.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
	
				}
	
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
		}
	private void createStyledText() {
		mInnerText = new StyledText (mTabFolder, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData spec = new GridData();
		spec.horizontalAlignment = GridData.FILL;
		spec.grabExcessHorizontalSpace = true;
		spec.verticalAlignment = GridData.FILL;
		spec.grabExcessVerticalSpace = true;
		mInnerText.setLayoutData(spec);
		mInnerText.addLineStyleListener(lineStyler);
//		text.setEditable(false);
		Color bg = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		mInnerText.setBackground(bg);
		

	}
	public CompositeMapTreeShell() {
		super();
	}

	public void refresh(boolean dirty) {
		String text = shell.getText();
		if(text.indexOf("*")==-1)
			text = "*" + text;
		shell.setText(text);
		if (dirty) {
//			System.out.println("mainform..............");
			mServiceTreeEditor.refresh();
			mPropertyEditor.refresh();
			mPropertyArrayEditor.refresh();
		}	
		mParentViewer.refresh(true);
		
	}

}