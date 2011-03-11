package uncertain.ide.eclipse.bm;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.project.propertypage.ProjectPropertyPage;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.AuroraResourceUtil;
import uncertain.ide.help.LocaleMessage;
import aurora.ide.AuroraConstant;

public class BMUtil {
	public static String BMPrefix = "bm";
	public static String FeaturesUri = "aurora.database.features";
	public static String FeaturesPrefex = "f";
	public static String OracleUri = "aurora.database.local.oracle";
	public static String OraclePrefex = "ora";
	public static IResource getBMFromClassPath(String classPath) throws ApplicationException{
		if(classPath == null)
			return null;
		String path = classPath.replace('.', File.separatorChar) +'.' + AuroraConstant.BMFileExtension;
		String fullPath = ProjectPropertyPage.getBMBaseDir(AuroraResourceUtil.getIProjectFromSelection())+File.separatorChar+path;
		IResource file = ResourcesPlugin.getWorkspace().getRoot().findMember(fullPath);
		return file;
	}
	public static String getBMDescription(IResource file) throws ApplicationException{
		if(file == null)
			return null;
		CompositeMap bm = AuroraResourceUtil.loadFromResource(file);
		final String bmDescNodeName = "descripiton";
		if(bm == null)
			return null;
		if (!bm.getQName().getLocalName().equals(AuroraConstant.ModelQN.getLocalName())){
			throw new ApplicationException("文件:"+file.getFullPath().toOSString()+"的"+LocaleMessage.getString("this.root.element.is.not") + AuroraConstant.ModelQN+ " !");
		}
		CompositeMap bmCm = bm.getChild(bmDescNodeName);
		if(bmCm != null){
			return bmCm.getText();
		}
		return null;
	}
}
