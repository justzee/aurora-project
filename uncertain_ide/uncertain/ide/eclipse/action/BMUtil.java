package uncertain.ide.eclipse.action;

import java.io.File;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.wizards.ProjectProperties;

public class BMUtil {

	public static CompositeMap getFields(String classPath) throws Exception{
		String fileExtension = "bm";
		String childName = "fields";
		if(classPath == null)
			return null;
		CompositeLoader loader = new CompositeLoader();
		String path = classPath.replace('.', File.separatorChar) +'.' + fileExtension;
		String fullPath = ProjectProperties.getBMBaseDir()+File.separatorChar+path;
		CompositeMap root = loader.loadByFullFilePath(fullPath);
		CompositeMap fields = root.getChild(childName);
		return fields;
	}
}
