/**
 * 
 */
package uncertain.ide.eclipse.editor.bm;

import java.io.File;
import java.io.IOException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.wizards.ProjectProperties;
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
//		    CompositeMap config = mCompositeLoader.loadFromClassPath(name, ext);
			String filePath = convertResourcePath(name,ext);
			CompositeLoader loader = CompositeLoader.createInstanceForOCM();
		    String fullPath = ProjectProperties.getBMBaseDir() + File.separator + filePath;
		    CompositeMap config = loader.loadByFullFilePath(fullPath);
		    if (config == null)
		        throw new IOException("Can't load resource " + name);
		    model = createBusinessModelInternal(config);
		    model.setName(name);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
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
