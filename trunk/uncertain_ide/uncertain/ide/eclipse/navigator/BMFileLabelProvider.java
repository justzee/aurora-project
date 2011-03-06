package uncertain.ide.eclipse.navigator;

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.bm.BMUtil;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.LocaleMessage;

public class BMFileLabelProvider extends LabelProvider {

	public final int LimitDescLength = 15;
	public String getText(Object element) {
		IResource resource = resolveObject(element);
		if(resource != null){
			String resourceName = resource.getName();
			try {
				String text = getBMDescription(resource);
				if(text != null){
					resourceName =resourceName+ text;
				}
				return resourceName;
			} catch (ApplicationException e) {
				CustomDialog.showErrorMessageBox(e);
				return resourceName;
			}
		}
		return element == null ? "" : element.toString();//$NON-NLS-1$
	}

	public Image getImage(Object element) {
		IResource resource = resolveObject(element);
		try {
			if(isHasOperation(resource)){
				return Activator.getImageDescriptor(LocaleMessage.getString("sql.icon")).createImage();
			}
		} catch (ApplicationException e) {
			CustomDialog.showErrorMessageBox(e);
		}
		return Activator.getImageDescriptor(LocaleMessage.getString("bm.icon")).createImage();
	}
	private IResource resolveObject(Object element){
		IResource resource = null;
		if (element instanceof BMFile) {
			BMFile file = (BMFile) element;
			resource = ResourcesPlugin.getWorkspace().getRoot().findMember(file.getPath());
		}
		if (element instanceof IResource) {
			resource = (IResource) element;
		}
		return resource;
	}
	private String getBMDescription(IResource file) throws ApplicationException{
		CompositeMap bmData = loadFromResource(file);
		String bmDesc = BMUtil.getBMDescription(bmData);
		if(bmDesc == null)
			return null;
		if(LimitDescLength>=bmDesc.length())
			return formatDesc(bmDesc);
		else{
			return formatDesc(bmDesc.substring(0, LimitDescLength-3)+"...");
		}
	}
	private boolean isHasOperation(IResource file) throws ApplicationException{
		String OperationsNode = "operations";
		CompositeMap bmData = loadFromResource(file);
		return bmData.getChild(OperationsNode)!=null;
	}

	private CompositeMap loadFromResource(IResource file) throws ApplicationException {
		if(file == null || !file.exists()){
			return null;
		}
		String fullLocationPath =file.getLocation().toOSString();
		CompositeLoader cl = CompositeLoader.createInstanceForOCM();
		cl.setSaveNamespaceMapping(true);
		CompositeMap bmData;
		try {
			bmData = cl.loadByFile(fullLocationPath);
		} catch (IOException e) {
			throw new ApplicationException("文件路径"+fullLocationPath+"不存在!",e);
		} catch (SAXException e) {
			throw new ApplicationException("文件"+fullLocationPath+"格式不正确!",e);
		}
		return bmData;
	}
	private String formatDesc(String desc){
		if(desc == null)
			return null;
		else{
			return " {"+desc+"}";
		}
	}
}