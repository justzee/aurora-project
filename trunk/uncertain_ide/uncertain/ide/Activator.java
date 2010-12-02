package uncertain.ide;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import uncertain.ide.eclipse.editor.widgets.CustomDialog;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "uncertain_ide";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static void openFileInEditor(IFile file, String id) {
		IWorkbenchWindow iwb = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (iwb == null)
			return;
		IWorkbenchPage wp = iwb.getActivePage();
		if (wp == null)
			return;
		try {
			wp.openEditor(new FileEditorInput(file), id);
		} catch (PartInitException e) {
			CustomDialog.showExceptionMessageBox(e);
		}
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	public static IFile getActiveIFile(){
		IEditorInput input = getActivePage().getActiveEditor().getEditorInput();
		IFile ifile = ((IFileEditorInput) input).getFile();
		return ifile;
	}
	public static IWorkbenchPage getActivePage(){
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
	}
	
	public static void main(String[] args) {

	}
}
