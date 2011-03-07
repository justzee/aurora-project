package uncertain.ide.eclipse.bm;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.project.propertypage.ProjectPropertyPage;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.AuroraResourceUtil;
import uncertain.ide.help.LocaleMessage;
import aurora.ide.AuroraConstant;

public class BMUtil {

	public static CompositeMap getFields(String classPath) throws ApplicationException{
		String fileExtension = "bm";
		String childName = "fields";
		if(classPath == null)
			return null;
		CompositeLoader loader = new CompositeLoader();
		String path = classPath.replace('.', File.separatorChar) +'.' + fileExtension;
		String fullPath = ProjectPropertyPage.getBMBaseLocalDir(AuroraResourceUtil.getIProjectFromSelection())+File.separatorChar+path;
		CompositeMap root;
		try {
			root = loader.loadByFullFilePath(fullPath);
		} catch (IOException e) {
			throw new ApplicationException("文件路径"+fullPath+"不正确!",e);
		} catch (SAXException e) {
			throw new ApplicationException("文件"+fullPath+"解析不正确!",e);
		}
		CompositeMap fields = root.getChild(childName);
		return fields;
	}
	public static String getBMDescription(IResource file) throws ApplicationException{
		if(file == null)
			return null;
		CompositeMap bm = AuroraResourceUtil.loadFromResource(file);
		final String bmDescNodeName = "descripiton";
		if(bm == null)
			return null;
		if (!bm.getQName().equals(AuroraConstant.ModelQN)){
			throw new ApplicationException("文件:"+file.getFullPath().toOSString()+"的"+LocaleMessage.getString("this.root.element.is.not") + AuroraConstant.ModelQN+ " !");
		}
		CompositeMap bmCm = bm.getChild(bmDescNodeName);
		if(bmCm != null){
			return bmCm.getText();
		}
		return null;
	}
}
