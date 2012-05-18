package aurora.ide.navigator;


import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.bm.BMUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.LocaleMessage;

public class BMFileLabelProvider extends LabelProvider 
//implements ICommonLabelProvider 
{

	public BMFileLabelProvider() {
		super();
	}
	public final int LimitDescLength = 15;
	public String getText(Object element) {
		IResource resource = resolveObject(element);
		if(resource != null){
			String resourceName = resource.getName();
			if(!resourceName.toLowerCase().endsWith("."+AuroraConstant.BMFileExtension)){
				return resourceName;
			}
			try {
				String text = getBMDescription(resource);
				if(text != null){
					resourceName =resourceName+ text;
				}
				return resourceName;
			} catch (final ApplicationException e) {
				return resourceName;
			}
		}
		return element == null ? "" : element.toString();//$NON-NLS-1$
	}

	public Image getImage(Object element) {
		IResource resource = resolveObject(element);
		if(resource == null)
			return null;
		if(resource instanceof IFolder){
			return  AuroraPlugin.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER).createImage();
		}
		String resourceName = resource.getName();
		if(!resourceName.toLowerCase().endsWith("."+AuroraConstant.BMFileExtension)){
			return  AuroraPlugin.getImageDescriptor(ISharedImages.IMG_OBJ_FILE).createImage();
		}
		try {
			if(isHasOperation(resource)){
				return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("sql.icon")).createImage();
			}
		} catch (final ApplicationException e) {
			return  AuroraPlugin.getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK).createImage();
		}
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("bm.icon")).createImage();
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
		String bmDesc = BMUtil.getBMDescription(file);
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
		CompositeMap bmData = AuroraResourceUtil.loadFromResource(file);
		if(bmData == null)
			return false;
		return bmData.getChild(OperationsNode)!=null;
	}
	private String formatDesc(String desc){
		if(desc == null)
			return null;
		else{
			return " {"+desc+"}";
		}
	}
}