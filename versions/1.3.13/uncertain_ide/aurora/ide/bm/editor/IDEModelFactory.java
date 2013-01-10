/**
 * 
 */
package aurora.ide.bm.editor;


import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;
import aurora.bm.BusinessModel;
import aurora.bm.ModelFactory;
import aurora.ide.api.composite.map.CommentCompositeLoader;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ProjectUtil;


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
			CompositeLoader loader = CommentCompositeLoader.createInstanceForOCM();
		    String fullPath = ProjectUtil.getBMHomeLocalPath(ProjectUtil.getIProjectFromSelection()) + File.separator + filePath;
		    CompositeMap config = loader.loadByFullFilePath(fullPath);
		    if (config == null)
		        throw new IOException("Can't load resource " + name);
		    model = createBusinessModelInternal(config);
		    model.setName(name);
		} catch (IOException e) {
			DialogUtil.logErrorException(e);
			return null;
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return null;
		} catch (SAXException e) {
			DialogUtil.showExceptionMessageBox(e);
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
