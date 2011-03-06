package uncertain.ide.eclipse.navigator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.CustomDialog;


/**
 * @author chenxm21
 * 
 */
public class BMFileContentProvider implements ITreeContentProvider,IResourceChangeListener, IResourceDeltaVisitor {
	private  StructuredViewer viewer;
	private IProject project; 
	private static final Object[]  NO_CHILD = new Object[0]; 
	public BMFileContentProvider() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.POST_CHANGE);
	}

	public Object[] getChildren(Object parentElement) {
		if(project == null && parentElement instanceof IResource){
			project = ((IResource)parentElement).getProject();
		}
		if(parentElement instanceof IContainer){
			try {
				Object[] bmFiles = BMHierarchyCache.getInstance().getBMFilesFromResources(((IContainer)parentElement).members());
				return bmFiles;
			} catch (CoreException e) {
				CustomDialog.showErrorMessageBox(e);
				return NO_CHILD;
			} catch (ApplicationException e) {
				CustomDialog.showErrorMessageBox(e);
				return NO_CHILD;
			}
		}
		BMFile bmLinkFile = null;
		try {
			bmLinkFile = BMHierarchyCache.getInstance().getBMLinkFile(parentElement);
		} catch (ApplicationException e) {
			CustomDialog.showErrorMessageBox(e);
			return null;
		}
		if(bmLinkFile == null)
			return NO_CHILD;
		return bmLinkFile.getSubBMFiles().toArray();
	}

	public Object getParent(Object element) {
		if(element instanceof BMFile){
			BMFile file = (BMFile)element;
			try {
				IPath parentPath = file.getParentBMPath();
				if(parentPath != null)
					return BMHierarchyCache.getInstance().getBMLinkFile(parentPath);
				else{
					return ResourcesPlugin.getWorkspace().getRoot().findMember(parentPath);
				}
			} catch (ApplicationException e) {
				CustomDialog.showErrorMessageBox(e);
				return null;
			}
		}
		if (element instanceof IResource){
			return ((IResource) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof IContainer){
			try {
				return ((IContainer)element).members().length>0;
			} catch (CoreException e) {
				CustomDialog.showErrorMessageBox(e);
				return false;
			}
		}
		BMFile bmLinkFile = null;
		try {
			bmLinkFile = BMHierarchyCache.getInstance().getBMLinkFile(element);
		} catch (ApplicationException e) {
			CustomDialog.showErrorMessageBox(e);
			return false;
		}
		if(bmLinkFile == null)
			return false;
		return bmLinkFile.getSubBMFiles().size()>0;
	}

	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof IContainer){
			try {
				return ((IContainer)inputElement).members();
			} catch (CoreException e) {
				CustomDialog.showErrorMessageBox(e);
				return NO_CHILD;
			}
		}
		BMFile bmLinkFile = null;
		try {
			bmLinkFile = BMHierarchyCache.getInstance().getBMLinkFile(inputElement);
		} catch (ApplicationException e) {
			CustomDialog.showErrorMessageBox(e);
			return null;
		}
		if(bmLinkFile == null)
			return NO_CHILD;
		return bmLinkFile.getSubBMFiles().toArray();
		 
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	public void inputChanged(Viewer aViewer, Object oldInput, Object newInput) {
		viewer = (StructuredViewer) aViewer;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			delta.accept(this);
		} catch (CoreException e) {
			CustomDialog.showErrorMessageBox(e);
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		BMHierarchyCache.getInstance().removeInitProject(project);
		try {
			BMHierarchyCache.getInstance().initProject(project);
		} catch (ApplicationException e) {
			CustomDialog.showErrorMessageBox(e);
		}
		viewer.getControl().getDisplay().syncExec(new Runnable() {
			public void run() {
				viewer.refresh();
				
			}
		});
		return false;
//		IResource  resource =  delta.getResource();
//		if(resource instanceof IFile){
//			IFile file = (IFile)resource;
//			if(file.getName().toLowerCase().endsWith("."+AuroraConstant.BMFileExtension)){
//				BMHierarchyCache instance = BMHierarchyCache.getInstance();
//				BMLinkFile linkFile = instance.searchBMLinkFile(file);
//			}
//		}else{
//			if(resource instanceof IFile){
//				
//			}
//		}
//		return false;
	}
}
