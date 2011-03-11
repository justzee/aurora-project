package uncertain.ide.eclipse.bm.wizard.db;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import aurora.ide.AuroraConstant;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.bm.AuroraDataBase;
import uncertain.ide.eclipse.bm.BMUtil;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.LocaleMessage;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "bm". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class BMFromDBWizard extends Wizard implements INewWizard {
	
	private BMMainConfigPage mainConfigPage;
	private BMTablePage tablePage;
	private BMFieldsPage fieldsPage;
	private ISelection selection;
	private CompositeMap initContent;
	
	/**
	 * Constructor for BMFromDBWizard
	 */
	public BMFromDBWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		mainConfigPage = new BMMainConfigPage(selection,this);
		tablePage= new BMTablePage(selection,this);
		fieldsPage = new BMFieldsPage(selection,this);
		fieldsPage.setPageComplete(false);
		addPage(mainConfigPage);
		addPage(tablePage);
		addPage(fieldsPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String containerName = mainConfigPage.getContainerName();
		final String fileName = mainConfigPage.getFileName();
		initContent = createInitContent();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		String containerName,
		String fileName,
		IProgressMonitor monitor)
		throws CoreException {
		
		if(fileName.indexOf(".")==-1){
			fileName = fileName+".bm";
		}
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			CustomDialog.showErrorMessageBox(LocaleMessage.getString("container")+" \"" + containerName + "\""+LocaleMessage.getString("not.exist"));
			return;
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			InputStream stream = openContentStream();
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	
	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream() {
		String xmlHint = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String contents =xmlHint+initContent.toXML();
		return new ByteArrayInputStream(contents.getBytes());
	}
	private CompositeMap createInitContent() {

		CompositeMap model = new CompositeMap(BMUtil.BMPrefix,AuroraConstant.BMUri,"model");
		model.put("baseTable", getTableName());
		model.put("alias", "t1");
		addFieldsAndFeatures(model);
		try {
			CompositeMap pks = getPrimaryKeys();
			if(pks != null && pks.getChilds() != null){
				model.addChild(pks);
			}
		} catch (SQLException e) {
			CustomDialog.showErrorMessageBox(e);
		}
		return model;
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	public String getTableName(){
		return tablePage.getTableName();
	}
	public DatabaseMetaData getDBMetaData(){
		return tablePage.getDBMetaData();
	}
	public CompositeMap getPrimaryKeys() throws SQLException{
		return tablePage.getPrimaryKeys();
	}
	public void createPageControls(Composite pageContainer) {
		// super.createPageControls(pageContainer); 
	}
	private CompositeMap addFieldsAndFeatures(CompositeMap model){
		if(model == null)
			return null;
		CompositeMap features = new CompositeMap(BMUtil.BMPrefix,AuroraConstant.BMUri,"features");
		CompositeMap standardWho = new CompositeMap(BMUtil.FeaturesPrefex,BMUtil.FeaturesUri,"standard-who"); 
		features.addChild(standardWho);
		CompositeMap fields =  fieldsPage.getSelectedFields();
		//handle multi language
		String descIdFieldName = "description_id";
		if(fields == null)
			return model;
		model.addChild(fields);
		model.addChild(features);
		CompositeMap descIdField = fields.getChildByAttrib("name", descIdFieldName);
		if(descIdField == null)
			return model;
		descIdField.put("multiLanguage", "true");
		descIdField.put("multiLanguageDescField", "description");
		CompositeMap descField = new CompositeMap(fields.getPrefix(),fields.getNamespaceURI(),"field");
		descField.put("name", "description");
		descField.put("databaseType", "VARCHAR");
		descField.put("datatype", "java.lang.String");
		fields.addChild(descField);
		CompositeMap multiLanguage = new CompositeMap(BMUtil.FeaturesPrefex,BMUtil.FeaturesUri,"multi-language-storage");
		features.addChild(multiLanguage);
		return model;
	}
	public Connection getConnection() throws ApplicationException{
		String containerName = mainConfigPage.getContainerName();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throw new ApplicationException(LocaleMessage.getString("container")+" \"" + containerName + "\""+LocaleMessage.getString("not.exist"));
		}
		AuroraDataBase ad = new AuroraDataBase(resource.getProject());
		Connection conn = ad.getDBConnection();
		return conn;
	}
	public void refresh() throws ApplicationException{
		if(fieldsPage.getControl() != null )
			fieldsPage.refresh();
	}
}