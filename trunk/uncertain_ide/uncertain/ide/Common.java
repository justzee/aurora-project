/**
 * 
 */
package uncertain.ide;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.sql.DataSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.QualifiedName;
import uncertain.core.EngineInitiator;
import uncertain.core.UncertainEngine;
import uncertain.ide.eclipse.preferencepages.SxsdDirectoryPreferencePage;
import uncertain.ocm.IObjectRegistry;
import uncertain.pkg.PackageManager;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.SchemaManager;

public class Common {

	private final static String resource = "uncertain";
	private static SchemaManager schemaManager;
	private static ResourceBundle resourceBundle = getResourceBundle();
	public static String projectFile = "uncertain.properties";
	public static final String uncertain_project_dir = "uncertain_project_dir";
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static String getIfileLocalPath(IFile ifile) {
		String fileFullPath = ifile.getProject().getLocation().toString() + "/"
				+ ifile.getProjectRelativePath().toString();
		return fileFullPath;
	}

	public static void showMessageBox(int style, String title, String message) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, style);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	public static void showWarningMessageBox(String title, String message) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK
				| SWT.APPLICATION_MODAL);
		if (title == null)
			title = getString("messagebox.warning");
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	public static void showErrorMessageBox(String title, String message) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK
				| SWT.APPLICATION_MODAL);
		if (title == null)
			title = getString("messagebox.error");
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	public static void showExceptionMessageBox(String title, Exception e) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK
				| SWT.APPLICATION_MODAL);
		if (title == null)
			title = getString("messagebox.error");
		messageBox.setText(title);
		String message = getExceptionMessage(e);
		if(message != null){
			messageBox.setMessage(message);
			messageBox.open();
		}
		throw new RuntimeException(e);
	}
	public static String getExceptionMessage(Exception e) {
		String message = null;
//		e.printStackTrace(s)printStackTrace();
		if (e.getCause() != null) {
			message = e.getCause().getLocalizedMessage();
			
		} else if (e.getLocalizedMessage() != null) {
			message = e.getLocalizedMessage();
		}
		return message;
	}

	public static int showConfirmDialogBox(String title, String message) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
				| SWT.OK | SWT.CANCEL | SWT.APPLICATION_MODAL);
		if (title == null)
			title = getString("messagebox.question");
		messageBox.setText(title);
		messageBox.setMessage(message);
		int buttonID = messageBox.open();
		return buttonID;
	}

	public static String getPrefix(CompositeMap cm, QualifiedName qName) {
		if (qName == null) {
			return null;
		}
		if (cm == null) {
			return qName.getPrefix();
		}
		Map prefix_mapping = CompositeUtil.getPrefixMapping(cm);
		String getNameSpace = qName.getNameSpace();
		Object uri_ot = prefix_mapping.get(getNameSpace);
		if (uri_ot != null)
			return (String) uri_ot;
		else
			return qName.getPrefix();
	}

	public static String getElementFullName(CompositeMap cm, QualifiedName qName) {
		String text = null;
		String prefix = getPrefix(cm, qName);
		String localName = qName.getLocalName();
		if (prefix != null)
			text = prefix + ":" + localName;
		else
			text = localName;
		return text;
	}

	public Set getMaxOcuss(Element element, SchemaManager manager) {
		Set allChildElements = new HashSet();
		Set childElements = element.getChilds();
		for (Iterator cit = childElements.iterator(); cit != null
				&& cit.hasNext();) {
			Object node = cit.next();
			if (!(node instanceof ComplexType))
				continue;
			ComplexType ct = (ComplexType) node;
			if (ct instanceof Element) {
				Element new_name = (Element) ct;
				allChildElements.add(new_name);
			} else {
				allChildElements.addAll(manager.getElementsOfType(ct));
			}
		}
		List complexTypes = element.getAllExtendedTypes();
		if (complexTypes == null)
			return allChildElements;
		for (Iterator cit = complexTypes.iterator(); cit != null
				&& cit.hasNext();) {
			ComplexType ct = (ComplexType) cit.next();
			// System.out.println("ExtendedTypes:"+ct.getLocalName());
			if (ct instanceof Element) {
				Element new_name = (Element) ct;
				allChildElements.addAll(getMaxOcuss(new_name, manager));
			}
			// else{
			// complexTypes.addAll(manager.getElementsOfType(ct));
			// }

		}
		return allChildElements;
	}

	public static SchemaManager getSchemaManager() {
		if (schemaManager != null)
			return schemaManager;
		PackageManager pkgManager = new PackageManager();
		try {
			String sxsdDir = Activator.getDefault().getPreferenceStore()
					.getString(SxsdDirectoryPreferencePage.SXSD_DIRECTORY);
			if (sxsdDir == null || sxsdDir.equals("")) {
				showSxsdDirHint();
			}
			// 将该目录下面所有的子目录作为package装载
			pkgManager.loadPackgeDirectory(sxsdDir);
		} catch (Exception e) {
			Common.showExceptionMessageBox(null, e);
		}

		schemaManager = SchemaManager.getDefaultInstance();
		schemaManager.addAll(pkgManager.getSchemaManager());
		return schemaManager;
	}

	private static void showSxsdDirHint() {
		showWarningMessageBox(null, getString("undefined.sxsd.dir"));
	}

	public static SchemaManager refeshSchemaManager() {

		PackageManager pkgManager = new PackageManager();
		try {
			String sxsdDir = Activator.getDefault().getPreferenceStore()
					.getString(SxsdDirectoryPreferencePage.SXSD_DIRECTORY);
			if (sxsdDir == null || sxsdDir.equals("")) {
				Common.showSxsdDirHint();
			}
			pkgManager.loadPackgeDirectory(sxsdDir);
			schemaManager = new SchemaManager();
			String pkg_name = SchemaManager.class.getPackage().getName();
		    String schema_name = pkg_name + ".SchemaForSchema";
		    schemaManager.loadSchemaFromClassPath(schema_name);
			schemaManager.addAll(pkgManager.getSchemaManager());
		} catch (Exception e) {
			Common.showExceptionMessageBox(null, e);
		}
		return schemaManager;
	}

	/**
	 * Gets a string from the resource bundle. We don't want to crash because of
	 * a missing String. Returns the key if not found.
	 */
	public static String getString(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		} catch (NullPointerException e) {
			return "!" + key + "!";
		}
	}

	public static ResourceBundle getResourceBundle() {
		Locale locale = Locale.getDefault();
		resourceBundle = ResourceBundle.getBundle(resource, locale);
		if (resourceBundle != null)
			return resourceBundle;
		resourceBundle = ResourceBundle.getBundle(resource);
		return resourceBundle;
	}
	public static Connection getDBConnection(String projectFullPath) throws Exception {
		File home_path = new File(projectFullPath);
		File config_path = new File(home_path,"WEB-INF");
		EngineInitiator ei = new EngineInitiator(home_path, config_path);       
		ei.init();
		UncertainEngine mUncertainEngine = ei.getUncertainEngine();
		IObjectRegistry mObjectRegistry = mUncertainEngine.getObjectRegistry();
		DataSource ds = (DataSource)mObjectRegistry.getInstanceOfType(DataSource.class);
		Connection conn = ds.getConnection();
		return conn;
	}
	public static Connection getDBConnection(IProject project) throws Exception {
		IFile file = project.getFile(Common.projectFile);
		if(!file.exists()){
			throw new RuntimeException("Please define the "+Common.projectFile+" file first !");
		}
		String fileFullPath = Common.getIfileLocalPath(file);
		File root = new File(fileFullPath);
		Properties props = new Properties();
		props.load(new FileInputStream(root));
		String uncertain_project_dir = (String)props.get(Common.uncertain_project_dir);
		return getDBConnection(uncertain_project_dir);
	}
	
}
