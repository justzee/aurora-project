/**
 * 
 */
package uncertain.ide;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.QualifiedName;
import uncertain.ide.eclipse.presentation.PreferenceLabelManager;
import uncertain.pkg.PackageManager;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.SchemaManager;

public class Common {

	private final static String resource = "uncertain";
	private static SchemaManager schemaManager;
	private static ResourceBundle resourceBundle = getResourceBundle();
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
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		if (title == null)
			title = getString("messagebox.warning");
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	public static void showErrorMessageBox(String title, String message) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		if (title == null)
			title = getString("messagebox.error");
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	public static int showConfirmDialogBox(String title, String message) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
				| SWT.OK | SWT.CANCEL);
		if (title == null)
			title = getString("messagebox.question");
		messageBox.setText(title);
		messageBox.setMessage(message);
		int buttonID = messageBox.open();
		return buttonID;
	}
	public static String getPrefix(CompositeMap cm, QualifiedName qName){
		if(qName == null){
			return null;
		}
		if(cm == null){
			return qName.getPrefix();
		}
		Map prefix_mapping = CompositeUtil.getPrefixMapping(cm);
		String getNameSpace = qName.getNameSpace();
		Object uri_ot = prefix_mapping.get(getNameSpace);
		if(uri_ot != null)
			return (String)uri_ot;
		else
			return qName.getPrefix();
	}
	public static String getElementFullName(CompositeMap cm, QualifiedName qName){
		String text = null;
		String prefix = getPrefix(cm,qName);
		String localName = qName.getLocalName();
		if(prefix != null)
			text = prefix+":"+localName;
		else
			text = localName;
		return text;
	}


	public Set getMaxOcuss(Element element,SchemaManager manager){
		Set allSonElements = new HashSet();
		Set sonElements = element.getChilds();
		for(Iterator cit = sonElements.iterator(); cit!=null && cit.hasNext();){
			Object node = cit.next();
			if(!(node instanceof ComplexType))
				continue;
			ComplexType ct = (ComplexType)node;
			if (ct instanceof Element) {
				Element new_name = (Element) ct;
				allSonElements.add(new_name);
			}
			else{
				allSonElements.addAll(manager.getElementsOfType(ct));
			}
		}
		List complexTypes = element.getAllExtendedTypes();
		if(complexTypes == null)
			return allSonElements;
		for(Iterator cit = complexTypes.iterator(); cit!=null && cit.hasNext();){
			ComplexType ct = (ComplexType)cit.next();
//			System.out.println("ExtendedTypes:"+ct.getLocalName());
			if (ct instanceof Element) {
				Element new_name = (Element) ct;
				allSonElements.addAll(getMaxOcuss(new_name,manager));
			}
//			else{
//				complexTypes.addAll(manager.getElementsOfType(ct));
//			}
				
		}
		return allSonElements;
	}

	public static SchemaManager getSchemaManager() {
			if (Common.schemaManager != null)
				return Common.schemaManager;
			PackageManager pkgManager = new PackageManager();
			try {
				String sxsdDir = Activator.getDefault().getPreferenceStore()
						.getString(PreferenceLabelManager.SXSD_DIRECTORY);
				if (sxsdDir == null || sxsdDir.equals("")) {
					showSxsdDirHint();
				}
				// 将该目录下面所有的子目录作为package装载
				pkgManager.loadPackgeDirectory(sxsdDir);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
	
			Common.schemaManager = SchemaManager.getDefaultInstance();
			Common.schemaManager.addAll(pkgManager.getSchemaManager());
	//		NameSpace[] nameSpaces = schemaManager.getNameSpaces(); 
	//		for(int i=0;i<schemaManager.)
			return Common.schemaManager;
		}

	private static void showSxsdDirHint() {
		showWarningMessageBox(null, getString("undefined.sxsd.dir"));
	}

	public static SchemaManager refeshSchemaManager() {
	
		PackageManager pkgManager = new PackageManager();
		try {
			String sxsdDir = Activator.getDefault().getPreferenceStore()
					.getString(PreferenceLabelManager.SXSD_DIRECTORY);
			if (sxsdDir == null || sxsdDir.equals("")) {
				Common.showSxsdDirHint();
			}
			// 将该目录下面所有的子目录作为package装载
			pkgManager.loadPackgeDirectory(sxsdDir);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		Common.schemaManager = SchemaManager.getDefaultInstance();
		Common.schemaManager.addAll(pkgManager.getSchemaManager());
	
		return Common.schemaManager;
	}
	/**
	 * Gets a string from the resource bundle.
	 * We don't want to crash because of a missing String.
	 * Returns the key if not found.
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
	public static ResourceBundle getResourceBundle(){
		Locale locale  =  Locale.getDefault();
		resourceBundle = ResourceBundle.getBundle(resource,locale);
		if(resourceBundle != null)
			return resourceBundle;
		resourceBundle = ResourceBundle.getBundle(resource);
		return resourceBundle;
	}

}
