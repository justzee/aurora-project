/**
 * 
 */
package uncertain.ide.eclipse.wizards;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.action.CompositeMapAction;
import uncertain.ide.eclipse.editor.bm.GridDialog;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.GridViewer;
import uncertain.ide.eclipse.editor.widgets.IGridViewer;

/**
 * @author linjinxiao
 * 
 */
public class DataSetWizard extends Wizard {

	private CompositeMap parentCM;
	private String prefix;
	private String uri = "http://www.aurora-framework.org/application";
	private String cmName="dataSet";

	private String bmFiles;

	private NavigationPage navigationPage;
	private String nextPageName;
	public DataSetWizard(CompositeMap parentCM, String prefix,
			String uri, String cmName) {
		super();
		this.parentCM= parentCM;
		this.prefix= prefix;
		this.uri= uri;
	}

	public DataSetWizard(CompositeMap parentCM,String bmFiles) {
		super();
		QualifiedName QName = new QualifiedName(uri,cmName);
		this.parentCM = parentCM;
		this.prefix = CompositeMapAction.getContextPrefix(parentCM,QName);
		this.bmFiles = bmFiles;
	}

	public void addPages() {
		if (bmFiles != null) {
			BMSelectionPage bmSelectionPage = new BMSelectionPage();
			bmSelectionPage.setSelection(bmFiles);
			nextPageName = BMSelectionPage.PAGE_NAME;
			addPage(bmSelectionPage);
		} else {
			navigationPage = new NavigationPage();
			navigationPage.setPageComplete(false);
			addPage(navigationPage);
			setForcePreviousAndNextButtons(true);
		}
	}

	public IWizardPage getNextPage(IWizardPage page) {
		if (NavigationPage.PAGE_NAME.equals(page.getName())) {
			if(nextPageName != null){
				WizardPage oldPage =(WizardPage)getPage(nextPageName);
				if(oldPage!= null){
					oldPage.setPageComplete(true);
				}
			}
			nextPageName = navigationPage.getNextPageName();
			WizardPage newPage = (WizardPage)getPage(nextPageName);
			if(newPage != null){
				newPage.setPageComplete(false);
				return newPage;
			}
			if(LoopupCodePage.PAGE_NAME.equals(nextPageName)){
				newPage = new LoopupCodePage();
			}else if(ManualDataSetPage.PAGE_NAME.equals(nextPageName)){
				newPage = new ManualDataSetPage(parentCM);
			}else if(BMSelectionPage.PAGE_NAME.equals(nextPageName)){
				newPage = new BMSelectionPage();
			}
			newPage.setPageComplete(false);
			addPage(newPage);
			return newPage;
		}
		return null;
	}
    public void createPageControls(Composite pageContainer) {
    }

	public boolean performFinish() {
		Set ids = new HashSet();
		CompositeMapAction.collectAttribueValues(ids, "id", parentCM.getRoot());
		WizardPage nextPage = (WizardPage)getPage(nextPageName);
		if (LoopupCodePage.PAGE_NAME.equals(nextPageName)) {
			LoopupCodePage loopupCodePage  = (LoopupCodePage)nextPage;
			CompositeMap child = CompositeMapAction.addElement(parentCM,
					prefix, uri, cmName);
			String loopupCode = loopupCodePage.getLookUpCode();
			child.put("loopupCode", loopupCodePage.getLookUpCode());
			child.put("id", getId(ids, loopupCode));
			return true;
		}
		if (ManualDataSetPage.PAGE_NAME.equals(nextPageName)) {
			ManualDataSetPage manualDataSetPage  = (ManualDataSetPage)nextPage;
			CompositeMap child = CompositeMapAction.addElement(parentCM,
					prefix, uri, cmName);
			child.put("id", manualDataSetPage.getId());
			return true;
		}
		if (BMSelectionPage.PAGE_NAME.equals(nextPageName)) {
			BMSelectionPage bmSelectionPage  = (BMSelectionPage)nextPage;
			String[] names = bmSelectionPage.getSelection().split(",");
			for (int i = 0; i < names.length; i++) {
				CompositeMap child = CompositeMapAction.addElement(parentCM,
						prefix, uri, cmName);
				configCompositeMap(child, names[i], bmSelectionPage
						.getJustForInput(), bmSelectionPage.getFromServer(),
						bmSelectionPage.getCanSave(), ids);
			}
		}
		return true;
	}

	public void configCompositeMap(CompositeMap data, String bmName,
			boolean justForInput, boolean fromServer, boolean canSave, Set ids) {
		String fileName = bmName.substring(bmName.lastIndexOf(".") + 1);
		data.put("href", bmName);
		data.put("id", getId(ids, fileName));
		if (justForInput) {
			data.put("canQuery", "false");
			data.put("canSubmit", "false");
			return;
		} else {
			if (fromServer) {
				data.put("canQuery", "false");
			}
			if (canSave) {
				data.put("canSubmit", "true");
			}
		}
	}

	public String getId(Set list,String bmName){
		String suggestId = bmName+"_ds";
		int i = 1;
		while(list.contains(suggestId)){
			suggestId =  bmName+"_ds"+i;
		}
		list.add(suggestId);
		return suggestId;
	}

	class NavigationPage extends WizardPage {
		public static final String PAGE_NAME = "NavigationPage";
		private String nextPageName;

		protected NavigationPage() {
			super(PAGE_NAME);
			setTitle(LocaleMessage.getString("create.dataset.page"));
		}
		
		public void createControl(Composite parent) {
			Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new GridLayout());

			final Button lookup = new Button(content, SWT.RADIO);
			lookup.setText(LocaleMessage.getString("create.lookup"));
			lookup.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					if (lookup.getSelection()) {
						nextPageName = LoopupCodePage.PAGE_NAME;
					}

				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);

				}
			});

			final Button manual = new Button(content, SWT.RADIO);
			manual.setText(LocaleMessage.getString("manual.creation"));
			manual.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					if (manual.getSelection()) {
						nextPageName = ManualDataSetPage.PAGE_NAME;
					}

				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);

				}
			});

			final Button fromBM = new Button(content, SWT.RADIO);
			fromBM.setText(LocaleMessage.getString("create.from.bm.file"));
			fromBM.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					if (fromBM.getSelection()) {
						nextPageName = BMSelectionPage.PAGE_NAME;
					}

				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);

				}
			});
			nextPageName = ManualDataSetPage.PAGE_NAME;
			getWizard().getNextPage(this);
			setPageComplete(true);
			setControl(content);
		}

		public String getNextPageName() {
			return nextPageName;
		}
	    public IWizardPage getNextPage() {
	        return getWizard().getNextPage(this);
	    }

	}
}

class LoopupCodePage extends WizardPage {
	public static final String PAGE_NAME = "LookupCodePage";
	private String lookupCode;

	protected LoopupCodePage() {
		super(PAGE_NAME);
		setTitle(LocaleMessage.getString("create.lookup"));
	}

	public void createControl(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(new GridLayout(2, false));
		Label label = new Label(content, SWT.CANCEL);
		label.setText(LocaleMessage.getString("please.input.lookupcode"));
		final Text text = new Text(content, SWT.NONE);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (text.getText() != null && !(text.getText().equals(""))) {
					lookupCode = text.getText();
					setErrorMessage(null);
					setPageComplete(true);
				} else {
					setErrorMessage(LocaleMessage.getString("lookupcode.can.not.be.null"));
					setPageComplete(false);
				}

			}
		});
		setControl(content);
	}

	public String getLookUpCode() {
		return lookupCode;
	}

}

class ManualDataSetPage extends WizardPage {
	public static final String PAGE_NAME = "ManualDataSet";
	private String id;
	CompositeMap parentCM;

	protected ManualDataSetPage(CompositeMap parentCM) {
		super(PAGE_NAME);
		setTitle(LocaleMessage.getString("manual.create.dataset.page"));
		this.parentCM = parentCM;
	}

	public void createControl(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(new GridLayout(2, false));
		Label label = new Label(content, SWT.CANCEL);
		label.setText(LocaleMessage.getString("please.input.id"));
		final Text text = new Text(content, SWT.NONE);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (text.getText() != null && !(text.getText().equals(""))) {
					Set ids = new HashSet();
					CompositeMapAction.collectAttribueValues(ids, "id",
							parentCM.getRoot());
					if (ids.contains(text.getText())) {
						setErrorMessage(LocaleMessage.getString("This.id.has.exists.please.change.it"));
						setPageComplete(false);
						return;
					}
					setErrorMessage(null);
					id = text.getText();
					setPageComplete(true);
				} else {
					setErrorMessage(LocaleMessage.getString("id.can.not.be.null"));
					setPageComplete(false);
				}

			}
		});
		setControl(content);
	}

	public String getId() {
		return id;
	}

}

class BMSelectionPage extends WizardPage {
	public static final String PAGE_NAME = "CreateDataSetFromBMPage";
	private Text bmselectionsText;
	private String bmselections;
	private Button justForInput;
	private Button fromServer;
	private Button canSave;

	protected BMSelectionPage() {
		super(PAGE_NAME);
		setTitle(LocaleMessage.getString("create.from.bm.file.page"));
	}

	public void createControl(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(new GridLayout(1, false));
		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		Group bmilesGroup = new Group(content, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		bmilesGroup.setLayout(layout);
		bmilesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bmilesGroup.setText(LocaleMessage.getString("bussiness.model.files"));

		bmselectionsText = new Text(bmilesGroup, SWT.NONE);
		bmselectionsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bmselectionsText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (bmselectionsText.getText() != null
						&& !(bmselectionsText.getText().equals(""))) {
					bmselections = bmselectionsText.getText();
					setErrorMessage(null);
					setPageComplete(true);
				} else {
					setErrorMessage(LocaleMessage.getString("bm.file.selection.can.not.be.null"));
					setPageComplete(false);
				}

			}
		});
		if (bmselections != null) {
			bmselectionsText.setText(bmselections);
		}
		Button uncertainProDirButton = new Button(bmilesGroup, SWT.PUSH);
		uncertainProDirButton.setData(data);
		uncertainProDirButton.setText(LocaleMessage.getString("openBrowse"));
		uncertainProDirButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					fireEvent();
				} catch (Exception e) {
					CustomDialog.showExceptionMessageBox(e);
				}
			}
		});
		justForInput = new Button(content, SWT.CHECK);
		justForInput.setText(LocaleMessage.getString("is.just.for.input"));

		final Group configGroup = new Group(content, SWT.NONE);
		configGroup.setText(LocaleMessage.getString("config.query.and.save"));
		layout = new GridLayout();
		configGroup.setLayout(layout);
		data = new GridData(GridData.FILL_HORIZONTAL);
		configGroup.setLayoutData(data);

		final Group dataQueryGroup = new Group(configGroup, SWT.NONE);
		dataQueryGroup.setText(LocaleMessage.getString("fill.data"));
		data = new GridData(GridData.FILL_HORIZONTAL);
		dataQueryGroup.setLayoutData(data);
		dataQueryGroup.setLayout(layout);
		fromServer = new Button(dataQueryGroup, SWT.RADIO);
		fromServer.setText(LocaleMessage.getString("get.data.from.server"));
		fromServer.setSelection(true);
		final Button fromClient = new Button(dataQueryGroup, SWT.RADIO);
		fromClient.setText(LocaleMessage.getString("get.data.from.ajax"));

		canSave = new Button(configGroup, SWT.CHECK);
		canSave.setText(LocaleMessage.getString("can.save"));
		canSave.setSelection(true);

		justForInput.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (justForInput.getSelection()) {
					configGroup.setEnabled(false);
					fromServer.setSelection(false);
					fromClient.setSelection(false);
					canSave.setSelection(false);
				} else {
					configGroup.setEnabled(true);
					fromServer.setSelection(true);
					fromClient.setSelection(false);
					canSave.setSelection(true);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);

			}
		});
		setControl(content);
	}

	public boolean getJustForInput() {
		return justForInput.getSelection();
	}

	public boolean getFromServer() {
		return fromServer.getSelection();
	}

	public boolean getCanSave() {
		return canSave.getSelection();
	}

	public String getSelection() {
		return bmselections;
	}

	public void setSelection(String bmselections) {
		this.bmselections = bmselections;
	}

	private void fireEvent() throws Exception {

		IEditorInput input = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getEditorInput();
		IFile ifile = ((IFileEditorInput) input).getFile();
		IProject project = ifile.getProject();
		String bmFilesDir = ProjectProperties.getBMBaseDir(project);
		File baseDir = new File(bmFilesDir);
		String fullPath = baseDir.getAbsolutePath();
		CompositeMap bmFiles = getAllBMFiles(baseDir, fullPath);

		String[] columnProperties = { "name", "fullpath" };
		GridViewer grid = new GridViewer(columnProperties,IGridViewer.isAllChecked|IGridViewer.isColumnPacked|IGridViewer.isMulti);
		grid.setData(bmFiles);
		GridDialog dialog = new GridDialog(new Shell(), grid);
		if (dialog.open() == Window.OK) {
			if (dialog.getSelected() != null) {
				CompositeMap data = dialog.getSelected();
				Iterator it = data.getChildsNotNull().iterator();
				StringBuffer sb = new StringBuffer();
				for(;it.hasNext();){
					CompositeMap cm = (CompositeMap)it.next();
					sb.append(cm.getString("fullpath"));
				}
				bmselectionsText.setText(sb.toString());
			}
		}
	}

	private CompositeMap getAllBMFiles(File rootFile, String fullPath) {
		CompositeMap bmFiles = new CompositeMap();
		getChilds(rootFile, bmFiles, fullPath);
		return bmFiles;

	}

	private void getChilds(File file, CompositeMap parent, String fullPath) {
		if (file.isDirectory()) {
			File[] nextLevel = file.listFiles();
			for (int i = 0; i < nextLevel.length; i++) {
				getChilds(nextLevel[i], parent, fullPath);
			}
		} else if (file.getName().toLowerCase().endsWith(".bm")) {
			CompositeMap child = new CompositeMap();
			String fullpath = getClassName(file, fullPath);
			child.put("name", file.getName());
			child.put("fullpath", fullpath);
			parent.addChild(child);
		}
	}

	private String getClassName(File file, String fullpath) {
		String path = file.getPath();
		int end = path.indexOf(".");
		path = path.substring(fullpath.length() + 1, end);
		path = path.replace(File.separatorChar, '.');
		return path;
	}
}
