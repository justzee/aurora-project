/**
 * 
 */
package uncertain.ide.util;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class Common {

	public static String getIfileLocalPath(IFile ifile) {
		String fileFullPath = ifile.getLocation().toOSString();
		return fileFullPath;
	}
	public static IResource getIResourceSelection() {
		return getIResourceFromIStructuredSelection(getStructuredSelection());
	}
	public static IResource getIResourceFromIStructuredSelection(IStructuredSelection selection) {
		if(selection == null)
			return null;
		StructuredSelection currentSelection = new StructuredSelection(IDE
				.computeSelectedResources(selection));
		Iterator it = currentSelection.iterator();
		if (it.hasNext()) {
			Object object = it.next();
			IResource selectedResource = null;
			if (object instanceof IResource) {
				selectedResource = (IResource) object;
			} else if (object instanceof IAdaptable) {
				selectedResource = (IResource) ((IAdaptable) object)
						.getAdapter(IResource.class);
			}
			if (selectedResource != null) {
				if (selectedResource.getType() == IResource.FILE) {
					selectedResource = selectedResource.getParent();
				}
				if (selectedResource.isAccessible()) {
					return selectedResource;
				}
			}
		}
		return null;
	}
	public static IStructuredSelection getStructuredSelection(){
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;
        ISelection selection = window.getSelectionService().getSelection();
        IStructuredSelection selectionToPass = StructuredSelection.EMPTY;
        if (selection instanceof IStructuredSelection) {
        	selectionToPass = (IStructuredSelection)selection;
        }else{
            // Build the selection from the IFile of the editor
            IWorkbenchPart part = window.getPartService().getActivePart();
            if (part instanceof IEditorPart) {
                IEditorInput input = ((IEditorPart) part).getEditorInput();
                Class fileClass = IFile.class;
                if (input != null && fileClass != null) {
                    Object file = Platform.getAdapterManager().getAdapter(input, fileClass);
                    if (file != null) {
                        selectionToPass = new StructuredSelection(file);
                    }
                }
            }
        }
        return selectionToPass;
	}
}
