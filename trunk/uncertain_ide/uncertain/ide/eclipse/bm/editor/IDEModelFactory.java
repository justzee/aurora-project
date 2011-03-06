/**
 * 
 */
package uncertain.ide.eclipse.bm.editor;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.project.propertypage.ProjectPropertyPage;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.AuroraResourceUtil;
import uncertain.ide.help.CustomDialog;
import uncertain.ocm.OCManager;
import aurora.bm.BusinessModel;
import aurora.bm.ModelFactory;


/**
 * @author linjinxiao
 *
 */
public class IDEModelFactory extends ModelFactory {

	public IDEModelFactory(OCManager ocm) {
		super(ocm);
	}

	protected BusinessModel getNewModelInstance(String name, String ext){
		BusinessModel model = null;
		if (name == null)
		    throw new IllegalArgumentException("model name is null");
		try {
			String filePath = convertResourcePath(name,ext);
			CompositeLoader loader = CompositeLoader.createInstanceForOCM();
		    String fullPath = ProjectPropertyPage.getBMBaseLocalDir(AuroraResourceUtil.getIProjectFromSelection()) + File.separator + filePath;
		    CompositeMap config = loader.loadByFullFilePath(fullPath);
		    if (config == null)
		        throw new IOException("Can't load resource " + name);
		    model = createBusinessModelInternal(config);
		    model.setName(name);
		} catch (IOException e) {
			CustomDialog.showErrorMessageBox(e);
			return null;
		} catch (ApplicationException e) {
			CustomDialog.showErrorMessageBox(e);
			return null;
		} catch (SAXException e) {
			CustomDialog.showErrorMessageBox(e);
			return null;
		}
		return model;
	}
    public String convertResourcePath( String path, String file_ext ){
        return path.replace('.', '/') +'.' + file_ext;
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
