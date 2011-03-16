package uncertain.ide.eclipse.navigator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;

import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.bm.BMUtil;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.AuroraResourceUtil;
import uncertain.ide.help.LocaleMessage;
import aurora.ide.AuroraConstant;

public class BMFileLabelProvider extends LabelProvider {

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
//				CustomDialog.showErrorMessageBox(e);
				return resourceName;
			}
		}
		return element == null ? "" : element.toString();//$NON-NLS-1$
	}

	public Image getImage(Object element) {
		IResource resource = resolveObject(element);
		if(resource == null)
			return null;
		String resourceName = resource.getName();
		if(!resourceName.toLowerCase().endsWith("."+AuroraConstant.BMFileExtension)){
			return  Activator.getImageDescriptor(ISharedImages.IMG_OBJ_FILE).createImage();
		}
		try {
			if(isHasOperation(resource)){
				return Activator.getImageDescriptor(LocaleMessage.getString("sql.icon")).createImage();
			}
		} catch (final ApplicationException e) {
//			CustomDialog.showErrorMessageBox(e);
			return  Activator.getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK).createImage();
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